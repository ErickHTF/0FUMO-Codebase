package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.exception.EmailAlreadyRegisteredException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyRegisteredException(dto.getEmail());
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(toUserDetails(user));
        return new AuthResponseDTO(token, UserResponseDTO.from(user));
    }

    public AuthResponseDTO login(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        String token = jwtService.generateToken(toUserDetails(user));
        return new AuthResponseDTO(token, UserResponseDTO.from(user));
    }

    public UserResponseDTO findById(Long id) {
        return UserResponseDTO.from(getUser(id));
    }

    public UserResponseDTO update(Long id, UpdateUserDTO dto) {
        User user = getUser(id);

        if (!user.getEmail().equals(dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyRegisteredException(dto.getEmail());
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return UserResponseDTO.from(userRepository.save(user));
    }

    public UserResponseDTO completeAssessment(String email, CompleteAssessmentDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        user.setCigsPerDay(dto.getCigsPerDay());
        user.setPackCostId(dto.getPackCostId());
        user.setQuitDate(LocalDateTime.now());
        user.setAssessmentCompleted(true);

        return UserResponseDTO.from(userRepository.save(user));
    }

    public UserResponseDTO updateQuitDate(String email, UpdateQuitDateDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        user.setQuitDate(LocalDate.parse(dto.getQuitDate()).atStartOfDay());
        return UserResponseDTO.from(userRepository.save(user));
    }

    // ── Insight 4: Projeção Financeira ───────────────────────────────────────

    public FinancialProjectionDTO getFinancialProjection(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        LocalDateTime start = user.getQuitDate() != null
                ? user.getQuitDate()
                : user.getCreatedAt();

        long days        = ChronoUnit.DAYS.between(start, LocalDateTime.now());
        int cigsPerDay   = user.getCigsPerDay()  != null ? user.getCigsPerDay()  : 15;
        double pricePerCig = packPrice(user.getPackCostId()) / 20.0;
        double dailySaving = cigsPerDay * pricePerCig;

        long   cigarettesAvoided = cigsPerDay * days;
        double savedAmount       = round2(cigarettesAvoided * pricePerCig);

        List<ProjectionMilestoneDTO> milestones = List.of(
                new ProjectionMilestoneDTO(30,  round2(cigsPerDay * 30  * pricePerCig)),
                new ProjectionMilestoneDTO(90,  round2(cigsPerDay * 90  * pricePerCig)),
                new ProjectionMilestoneDTO(180, round2(cigsPerDay * 180 * pricePerCig)),
                new ProjectionMilestoneDTO(365, round2(cigsPerDay * 365 * pricePerCig))
        );

        return new FinancialProjectionDTO(savedAmount, cigarettesAvoided, days,
                milestones, round2(dailySaving));
    }

    private double packPrice(String packCostId) {
        return switch (packCostId != null ? packCostId : "") {
            case "r5_8"    -> 6.5;
            case "r9_12"   -> 10.5;
            case "r13_16"  -> 14.5;
            case "r17plus" -> 19.0;
            default        -> 10.5;
        };
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(this::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
