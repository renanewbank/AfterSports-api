Você é um desenvolvedor sênior. Gere o código **direto** (sem explicações) para completar o meu projeto Spring Boot já criado. Use **apenas** as dependências que já existem no `pom.xml`. Stack atual:

* Java 17, Spring Boot **3.3.5**
* Starters: `web`, `data-jpa`, `validation`, `springdoc-openapi-starter-webmvc-ui`
* DB: H2 em dev/test (já configurado), Postgres em runtime (não usar agora)
* Test profile já existe (`application-test.yml`)
* Pacote base: `com.aftersports.aftersports`
* Objetivo da disciplina: **API REST** em camadas (Controller/Service/Repository), **CRUD de ao menos 2 entidades com relacionamento 1:N**, **integração com API externa** (clima), **Swagger/OpenAPI**. Autenticação é **opcional**; **não** implementar para manter simplicidade.

## Tema

**AfterSports** — plataforma simples de agendamento de **aulas esportivas** (ex.: surf, tênis, funcional). Consumir **Open-Meteo** (sem chave) para enriquecer consultas com previsão resumida.

## Regras de implementação

* **Não** adicionar dependências novas.
* Usar **JPA/Hibernate** com `ddl-auto: update`.
* Usar **Jakarta Validation** nas DTOs.
* Usar **RestClient** do Spring para HTTP na integração externa.
* Retornar **DTOs (records)** nos controllers; entity nunca no payload.
* Mapear timezone como **UTC** no back, datas no formato ISO-8601.
* Erros: lançar `IllegalArgumentException` ou criar `NotFoundException` + `@RestControllerAdvice` simples.
* Organização: `domain/model`, `domain/repo`, `domain/service`, `web/dto`, `web/controller`, `infra/external` (cliente Open-Meteo), `infra/config` (CORS opcional).
* **Sem** segurança/JWT.

## Modelagem mínima

Crie **três entidades** com relacionamento 1:N:

1. `Instructor`

   * `id` (Long, PK, identity)
   * `name` (String, not null)
   * `sport` (String, not null)  // ex.: SURF, TENIS, FUNCIONAL
   * `bio` (String, opcional)

2. `Lesson` (uma aula publicada para reserva)

   * `id` (Long, PK, identity)
   * `instructor` (ManyToOne, not null)
   * `title` (String, not null)        // “Aula de Surf – Iniciantes”
   * `description` (String, opcional)
   * `dateTime` (LocalDateTime, not null)
   * `durationMinutes` (Integer, not null)
   * `capacity` (Integer, not null)
   * `priceCents` (Long, not null, >=0)
   * `lat` (Double, not null)
   * `lon` (Double, not null)

3. `Booking` (reserva do aluno)

   * `id` (Long, PK, identity)
   * `lesson` (ManyToOne, not null)
   * `studentName` (String, not null)
   * `studentEmail` (String, not null)
   * `createdAt` (Instant, not null, setado em `@PrePersist`)

**Regra:** não permitir criar `Booking` se `capacity` já atingida.

## Repositórios

Crie interfaces `JpaRepository`:

* `InstructorRepository`, `LessonRepository`, `BookingRepository`
* Queries úteis: `BookingRepository.countByLessonId(Long lessonId)`; `LessonRepository.findByInstructorId(Long instructorId)`

## DTOs (records)

Crie em `web/dto`:

* `InstructorDTO` (id, name, sport, bio)
* `InstructorCreateRequest` (name, sport, bio?) – validações `@NotBlank`
* `LessonDTO` (id, instructorId, title, description, dateTime, durationMinutes, capacity, priceCents, lat, lon)
* `LessonCreateRequest` / `LessonUpdateRequest` com validações (`@NotNull`, `@Positive`, `@Email` onde aplicável)
* `BookingDTO` (id, lessonId, studentName, studentEmail, createdAt)
* `BookingCreateRequest` (lessonId, studentName, studentEmail)

## Services

Em `domain/service`:

* `InstructorService`: CRUD básico.
* `LessonService`: CRUD, listar por instrutor, **método helper** `ensureCapacity(lessonId)` que compara `capacity` com `bookings`.
* `BookingService`: `create(BookingCreateRequest)` valida capacidade; métodos de consulta (por aula, por email do aluno).
* **WeatherService**: método `WeatherSummary getForecast(double lat, double lon, LocalDate date)` chamando Open-Meteo.

