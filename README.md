# Test-Driven Development – Spring Boot

A Spring Boot project demonstrating **Test-Driven Development (TDD)** with JUnit 5, Mockito, and Testcontainers.

---

## Tech Stack

| Tool | Purpose |
|---|---|
| Java 21 | Language |
| Spring Boot 3 | Application framework |
| JUnit 5 | Test runner |
| Mockito | Mocking framework |
| AssertJ / BDDAssertions | Fluent assertions |
| Testcontainers (MySQL) | Real database integration tests |
| JaCoCo | Code coverage reporting |
| Gradle | Build tool |

---

## Project Structure

```
src/
 ├── main/java/org/testdrivendevelopment/
 │    ├── clients/         PaymentClient
 │    ├── dtos/            CreateOrderRequest, OrderDto
 │    ├── entites/         Order
 │    ├── repositories/    OrderRepository
 │    └── services/        OrderService
 └── test/java/org/testdrivendevelopment/
      ├── TestDrivenDevelopmentApplicationTests.java   (context load test)
      ├── repositories/OrderRepositoryTest.java        (JPA / Testcontainers)
      └── services/OrderServiceTests.java              (unit test with mocks)
```

---

## Gradle Commands

### Run all tests
```bash
./gradlew test
```
Runs every test class found under `src/test/`. After the tests finish, JaCoCo automatically generates a coverage report (configured via `finalizedBy jacocoTestReport` in `build.gradle`).

### Run tests + generate coverage report explicitly
```bash
./gradlew test jacocoTestReport
```
The HTML report is written to:
```
build/reports/jacoco/test/html/index.html
```

### Run a single test class
```bash
./gradlew test --tests "org.testdrivendevelopment.services.OrderServiceTests"
```

### Run a single test method
```bash
./gradlew test --tests "org.testdrivendevelopment.services.OrderServiceTests.it_should_fail_order_creation_when_payment_failed"
```

### Build without running tests
```bash
./gradlew build -x test
```

### Clean build outputs
```bash
./gradlew clean
```

### Full clean build with tests
```bash
./gradlew clean build
```

---

## Test Flow

TDD follows the **Red → Green → Refactor** cycle:

1. **Red** – Write a failing test first (the feature doesn't exist yet).
2. **Green** – Write the minimum production code to make the test pass.
3. **Refactor** – Clean up the code while keeping all tests green.

---

## Test Layers

### 1. Context Load Test – `TestDrivenDevelopmentApplicationTests`

```java
@SpringBootTest
class TestDrivenDevelopmentApplicationTests {

    @Test
    void contextLoads() { }
}
```

- Annotated with `@SpringBootTest`, which boots the full application context.
- Verifies that the Spring context starts without errors.
- Acts as a smoke test for wiring and configuration.

---

### 2. Repository / Integration Test – `OrderRepositoryTest`

```java
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest { ... }
```

**What it does**

- `@DataJpaTest` loads only the JPA slice of the application context (entities, repositories, `TestEntityManager`).
- `@AutoConfigureTestDatabase(replace = NONE)` prevents Spring from swapping in an embedded database (H2). This forces the test to use the real database provided by Testcontainers.
- A real **MySQL 8.0** container is spun up automatically via:
  ```java
  @Container
  public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0");
  ```
- `@DynamicPropertySource` wires the container's JDBC URL, credentials, and driver into Spring's datasource configuration at runtime.

**Test method: `it_should_find_order`**

Flow:
1. **Given** – Two `Order` objects are persisted directly through `TestEntityManager` and flushed to the database.
2. **When** – `orderRepository.findAll()` is called to retrieve all orders.
3. **Then** – The result list is verified to be non-empty and the IDs of both orders match what was persisted.

---

### 3. Service Unit Test – `OrderServiceTests`

```java
@ExtendWith(MockitoExtension.class)
public class OrderServiceTests { ... }
```

**What it does**

- `@ExtendWith(MockitoExtension.class)` activates the Mockito test extension without loading a Spring context, making these tests fast.
- Dependencies of `OrderService` are replaced with mocks:
  ```java
  @Mock
  private OrderRepository orderRepository;

  @Mock
  private PaymentClient paymentClient;

  @InjectMocks
  private OrderService orderService;  // mocks are injected automatically
  ```

---

**Test method: `it_should_create_a_new_orders` (Parameterized)**

```java
@ParameterizedTest
@MethodSource("order_requests")
public void it_should_create_a_new_orders(String productCode, Integer amount,
                                           BigDecimal unitPrice, BigDecimal totalPrice) { ... }
```

- `@ParameterizedTest` + `@MethodSource` runs the same test logic with multiple inputs, avoiding duplicated test methods.
- The data provider `order_requests()` supplies two scenarios:
  | customerCode | amount | unitPrice | expectedTotal |
  |---|---|---|---|
  | code1 | 5 | 19.99 | 99.95 |
  | code2 | 10 | 15.00 | 150.00 |
- `when(orderRepository.save(any())).thenReturn(order)` stubs the repository so no real database call is made.
- The assertion checks that `orderDto.getTotalPrice()` equals the expected total.

---

**Test method: `it_should_fail_order_creation_when_payment_failed`**

```java
doThrow(new IllegalArgumentException()).when(paymentClient).pay(any());
```

- Uses `doThrow(...)` instead of `when(...).thenThrow(...)` because `paymentClient.pay()` returns `void`.
- `catchThrowable(...)` from AssertJ captures the exception thrown by `orderService.createOrder(...)`.
- Assertions:
  - The thrown exception is an `IllegalArgumentException`.
  - `verifyNoInteractions(orderRepository)` confirms that the repository is **never called** when the payment step fails — verifying the correct order of operations in the service.

---

## Coverage Report

JaCoCo is configured to run automatically after every `./gradlew test`.  
Open the generated report in a browser:

```
build/reports/jacoco/test/html/index.html
```

To generate only the report (tests already ran):
```bash
./gradlew jacocoTestReport
```
