# AfterSports — API de agendamentos de aulas esportivas

API simples em **Spring Boot 3** para gerenciar **instrutores**, **aulas** e **reservas**, com integração a **Open-Meteo** para previsão do tempo na data/local da aula.

> Foco: simplicidade, agilidade e clareza para a disciplina de Desenvolvimento para Servidores (Java + Spring).

---

## ✨ Principais recursos

* CRUD de **Instructors** e **Lessons** (relacionamento 1:N)
* **Bookings** com validação de **capacidade** da aula
* **Previsão do tempo** por aula (Open-Meteo)
* **Validações** com Jakarta Validation (DTOs `record`)
* **Swagger/OpenAPI** em `/swagger-ui.html`
* Perfis: **dev** (H2 em memória) e **test** (H2)

---

## 🏗️ Stack / Dependências

* Java 17, Maven
* Spring Boot (Web, Data JPA, Validation)
* H2 (dev e testes)
* springdoc-openapi (Swagger UI)

> **Opcional (futuro):** trocar H2 por PostgreSQL alterando o `application.yaml`.

---

## 🚀 Como rodar (dev)

1. **Build**

```bash
./mvnw -DskipTests clean package
```

2. **Start**

```bash
./mvnw spring-boot:run
# Swagger: http://localhost:8080/swagger-ui.html
```

3. **Perfil de testes (H2 em memória)**

```bash
./mvnw test
```

> Seeds de dados (alguns instrutores/aulas) são carregados por `DevDataLoader` quando **não** estiver em `test`.

---

## ⚙️ Configuração (application.yaml)

O projeto já vem pronto para **H2 em memória** no perfil padrão:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:aftersports
  jpa:
    hibernate.ddl-auto: update
```

### Usar PostgreSQL (opcional)

Altere `spring.datasource` no `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/aftersports
    username: postgres
    password: YOUR_PASSWORD
  jpa:
    hibernate.ddl-auto: update
```

E adicione a dependência do **PostgreSQL** (já presente no `pom.xml` como `runtime`).

---

## 🔌 Integração externa: Open-Meteo

* Cliente: `OpenMeteoClient` usando `RestClient`
* Endpoint interno: `GET /api/lessons/{id}/weather`
* Retorno: `WeatherSummary { date, temperatureMax, temperatureMin, precipitationProbability, summary }`

---

## 📚 Endpoints principais

Swagger em: **`/swagger-ui.html`**

### Instructors

* `POST /api/instructors` — cria
* `GET /api/instructors` — lista
* `GET /api/instructors/{id}` — busca por id
* `PUT /api/instructors/{id}` — atualiza
* `DELETE /api/instructors/{id}` — remove

### Lessons

* `POST /api/lessons` — cria
* `GET /api/lessons` — lista
* `GET /api/lessons/{id}` — busca por id
* `PUT /api/lessons/{id}` — atualiza
* `DELETE /api/lessons/{id}` — remove
* `GET /api/instructors/{instructorId}/lessons` — lista por instrutor
* `GET /api/lessons/{id}/weather` — previsão do tempo (Open-Meteo)

### Bookings

* `POST /api/bookings` — cria reserva (valida capacidade)
* `GET /api/lessons/{lessonId}/bookings` — lista reservas por aula
* `GET /api/bookings/search?email={email}` — lista reservas por e-mail do aluno

---

## 🧪 Exemplos (cURL)

> Dica: copie/cole no terminal após subir o app.

**Criar instrutor**

```bash
curl -s http://localhost:8080/api/instructors \
  -H 'Content-Type: application/json' \
  -d '{"name":"Ana Souza","sport":"SURF","bio":"Instrutora experiente"}'
```

**Criar aula** (ajuste `instructorId` com o retorno acima)

```bash
curl -s http://localhost:8080/api/lessons \
  -H 'Content-Type: application/json' \
  -d '{
    "instructorId":1,
    "title":"Aula de Surf - Iniciantes",
    "description":"Primeiro contato com o mar",
    "dateTime":"2025-12-12T08:00:00",
    "durationMinutes":90,
    "capacity":5,
    "priceCents":12000,
    "lat":-23.993,
    "lon":-46.307
  }'
```

**Previsão do tempo da aula**

```bash
curl -s http://localhost:8080/api/lessons/1/weather
```

**Criar reserva**

```bash
curl -s http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{"lessonId":1,"studentName":"Renan","studentEmail":"renan@exemplo.com"}'
```

**Listar reservas da aula**

```bash
curl -s http://localhost:8080/api/lessons/1/bookings
```

---

## 🧰 Estrutura (alto nível)

```
src/main/java/com/aftersports/aftersports
├── domain
│   ├── model/ (JPA entities)
│   ├── repo/  (JpaRepository)
│   └── service/ (regras de negócio + integração Open-Meteo)
├── infra
│   ├── config/ (CORS, seed dev)
│   └── external/weather/ (OpenMeteoClient, WeatherSummary)
└── web
    ├── controller/ (REST endpoints)
    ├── dto/ (records de request/response + validation)
    └── error/ (handler e exceções)
```

---

## ❗ Tratamento de erros

* `@RestControllerAdvice` (`ApiExceptionHandler`) para:

  * `NotFoundException` → 404
  * Validação (`MethodArgumentNotValidException`) → 400 com mapa de erros
  * `IllegalArgumentException` → 400 (ex.: capacidade da aula)
  * Demais exceções → 500 com `detail`

---

## 🧭 Convenções & boas práticas

* DTOs como **records**
* **Services** fazem as regras (ex.: `ensureCapacity`)
* **Controllers** finos (apenas orquestram)
* **Validation** nas DTOs (`@NotBlank`, `@Positive`, etc.)
* **UTC** como timezone (Jackson + Hibernate)

---

## 🧪 Testes

* **Perfil `test`** usa `application-test.yml` (H2, `ddl-auto=create-drop`)
* Teste básico de contexto: `AfterSportsApplicationTests`

Rodar:

```bash
./mvnw test
```

---

## 📦 Build e empacotamento

```bash
./mvnw -DskipTests clean package
java -jar target/aftersports-0.0.1-SNAPSHOT.jar
```

---

## 📌 Roadmap curto (opcional)

* Filtro de aulas por período/local/esporte
* Paginação de listagens
* Autenticação (JWT) para rotas de escrita
* Trocar H2 por PostgreSQL em dev/prod

---

## 👥 Autores / Créditos

Projeto acadêmico — FATEC (Desenvolvimento para Servidores).
Stack e código base por **Renan** (AfterSports).
