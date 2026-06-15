# Checklist de Validação — 0FUMO

Executar antes de considerar qualquer tarefa concluída.

---

## Implementação × Diagrama

- [ ] As alterações em entidades batem com `diagrams/AutoCadastroUC.plantuml`?
- [ ] O fluxo de telas bate com `diagrams/frontend.plantuml`?
- [ ] A arquitetura de camadas (controller → service → repository → DTO) bate com `diagrams/ModeloArquitetura.plantuml`?
- [ ] Os casos de uso cobertos batem com `diagrams/usecase.plantuml`?
- [ ] Se a implementação mudou → o(s) diagrama(s) afetado(s) foram atualizados?
- [ ] Se um diagrama foi adicionado/alterado → a implementação foi ajustada para refletir?

---

## Auto Cadastro (UC)

- [ ] Endpoint `POST /api/auth/register` aceita `name`, `email`, `password`?
- [ ] Validação server-side presente (`@NotBlank`, `@Email`, `@Size`)?
- [ ] E-mail duplicado retorna 409?
- [ ] Senha é armazenada com BCrypt?
- [ ] `assessmentCompleted` inicia como `false`?
- [ ] Resposta inclui token JWT + `UserResponseDTO`?
- [ ] Frontend redireciona para `assessment.html` após cadastro bem-sucedido?
- [ ] Entidade `User` contém os métodos `validarEmail()`, `validarSenha()`, `validarCampos()`?

---

## Avaliação Inicial (UC)

- [ ] Endpoint `POST /api/users/{id}/assessment` aceita body com todos os campos?
- [ ] DTO `AssessmentRequestDTO` contém: `cigsPerDay`, `smokingYears`, `motivation`, `dependencyLevel`?
- [ ] Todos os campos têm validação (`@NotNull`, `@NotBlank`, `@Min`)?
- [ ] Service persiste todos os campos na entidade `User`?
- [ ] `assessmentCompleted` é setado para `true` ao concluir?
- [ ] `UserResponseDTO` expõe os campos da avaliação?
- [ ] Frontend coleta os 4 campos: cigarros/dia, anos fumando, motivação, nível de dependência?
- [ ] Frontend valida todos os campos antes de enviar?
- [ ] `api.js` envia body completo com os 4 campos?
- [ ] Após concluir, frontend atualiza `localStorage` e redireciona para `dashboard.html`?
- [ ] `dashboard.html` redireciona para `assessment.html` se `assessmentCompleted === false`?
- [ ] `assessment.html` redireciona para `dashboard.html` se já concluído?

---

## Segurança / Acesso

- [ ] Rotas `/api/auth/**` são públicas?
- [ ] Demais rotas `/api/**` exigem JWT válido?
- [ ] Recursos estáticos (`*.html`, `/css/**`, `/js/**`) são públicos?

---

## Monitorar Progresso (UC)

- [ ] Endpoint `GET /api/progress` retorna estatísticas do usuário autenticado?
- [ ] DTO `ProgressStatsDTO` contém: `totalEvents`, `eventsByType`, `eventsByIntensity`, `eventsByTrigger`, `lastEventAt`, `daysTracking`?
- [ ] Service valida se avaliação inicial foi concluída (403 se não)?
- [ ] Service agrega eventos por tipo, intensidade e gatilho?
- [ ] Service calcula dias de acompanhamento desde o primeiro evento?
- [ ] Endpoint exige JWT válido?
- [ ] Diagrama `MonitorarProgressoCD.plantuml` reflete a implementação?
- [ ] `ModeloArquitetura.plantuml` inclui `ProgressController` → `ProgressService`?

---

## Sugerir Recursos de Relaxamento (UC)

- [ ] Entidade `RelaxationResource` contém: `id`, `title`, `description`, `category`, `trigger`?
- [ ] Endpoint `GET /api/relaxation-resources` lista todos os recursos?
- [ ] Endpoint `GET /api/relaxation-resources?trigger=X` filtra por gatilho?
- [ ] DTO `RelaxationResourceResponseDTO` contém todos os campos da entidade?
- [ ] Service valida se avaliação inicial foi concluída (403 se não)?
- [ ] `data.sql` popula recursos iniciais sem duplicar (idempotente)?
- [ ] Diagrama `SugerirRecursosCD.plantuml` reflete a implementação?
- [ ] `ModeloArquitetura.plantuml` inclui `RelaxationResourceController` → `RelaxationResourceService`?
- [ ] `POST /api/events` continua retornando `EventResponseDTO` (sem breaking change)?

---

## Geral

- [ ] Nenhum campo novo na entidade sem coluna correspondente no banco (Hibernate auto-update)?
- [ ] Nenhum campo coletado no frontend sem ser enviado ao backend?
- [ ] Nenhum campo recebido no backend sem ser persistido?
