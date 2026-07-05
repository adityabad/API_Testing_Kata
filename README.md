# API Testing Kata — Java, Rest-Assured & Cucumber

A BDD-style API test automation framework for the [automationintesting.online](https://automationintesting.online/) hotel booking playground.

The framework covers health checks, booking CRUD operations, authentication/authorization scenarios, schema validation, retry logic for transient 5xx errors, and parallel execution.

---

## Table of Contents

1. [Features](#features)
2. [Tech Stack](#tech-stack)
3. [Project Structure](#project-structure)
4. [Prerequisites](#prerequisites)
5. [Setup](#setup)
6. [Running Tests](#running-tests)
7. [GitHub Actions CI](#github-actions-ci)
8. [Configuration](#configuration)
9. [Known Bugs](#known-bugs)
10. [Known Issues & Limitations](#known-issues--limitations)
11. [Troubleshooting](#troubleshooting)

---

## Features

- **BDD with Cucumber**: Human-readable Gherkin feature files for booking CRUD and auth scenarios.
- **Booking CRUD coverage**:
  - Create booking (positive + validation-negative examples)
  - Retrieve booking by id (existing + non-existing)
  - Full update (PUT) of a booking
  - Partial update (PATCH) of a booking
  - Delete booking + verify it cannot be retrieved anymore
- **Authentication & authorization scenarios**:
  - Invalid login credentials
  - Accessing protected endpoints without a token
  - Accessing protected endpoints with an invalid/expired token
- **Health check**: Background step verifies `/booking/actuator/health` before booking scenarios.
- **Rich assertions**: Status codes, response body fields, content type, and JSON Schema validation.
- **JSON Schema validation**: All structured responses are validated against JSON schemas in `src/test/resources/schemas/`.
- **Retry mechanism**: Automatic retry (up to 3 times, 1s delay) on HTTP 5xx server errors via a Rest-Assured filter.
- **Parallel execution**: Cucumber scenarios run in parallel with 2 fixed workers.
- **Environment-based credentials**: Username/password are read from environment variables, not committed to the repo.
- **GitHub Actions CI**: Scheduled and standalone workflow with test results published directly in the GitHub Actions UI.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Maven | Build & dependency management |
| Rest-Assured | API testing library |
| Cucumber 7.22.2 | BDD framework |
| JUnit Platform Suite | Cucumber test runner |
| Lombok | Boilerplate reduction for models |
| Jackson | JSON serialization/deserialization |
| JSON Schema Validator | Response schema validation |

---

## Project Structure

```
├── .github/workflows/ci.yml      # GitHub Actions workflow
├── src/
│   ├── main/java/com/booking/
│   │   ├── clients/              # API clients (Booking, Auth, Health)
│   │   ├── models/               # POJOs for request/response bodies
│   │   ├── specs/                # Rest-Assured request specifications
│   │   ├── filters/              # Rest-Assured filters (retry)
│   │   └── utils/                # ConfigReader utility
│   ├── test/java/com/booking/
│   │   ├── stepdefinitions/      # Cucumber step definitions
│   │   ├── context/              # Shared scenario state
│   │   ├── tests/                # Standalone TestNG test (legacy)
│   │   └── TestRunner.java       # JUnit Platform Suite entry point
│   └── test/resources/
│       ├── features/             # Gherkin feature files
│       ├── schemas/              # JSON Schema files
│       ├── spec/booking.yaml     # OpenAPI spec (backup)
│       └── qa.properties         # Non-secret configuration
└── pom.xml
```

---

## Prerequisites

- JDK 17 or higher
- Maven 3.8+
- (Optional) IntelliJ IDEA or Eclipse for local development

---

## Setup

### 1. Clone the repository

```bash
git clone <repository-url>
cd API_Testing_Kata
```

### 2. Set credentials

The framework reads credentials from environment variables or Java system properties. Do **not** put them in `qa.properties`.

#### Option A: Environment variables

**Linux / macOS / Git Bash**

```bash
export BOOKING_USERNAME=admin
export BOOKING_PASSWORD=password
```

Inline:

```bash
BOOKING_USERNAME=admin BOOKING_PASSWORD=password mvn clean test
```

**Windows PowerShell**

```powershell
$env:BOOKING_USERNAME="admin"
$env:BOOKING_PASSWORD="password"
mvn clean test
```

**Windows CMD**

```cmd
set BOOKING_USERNAME=admin
set BOOKING_PASSWORD=password
mvn clean test
```

Or in one line:

```cmd
set BOOKING_USERNAME=admin && set BOOKING_PASSWORD=password && mvn clean test
```

> The expected environment variable names are `BOOKING_USERNAME` and `BOOKING_PASSWORD`.

#### Option B: Java system properties (works the same on every OS)

```bash
mvn clean test -DBOOKING_USERNAME=admin -DBOOKING_PASSWORD=password
```

You can also use the shorter names:

```bash
mvn clean test -Dusername=admin -Dpassword=password
```

### 3. Verify Maven can resolve dependencies

```bash
mvn clean compile test-compile
```

---

## Running Tests

### Run all Cucumber tests

```bash
mvn clean test
```

Make sure the credentials are set first using one of the methods above.

### Run with credentials inline

**Linux / macOS / Git Bash**

```bash
BOOKING_USERNAME=admin BOOKING_PASSWORD=password mvn clean test
```

**Windows CMD**

```cmd
set BOOKING_USERNAME=admin && set BOOKING_PASSWORD=password && mvn clean test
```

**Any OS using system properties**

```bash
mvn clean test -DBOOKING_USERNAME=admin -DBOOKING_PASSWORD=password
```

### Run the standalone TestNG test

The project also contains a standalone `BookingTest.java` that can be run directly via TestNG from an IDE, or from the command line:

```bash
BOOKING_USERNAME=admin BOOKING_PASSWORD=password mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
BOOKING_USERNAME=admin BOOKING_PASSWORD=password mvn test-compile
java -cp "target/classes;target/test-classes;$(cat cp.txt)" org.testng.TestNG -testclass com.booking.tests.BookingTest
```

### Run tests sequentially

Parallel execution is enabled by default in `TestRunner.java`. To disable it, remove or comment out these lines in `TestRunner.java`:

```java
@ConfigurationParameter(key = "cucumber.execution.parallel.enabled", value = "true")
@ConfigurationParameter(key = "cucumber.execution.parallel.config.strategy", value = "fixed")
@ConfigurationParameter(key = "cucumber.execution.parallel.config.fixed.parallelism", value = "2")
```

---

## GitHub Actions CI

The workflow in `.github/workflows/ci.yml` runs:

- On every push/PR to `main` or `kata-feature-booking`
- Daily at 06:00 UTC (`schedule`)
- Manually via `workflow_dispatch`

### Required repository secrets

Go to **Settings → Secrets and variables → Actions** and add:

| Secret | Value example |
|---|---|
| `BOOKING_USERNAME` | `admin` |
| `BOOKING_PASSWORD` | `password` |

Test results are published directly in the GitHub Actions UI using `dorny/test-reporter` — no artifact download required.

---

## Configuration

Configuration resolution order (highest priority first):

1. Environment variables prefixed with `BOOKING_` (e.g., `BOOKING_USERNAME`)
2. Java system properties prefixed with `BOOKING_` (e.g., `-DBOOKING_USERNAME=admin`)
3. Java system properties (e.g., `-Dusername=admin`)
4. `src/test/resources/qa.properties`

Edit `qa.properties` for non-sensitive settings like `base.url`, `booking.url`, `auth.url`, etc.

---

## Known Bugs

These are discrepancies between the OpenAPI spec (`booking.yaml`) and the live API at `automationintesting.online`. The test suite follows the YAML spec, so these scenarios currently fail (red) against the real service.

| # | Bug | Spec (YAML) | Actual API | Affected tests |
|---|---|---|---|---|
| 1 | Create booking status code | `200 OK` | `201 Created` | All create-booking scenarios |
| 2 | Delete booking status code | `201 Created` | `202 Accepted` with empty body | Delete booking scenario |
| 3 | Update booking response shape | Top-level `Booking` object | `{ "booking": {...}, "bookingid": ... }` | Update booking scenario |
| 4 | Partial update (PATCH) | Supported, returns `200` | `405 Method Not Allowed` | Partial update scenario |
| 5 | Lastname length validation message | `size must be between 3 and 18` | `size must be between 3 and 30` | Negative create examples |

> These failures are intentional: the suite documents the contract as specified in `booking.yaml` and highlights where the implementation diverges.

---

## Known Issues & Limitations

1. **External dependency**: The application under test is a public playground that resets every 10 minutes. Test data is not persistent, and bookings created by the suite may disappear between runs.
2. **No test data cleanup**: Created bookings are not explicitly cleaned up after each scenario. Random `roomid` values are used to reduce collisions.
3. **Parallel execution shared resource**: Because the API is a shared external service, parallel scenarios may occasionally conflict (e.g., booking id reuse, rate limiting). The retry filter mitigates transient 5xx errors but not 409 conflicts.
4. **Schema validation on empty bodies**: Some error responses (404, 403) return an empty body from the real API. Schema validation is conditionally skipped for empty bodies to avoid JSON parse exceptions.
5. **No message feature currently included**: The original message-reading scenario has been removed from the current branch; the suite focuses on booking and auth.
6. **Credentials management**: For CI, credentials must be configured as repository secrets. Running locally without environment variables will fail authentication.
7. **Java version warning**: Maven currently targets Java 17. You may see a compiler warning about `--release` on newer JDKs; this does not block execution.

---

## Troubleshooting

### `Authentication failed! Verify credentials inside your properties sheet.`

You did not set `BOOKING_USERNAME` and `BOOKING_PASSWORD` environment variables.

### `class com.booking.stepdefinitions.BookingSteps does not have a public zero-argument constructor`

This was fixed by removing constructor-based dependency injection. If you see this, ensure `cucumber-picocontainer` is not on the classpath and no `cucumber.properties` file is forcing an object factory.

### `JSON path errors doesn't match`

This is the expected red test from **Known Bug #5**. The real API returns a different validation message than the YAML spec.

### `Expected status code <200> but was <201>`

This is the expected red test from **Known Bug #1**. The real API returns `201 Created` while the spec says `200 OK`.

### Tests fail with 5xx errors

The `ServerErrorRetryFilter` automatically retries 5xx responses. If failures persist, the external service may be down or rate-limiting requests.

---

## License

This project is provided as a testing kata for educational/evaluation purposes.
