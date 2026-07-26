# SMS Routing Service

A Spring Boot REST API that routes outbound SMS messages to carriers by phone number prefix, tracks message status, and blocks sends to opted-out numbers.

## Features

- Send and retrieve SMS messages
- Opt-out management
- Prefix-based carrier routing (AU, NZ, Global)
- Message status lifecycle: `PENDING` → `SENT` → `DELIVERED` or `BLOCKED`
- In-memory storage (no database required)

## Tech Stack

Java 17 · Spring Boot 4.1 · Maven

## Prerequisites

- Java 17+

## Quick Start

```bash
cd SMSRoutingService
./mvnw spring-boot:run
```

App runs at **http://localhost:8080**

### API Documentation

Interactive API docs (Scalar): **http://localhost:8080/scalar/**

### Quick test

```bash
# Send message (AU → Telstra on first send)
curl -X POST http://localhost:8080/messages \
  -H "Content-Type: application/json" \
  -d '{"destination_number":"+61491570156","content":"Hello","channel":"SMS"}'

# Get message status (use id from send response)
curl http://localhost:8080/messages/{id}

# Opt out a number
curl -X POST http://localhost:8080/optout/+61491570156
```

## Business Rules

**Carrier routing**

| Prefix | Carrier |
|--------|---------|
| `+61` | Telstra / Optus (alternate) |
| `+64` | Spark |
| Other | Global |

**Opt-out:** `POST /optout/{phoneNumber}` blocks future sends to that number (`status: BLOCKED`).

## API Examples

### Send message

```bash
curl -X POST http://localhost:8080/messages \
  -H "Content-Type: application/json" \
  -d '{
    "destination_number": "+61491570156",
    "content": "Hello world",
    "channel": "SMS"
  }'
```

Response (`201 Created`):

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "destination_number": "+61491570156",
  "status": "DELIVERED",
  "carrier": "Telstra"
}
```

### Get message status

```bash
curl http://localhost:8080/messages/550e8400-e29b-41d4-a716-446655440000
```

Response (`200 OK`):

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "destination_number": "+61491570156",
  "content": "Hello world",
  "channel": "SMS",
  "status": "DELIVERED",
  "carrier": "Telstra",
  "created_at": "2026-07-10T03:00:00Z",
  "updated_at": "2026-07-10T03:00:00.001Z"
}
```

### Opt out

```bash
curl -X POST http://localhost:8080/optout/+61491570156
```

Response (`200 OK`):

```json
{
  "phone_number": "+61491570156",
  "opted_out": true
}
```

Subsequent sends to that number return `status: BLOCKED` with no carrier.

## Assumptions

| Topic | Assumption |
|-------|------------|
| **Storage** | Messages and opt-outs stored in memory via `MessageRepository` and `OptOutRepository` (`ConcurrentHashMap`). Data is lost on app restart. |
| **Phone format** | Numbers must be E.164 (`+` prefix). AU local `04…` is normalized to `+61…`. Whitespace is stripped. |
| **AU validation** | `+61` followed by exactly 9 digits (11 digits total including country code). |
| **NZ validation** | `+64` followed by 8 or 9 digits (10–11 digits total including country code). |
| **Other countries** | Valid E.164 with 8–15 digits after `+`; routed to **Global**. |
| **AU carrier alternation** | Round-robin per send: Telstra → Optus → Telstra … (resets on app restart). |
| **Status lifecycle** | Every message starts `PENDING`. Successful sends transition `PENDING` → `SENT` → `DELIVERED` in one request. Opted-out numbers go `PENDING` → `BLOCKED`. The send response returns the **final** status. |
| **Blocked messages** | Still stored with a message ID so status can be retrieved via `GET /messages/{id}`. |
| **Errors** | Invalid phone → `400`. Missing message → `404`. Validation errors on request body → `400`. |

## Tests

```bash
./mvnw test                              # all tests
./mvnw test -Dtest="**/unit/**"          # unit only
./mvnw test -Dtest="**/component/**"     # component only
```

| Folder | What it tests | Spring context |
|--------|---------------|----------------|
| `src/test/.../unit/` | Validators, services (Mockito) | No |
| `src/test/.../component/` | Full HTTP API via MockMvc | Yes (`@SpringBootTest`) |

```
src/test/java/com/ludistudy/smsroutingservice/
  fixture/
    TestFixtures.java
  unit/
    validation/PhoneNumberValidatorTest.java
    service/CarrierRouterTest.java
    service/MessageServiceTest.java
    service/OptOutServiceTest.java
  component/
    SmsRoutingApiComponentTest.java
```

## Project Structure

```
src/main/java/com/ludistudy/smsroutingservice/
  controller/    REST endpoints + Scalar redirect (ApiDocsController)
  service/       Business logic (MessageService, OptOutService, CarrierRouter)
  repository/    In-memory stores (MessageRepository, OptOutRepository)
  entity/        Domain models (MessageEntity, OptOutEntity)
  model/         Enums (Carrier, MessageStatus)
  dto/           Request / response objects
  validation/    Phone number rules
  exception/     Error handling

docs/openapi.yaml          API spec (served at /openapi.yaml)
```
