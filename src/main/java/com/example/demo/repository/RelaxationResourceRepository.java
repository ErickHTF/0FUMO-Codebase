package com.example.demo.repository;

import com.example.demo.entity.RelaxationResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelaxationResourceRepository extends JpaRepository<RelaxationResource, Long> {

    List<RelaxationResource> findByTriggerIgnoreCase(String trigger);
}
