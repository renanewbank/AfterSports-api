# AfterSports API (Spring Boot)

API REST para gestão de **aulas esportivas**, com **arquitetura em camadas (MVC)**, **banco relacional (JPA/H2/PostgreSQL)**, **JWT **, e **integração com API externa de clima (Open-Meteo)**. Este backend é consumido pelo frontend React do repositório `aftersports-web`.

> **Repositórios**
>
> * Backend (este): `AfterSports-api`
> * Frontend: `AfterSports-web` (Vite + React + TS)

---

## 1) Objetivo do projeto

Atender aos requisitos da disciplina **Desenvolvimento de Software II** com um sistema web completo de **agendamento de aulas**:

* CRUD de **Instrutores**, **Aulas** e **Reservas**
* Relacionamentos 1:N (Instrutor→Aula, Aula→Reserva)
* Consulta de **previsão do tempo** para a data/local da aula (Open-Meteo)
* **Autenticação JWT** para APIs REST
* Documentação via **Swagger/OpenAPI**

---

## 2) Arquitetura (MVC em camadas)

```
com.aftersports.aftersports
├─ web/               # Controllers REST + DTOs + handlers de erro
├─ domain/
│  ├─ model/          # Entidades JPA (Instructor, Lesson, Booking, User)
│  ├─ repo/           # Repositórios Spring Data JPA
│  └─ service/        # Regras de negócio (InstructorService, LessonService, ...)
├─ infra/
│  ├─ config/         # Configurações (CORS, Swagger, seeder Dev, JWT @ConfigurationProperties)
│  ├─ external/       # Clientes externos (OpenMeteoClient)
│  └─ security/       # JwtService (assina/valida tokens)
└─ AfterSportsApplication.java
```

### Entidades e relacionamentos

* **Instructor (1) — (N) Lesson**
* **Lesson (1) — (N) Booking**
* **User** (para autenticação/autorização – opcional no REST)

---

## 3) Stack

* **Java 17**, **Spring Boot 3.3**
* **Spring Web, Spring Data JPA, Validation**
* **Banco**: H2 em memória (dev/test) e **PostgreSQL** (produção/local)
* **OpenAPI/Swagger UI**
* **JWT** (opcional) com propriedades em `application.yaml`

---

## 4) Como executar

### 4.1 Executar com H2 (recomendado para começar)

Sem configurar banco externo — sobe com H2 em memória.

```bash
./mvnw spring-boot:run
```

Swagger: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### 4.2 Executar com PostgreSQL

Crie apenas o **database vazio** (as tabelas são criadas/atualizadas pelo Hibernate):

```bash
createdb aftersports        # ou via pgAdmin
```

Ajuste `spring.datasource.*` no `application.yaml` (já apontando para `jdbc:postgresql://localhost:5432/aftersports`) e rode:

```bash
./mvnw spring-boot:run
```

> **Observação:** usamos `spring.jpa.hibernate.ddl-auto=update`. 

---

## 5) Configuração (JWT e seeder opcional de admin)

As chaves abaixo ficam em `application.yaml` sob o prefixo **`app.*`** (propriedades **custom** da aplicação):

```yaml
app:
  jwt:
    secret: ${JWT_KEY:dev-secret-key-change-me-please-1234567890}
    expiration-minutes: ${JWT_EXPIRATION:240}
  admin:
    email: ${ADMIN_EMAIL:}
    password: ${ADMIN_PASSWORD:}
```

* Em **dev**, você pode não setar nada (usa os defaults).
* Em **prod**, **defina `JWT_KEY` com ≥ 32 chars** e (opcional) `ADMIN_EMAIL`/`ADMIN_PASSWORD` para semear um usuário **ADMIN** na 1ª execução:

```bash
JWT_KEY='uma-chave-grande-e-secreta-012345...' \
ADMIN_EMAIL='admin@exemplo.com' \
ADMIN_PASSWORD='senha-forte' \
./mvnw spring-boot:run
```

---

## 6) Endpoints principais

### Instrutores

* `POST /api/instructors` – cria
* `GET /api/instructors` – lista
* `GET /api/instructors/{id}` – detalha
* `PUT /api/instructors/{id}` – atualiza
* `DELETE /api/instructors/{id}` – remove

### Aulas