### Weather summary (record)

Em `infra/external/weather`:

* `WeatherSummary` (record): `LocalDate date`, `Double temperatureMax`, `Double temperatureMin`, `Double precipitationProbability`, `String summary` (monte um resumo simples).
* `OpenMeteoClient` usando `RestClient` para chamar:
  `https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max&timezone=UTC&start_date={yyyy-MM-dd}&end_date={yyyy-MM-dd}`
  Parseie o JSON em classes internas mínimas ou `Map` -> extraia o necessário.

## Controllers (REST)

Base path: `/api`.

1. `InstructorController`

   * `POST /api/instructors` (create)
   * `GET /api/instructors` (list)
   * `GET /api/instructors/{id}` (get)
   * `PUT /api/instructors/{id}` (update)
   * `DELETE /api/instructors/{id}` (delete)

2. `LessonController`

   * `POST /api/lessons` (create)
   * `GET /api/lessons` (list paginada simples via `List`)
   * `GET /api/lessons/{id}` (get)
   * `PUT /api/lessons/{id}` (update)
   * `DELETE /api/lessons/{id}` (delete)
   * `GET /api/instructors/{instructorId}/lessons` (listar por instrutor)
   * **`GET /api/lessons/{id}/weather`** → chama `WeatherService` usando `lat/lon` e `date` derivada de `dateTime` para devolver `WeatherSummary`.

3. `BookingController`

   * `POST /api/bookings` (create) → valida capacidade
   * `GET /api/lessons/{lessonId}/bookings` (listar por aula)
   * `GET /api/bookings/search?email=...` (listar por e-mail do aluno)

## Mapeamentos & validações

* Usar métodos privados nos services para converter Entity <-> DTO.
* Validar `@Email` em `studentEmail`, `@Positive` em numéricos.
* Em `LessonService.update`, só atualizar campos presentes no request.

## Exception handler

Crie `web/error/NotFoundException extends RuntimeException` e `web/error/ApiExceptionHandler` com:

* `@ExceptionHandler(NotFoundException)` → 404
* `@ExceptionHandler(MethodArgumentNotValidException)` → 400 com mapa field->message
* fallback 500 com `message` e `detail`.

## Swagger/OpenAPI

O projeto já tem `springdoc`. Garanta que os controllers tenham `@Tag` e endpoints com `@Operation`. A UI deve abrir em `/swagger-ui.html`.

## CORS (opcional)

Crie `infra/config/CorsConfig` liberando `GET,POST,PUT,DELETE`.

## Dados de exemplo (DEV somente)

Crie um `CommandLineRunner` em `infra/config/DevDataLoader` (ativado se profile **não** é `test`) para inserir:

* 2 instrutores (ex.: “Ana — SURF”, “Bruno — TENIS”)
* 3 aulas futuras (lat/lon de um lugar real)
* Sem bookings iniciais
  Isso ajuda a testar rapidamente.

## Aceite (o que preciso ao finalizar o run)

* Compilar: `./mvnw -DskipTests clean package`
* Subir: `./mvnw spring-boot:run`
* Ver no Swagger: `http://localhost:8080/swagger-ui.html`
* Testes manuais (exemplos em `curl`):

```bash
# Criar instrutor
curl -s http://localhost:8080/api/instructors -H 'Content-Type: application/json' -d '{
  "name":"Ana Souza","sport":"SURF","bio":"Instrutora"
}'

# Criar aula
curl -s http://localhost:8080/api/lessons -H 'Content-Type: application/json' -d '{
  "instructorId":1,
  "title":"Aula de Surf - Iniciantes",
  "description":"Primeiro contato",
  "dateTime":"2025-12-10T08:00:00",
  "durationMinutes":90,
  "capacity":5,
  "priceCents":12000,
  "lat":-23.992,"lon":-46.308
}'

# Clima da aula
curl -s http://localhost:8080/api/lessons/1/weather

# Criar booking
curl -s http://localhost:8080/api/bookings -H 'Content-Type: application/json' -d '{
  "lessonId":1,"studentName":"Renan","studentEmail":"renan@exemplo.com"
}'

# Listar bookings da aula
curl -s http://localhost:8080/api/lessons/1/bookings
```

**Gere todos os arquivos necessários** conforme descrito acima, dentro do pacote `com.aftersports.aftersports`, mantendo o projeto compilável **sem** adicionar novas dependências.

