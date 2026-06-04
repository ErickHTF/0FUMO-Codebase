# 0FUMO — Backend

API REST em Spring Boot para a aplicação de cessação do tabagismo.

## Pré-requisitos

- Java 21
- Docker (banco de dados)

## Como rodar

**1. Sobe o banco**

```bash
docker compose up -d
```

**2. Roda a API**

```bash
./mvnw spring-boot:run
```

A API fica disponível em `http://localhost:8080`.

## Testes

```bash
./mvnw test
```

## Endpoints

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| POST | `/api/auth/register` | Não | Cadastro |
| POST | `/api/auth/login` | Não | Login |
| POST | `/api/users/{id}/assessment` | Sim | Avaliação inicial |
| POST | `/api/events` | Sim | Registrar evento |
| GET  | `/api/events` | Sim | Listar eventos |

Rotas autenticadas exigem `Authorization: Bearer <token>` no header.