* `POST /api/lessons` – cria
* `GET /api/lessons` – lista
* `GET /api/lessons/{id}` – detalha
* `PUT /api/lessons/{id}` – atualiza
* `DELETE /api/lessons/{id}` – remove
* `GET /api/instructors/{instructorId}/lessons` – aulas por instrutor
* `GET /api/lessons/{id}/weather` – **integração externa** (Open-Meteo) para previsão do dia/local da aula

### Reservas

* `POST /api/bookings` – cria (valida capacidade)
* `GET /api/lessons/{lessonId}/bookings` – lista por aula
* `GET /api/bookings/search?name=Renan` – busca por nome do aluno
* `DELETE /api/bookings/{id}` – cancela reserva

### Autenticação (opcional no REST)

* `POST /api/auth/register` – cadastra usuário e retorna `{ token, user }`
* `POST /api/auth/login` – autentica e retorna `{ token, user }`
* `GET /api/auth/me` – retorna usuário atual (header `Authorization: Bearer <token>`)

> **Swagger/OpenAPI:** documentação interativa em `/swagger-ui/index.html`.

---

## 7) Integração com API Externa (Open-Meteo)

O endpoint `GET /api/lessons/{id}/weather` chama o **Open-Meteo** para obter **temperatura máxima/mínima e probabilidade de precipitação** para a **data** e **coordenadas** da aula.
Cliente: `infra/external/weather/OpenMeteoClient.java`.

---

## 8) Exemplos (curl)

Criar instrutor:

```bash
curl -s http://localhost:8080/api/instructors \
  -H 'Content-Type: application/json' \
  -d '{"name":"Ana Souza","sport":"SURF","bio":"Instrutora"}'
```

Criar aula:

```bash
curl -s http://localhost:8080/api/lessons \
  -H 'Content-Type: application/json' \
  -d '{
    "instructorId":1,"title":"Aula de Surf - Iniciante",
    "dateTime":"2025-12-12T08:00:00","durationMinutes":90,
    "capacity":6,"priceCents":12000,"lat":-23.993,"lon":-46.307
  }'
```

Previsão do tempo da aula:

```bash
curl -s http://localhost:8080/api/lessons/1/weather
```

Criar reserva:

```bash
curl -s http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{"lessonId":1,"studentName":"Renan","studentEmail":"renan@exemplo.com"}'
```

---

## 9) Frontend 

O frontend **React (Vite + TS)** está no repositório `AfterSports-web` e **consome esta API**.
Config de proxy local: `vite.config.ts` → `'/api' -> 'http://localhost:8080'`.

Páginas: **Home**, **Instrutores**, **Aulas**, **Detalhe da Aula (com reservas + clima)**, **Reservas (busca/cancelamento)**, **Login/Registro** (quando Auth habilitado).

---

## 10) Checklist dos requisitos

1. **Arquitetura em camadas (MVC)** ✔️ `web` / `domain` / `infra`
2. **Backend Spring Boot** ✔️
3. **Persistência JPA + banco relacional** ✔️ H2 (dev/test) e PostgreSQL (opção)
4. **Frontend separado (React)** ✔️ `AfterSports-web` consumindo esta API
5. **Autenticação/autorização** (opcional no REST) ✔️ JWT com seeder de admin
6. **API REST documentada** ✔️ Swagger UI em `/swagger-ui/index.html` + README
7. **Integração com API externa** ✔️ Open-Meteo no endpoint `/lessons/{id}/weather`


---

## 11) Rodando testes

Há testes básicos de contexto. Para adicionar testes de serviço/repos, usar `@DataJpaTest`/`@SpringBootTest`.

```bash
./mvnw -DskipTests=false test
```
---

## 12) Troubleshooting

* **JWT secreto inválido**: defina `JWT_KEY` com 32+ chars.
* **Sem admin**: faltou `ADMIN_EMAIL`/`ADMIN_PASSWORD`; adicione e reinicie (o seeder cria 1 vez).
* **Postgres**: garanta que o **database** exista e as credenciais em `spring.datasource.*` estejam corretas.
* **CORS**: `CorsConfig` libera `/api/**` para métodos GET/POST/PUT/DELETE.

---

## 13) Licença

Uso acadêmico/educacional. 
---

### Autor do projeto

* César Borba
* Renan Ewbank
