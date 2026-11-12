# Release Notes

All notable changes to **QuitQ — E-Commerce Platform** are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [2.3.0] —2026-11-12

The platform becomes a full product: a complete Flipkart-style three-role
front-end, the remaining case-study compliance items closed (API versioning,
server-side logout, password recovery, order-tracking propagation, seller-id
resolution), a containerised runtime and an AWS deployment pipeline where a
push to `main` runs the unit tests, builds both images and rolls them onto
EC2 in front of a private RDS.

### Added

**React UI — Flipkart redesign (`quitq-ui/`)**

- Single-line header (logo, debounced search with live suggestions, Home,
  profile dropdown, Cart), role-guarded layouts, and a public layout for the
  library page; footer rebuilt as the blue panel with per-role feature columns.
- Full route tree: catalogue with category circles and an opt-in **Filters**
  panel (brand/price, draft inputs committed on Apply), product detail
  (breadcrumb, seller name, qty picker, add-to-cart / buy-now), cart with
  qty controls and PRICE DETAILS, checkout (address add/select + five payment
  methods), order confirmation, My Orders and a four-step tracking timeline.
- Seller: My Products (add / edit / mark out-of-stock / delete), orders with
  status dropdowns, sales report with totals. Admin: analytics cards, user and
  seller management, category CRUD.
- Sign-up with Customer/Seller toggle and a **real-time password-strength
  meter**; **Forgot Password** and **Reset Password** pages.
- One design system: ~60 `fk-*` classes over Bootstrap, Roboto, your logo and
  the Q-mark favicon; `no-console` lint rule; **zero new npm dependencies**
  (dependency set identical to the trainer's `helpdesk-ui`).

**API (`quitq_backend/`)**

- **API versioning** — `ApiVersionFilter` + `VersioningConfig`: `/api/v1..v3/**`
  rewrites onto `/api/**` and stamps `X-API-Version`.
- **Server-side logout** — `TokenDenylist` consulted by `JwtFilter`; replaying
  a logged-out token returns401.
- **Password recovery** — `POST /api/auth/forgot-password` (username or email,
  generic response, single-use15-minute token in `PasswordResetTokenStore`)
  and `POST /api/auth/reset-password` (BCrypt re-encode, token burned).
- `PUT /api/product/{id}` with ownership checks and stock→status recompute;
  `GET /api/product/seller/{sellerId}` for My Products; sales-report aggregate.
- `user-details` now returns the **seller id** for `ROLE_SELLER` (was the user
  id), fixing every seller-scoped call.
- Order status updates write an `order_tracking` row and advance the order
  header, so the customer timeline follows the seller's dropdown.
- Global CORS configuration bean (dev origin) replaces per-controller
  `@CrossOrigin`; every service logs through `@Slf4j` (zero `System.out`).

**Docker**

- Multi-stage images: `quitq_backend` (Maven → Temurin21 JRE) and `quitq_ui`
  (Node20 → nginx); nginx reverse-proxies `/api` to the backend so the browser
  is always same-origin — no CORS in any containerised environment.
- `docker-compose.yml` brings up frontend (:80), backend (:8082) and a
  self-seeding MySQL from `quitq_ecommerce.sql`; `docker-compose.ec2.yml` is
  the image-based variant CodeDeploy ships, with the **RDS endpoint baked in**
  so a release can never silently fall back to a container database.

**AWS & CI/CD**

- EC2 `quitq-app` (t3.micro, AL2023) running the compose stack; **private RDS**
  `quitq-db` (MySQL8, reachable only from the EC2 security group); ECR
  repositories for both images; S3 bucket for seeds and deploy artifacts.
- `buildspec.yml` — `mvn -B test` runs first as the **quality gate** (with a
  throwaway MySQL container), then both images are built and pushed to ECR.
- `appspec.yml` + `deploy.sh` — CodeDeploy copies the compose file and runs
  `docker compose pull && up -d`; the database volume is never touched.
- `.github/workflows/deploy.yml` — every push to `main` starts the pipeline
  through a scoped IAM user whose only permission is
  `codepipeline:StartPipelineExecution` (the org SCP blocks OIDC federation).
- Green end-to-end executions proven: Source → Build (Tests run:51) → Deploy.

**Library — Case Study A**

- `Author` / `Book` entities, derived-query `BookRepository`
  (`findByPublishedYearGreaterThanEqual`), `BookService`, `BookController`
  (`GET /api/books`, `GET /api/books/search?minYear=`) and the public
  `/library` page: fk-styled table, skeleton loading, draft+Apply year filter.
- `docs/Library-Case-Study-A-Seed.sql` —3 authors,6 books (idempotent).

**Documentation**

- README: deployment section (three run modes), library route and API rows,
  corrected counts (51 tests across13 classes,5 Postman folders /58 requests,
 26 components); Postman collection gains the **Library Case Study A - Books**
  folder.

### Verification

- `mvnw.cmd test` → **51 tests,0 failures** (Mockito service suites plus
  `@WebMvcTest` slices for versioning, logout denylist, password reset and
  sales report); `vite build` green; `eslint`0 problems.
- Browser walkthroughs on all three modes (dev, local Docker, EC2): login,
  catalogue, filters, cart → checkout → confirmation → tracking, seller CRUD
  and status forwarding, admin management, forgot-password round trip, `/library`
  year search — zero console errors.
- Pipeline executions green on `main`; RDS seeded (10 products,6 books) and
  verified as the live datasource; Cost Explorer reports $0.00 for the month.

### Known limitations & deliberate deviations

- **Postman instead of Swagger** and **JavaScript instead of TypeScript** —
  both follow the trainer's `helpdesk-ui` conventions; OAuth stays on the
  optional list (his project does not implement it either).
- Payment is method selection + a `PENDING → COMPLETED` record — the seam a
  real gateway drops into; no live PSP integration.
- Logout denylist and password-reset tokens are in-memory (single instance) —
  Redis is the production answer; password reset shows the link on screen
  because no SMTP is in scope.
- The site runs over plain HTTP (no domain/certificate provisioned); the
  GitHub-App webhook could not register (org SCP), so GitHub Actions bridges
  check-ins to the pipeline.

### Upgrade notes

Run it any of three ways — identical artifacts, configuration only differs:

```bash
docker compose up -d                              # http://localhost
cd quitq_backend && ./mvnw spring-boot:run       # dev API :8080
cd quitq_ui && npm install && npm run dev        # dev UI  :5173
```

`quitq_ecommerce.sql` remains a **full reset script** (drops and recreates its
twelve tables) — import only into a database you can afford to lose; the
library seed is a separate idempotent file.

---

## [2.2.0] — 2026-11-01

The platform gets its browser front-end: a React single-page app that signs
the user in with HTTP Basic, reads the JWT's role from a new user-details
endpoint and renders the matching dashboard — plus the customer cart flow
(stat, add, delete) driven end-to-end through Redux.

### Added

**React UI (`quitq-ui/`)**

- Vite 8 + React 19.3.1 project with react-router 7, Redux Toolkit, axios and
  Bootstrap 5.3.3 from the CDN — five routes: login, `customer/signup`,
  `customer/:token`, `admin/:token` and a page-not-found fallback.
- `Login` — HTTP Basic to `GET /api/auth/login`, stores the JWT, then calls
  `GET /api/auth/user-details` and switches on the role: `ROLE_CUSTOMER` →
  customer dashboard, `ROLE_ADMIN` → admin dashboard, `ROLE_SELLER` → stays on
  the login screen (seller dashboard on the roadmap).
- `SignUp` — posts a `CustomerDto` to `POST /api/customer/signup`.
- Customer dashboard composed of `Stat` (line count, units, total amount),
  `AddToCart` (product id + quantity) and `CartList` (delete), all reading the
  `cartItems` slice of a single `CustomerReducer`.
- Redux round-trip actions mirroring the API: `getCartItems` (GET),
  `addToCart` (POST) and `DELETE /api/cart/item/{id}` — the same
  get/post/delete progression, wired through manual async thunks.
- `AdminDashboard` re-verifies the token against `user-details` and re-checks
  the role before rendering; `config.js` carries the dev/prod API base URL.

**API (`quitq_backend/`)**

- `GET /api/auth/user-details` — authenticated endpoint returning the new
  `DetailsDto` record (`id`, `username`, `isActive`, `role`). For
  `ROLE_CUSTOMER` the id is swapped to the customer id, because the cart APIs
  are customer-id based; `UserRepository.getUserDetails` is a JPQL projection
  over `users`.
- `InactiveUserException` raised from `UserService.getUserDetails` when
  `users.is_active = 0`, mapped by `GlobalExceptionHandler` to **HTTP 412**
  with `ErrorDto(message, "Contact ADMIN admin@quitq.com", timestamp)`.
- `SecurityConfig` gains a matcher for `GET /api/auth/user-details`
  (`authenticated()`), taking the chain to 39 matchers.
- `@CrossOrigin(origins = "http://localhost:5173")` on the three controllers
  the UI calls — `AuthController`, `CustomerController`, `CartController`.

**Documentation**

- README: new *React UI* section, `user-details` and CORS in the auth
  reference, UI run steps in *Getting Started*, updated endpoint/DTO/exception
  counts and roadmap.

### Verification

- Backend suite green — 15 tests across five service classes.
- UI production build green (`vite build`, 98 modules).
- Browser end-to-end: customer login → dashboard with the seeded cart
  (Rs. 24,999) → add product → delete → logout → admin login → sign-up →
  seller sign-in stays on `/`.
- CORS preflights 200 on every path the UI calls.

### Known limitations

- The seller dashboard is not built yet — a `ROLE_SELLER` login stores the
  role and stays on the login screen.
- `npm run lint` reports the same unused-variable warnings the component
  sources inherit; `dev` and `build` do not run eslint.

### Upgrade notes

No schema change. After pulling, install the UI dependencies once, then run
both servers:

```bash
cd quitq-ui && npm install && npm run dev     # terminal 1 → http://localhost:5173
cd quitq_backend && ./mvnw spring-boot:run    # terminal 2 → :8080, start first
```

---

## [2.1.0] — 2026-10-21

Static analysis joins the workflow: the first SonarCloud analysis of the
backend passes the quality gate with zero open issues, and the scanner is
wired into the Maven build for every release that follows.

### Added

**Code quality (`pom.xml`)**

- `sonar-maven-plugin` 5.8.0.7211 — one `./mvnw verify sonar:sonar` command
  runs the suite and analyses the project against SonarCloud.
- `jacoco-maven-plugin` 0.8.12 (`prepare-agent` plus a `report` execution
  bound to the `test` phase) as the coverage source for the analysis.

**Documentation**

- README section *Code quality — SonarCloud* with the scanner command, the
  first-analysis results and the reviewed issue; roadmap updated with the
  static-analysis item.

### Analysis results — 21 October 2026

| Metric | Value |
| :-- | :-- |
| Quality gate | **PASSED** — 6/6 conditions OK |
| Bugs · code smells · security hotspots | 0 · 0 · 0 |
| Duplications | 0 % |
| Maintainability debt | 0 min |
| Coverage | 30.8 % |
| Lines of code (NCLOC) | 2,587 |
| Open issues | 0 |

### Reviewed

- `java:S4502` (CRITICAL) on `SecurityConfig`'s
  `.csrf(AbstractHttpConfigurer::disable)` — resolved on SonarCloud as a
  **false positive** with a written justification: QuitQ is a stateless REST
  API; authentication uses Bearer JWTs in the `Authorization` header, no
  client uses session cookies, and state-changing endpoints require a header
  browsers never attach cross-origin. The source code is unchanged.

---

## [2.0.0] — 2026-10-20

The API is now secured: JWT login with BCrypt passwords, role-based route
matching and per-owner data checks — plus optional pagination across every
list endpoint and the first Mockito unit-test suite.

### Added

**Pagination**

- Optional `page` / `size` query parameters on all twelve list endpoints
  (`page` defaults to `0`, `size` to `20`), mapped with `PageRequest.of` and
  repository methods that take a trailing `Pageable` and return `List`.

**Catalogue**

- `GET /api/product/unpurchased` — LEFT JOIN JPQL
  (`left join p.orderItems oi where oi is null`) listing products that have
  never been ordered, backed by a new `orderItems` collection on `Product`.

**Security (`config` / `utility` packages)**

- `SecurityConfig` — `SecurityFilterChain` with 38 matchers: catalogue reads
  are `permitAll`, mutating endpoints require
  `hasAuthority("ROLE_CUSTOMER" | "ROLE_SELLER" | "ROLE_ADMIN")`, CSRF
  disabled, HTTP Basic on.
- `JwtUtility` — HS256 token generation and extraction with a Base64-encoded
  signing key and a 24-hour expiry.
- `JwtFilter` — `OncePerRequestFilter` registered with
  `addFilterBefore(..., UsernamePasswordAuthenticationFilter.class)`; a valid
  `Authorization: Bearer` token populates the `SecurityContext`, so
  controllers resolve the caller from `Principal`.
- `MyUserDetailsService` — loads the account by username and honours the new
  `users.is_active` flag (`User` now implements `UserDetails`).
- BCrypt hashing at every password write — customer signup, seller signup and
  `POST /api/user/add`; seeded accounts carry real BCrypt hashes.
- `OwnershipException` (HTTP 400) raised when a customer or seller acts on
  another user's cart, order, payment or product — checked in the checkout,
  order, payment, cart and product services.

**Unit tests**

- 14 Mockito tests across five service classes (`ProductServiceTest`,
  `CustomerServiceTest`, `OrderServiceTest`, `PaymentServiceTest`,
  `CartServiceTest`) using `@ExtendWith(MockitoExtension.class)`,
  `@InjectMocks` / `@Mock` and `when` / `verify` — happy paths, duplicate
  signup, insufficient stock, missing entities, BCrypt encoding and
  ownership violations.

**API testing**

- Collection grown to 52 requests: three login requests (admin, seller,
  customer) exchange HTTP Basic credentials for tokens into collection
  variables that the 33 protected requests send as
  `Authorization: Bearer {{...}}`.

### Changed

- **Breaking** — `POST /api/auth/login` (JSON `LoginDto`) is replaced by
  `GET /api/auth/login` with HTTP Basic credentials, returning `TokenDto`
  (`token`, `expiryTime`, `issueTime`). `AuthService` and `LoginDto` are
  removed; signup still creates the `users` row first, now BCrypt-encoded.

### Known limitations

- A malformed Bearer token is not caught in `JwtFilter` and surfaces as a
  HTTP 500; token-format hardening is planned.
- `product.price` and monetary columns still use `DOUBLE`.
- API documentation, versioning, structured logging and the React front-end
  remain on the roadmap.

### Upgrade notes

Fresh installs from `quitq_ecommerce.sql` need nothing extra. On an existing
database, seeded rows need the `is_active` column set to enabled (rows
created before the column existed default to disabled, which blocks login)
and placeholder passwords replaced with the BCrypt hashes from the seed
script — `UPDATE users SET is_active = b'1';`.

---

## [1.2.0] — 2026-10-11

The complete REST API layer: all eight business flows implemented on top of the
v1.0.0 schema, with validation, JPQL and centralised exception handling.

### Added

**Application layer (`quitq_backend/`)**

- Layered code across 11 controllers, 11 services, 12 repositories, 12
  entities, 22 record DTOs, 7 static mappers and 5 enums — 46 endpoints in all.
- `application.properties` targets `quitq_db` with
  `createDatabaseIfNotExist=true` and `spring.jpa.hibernate.ddl-auto=update`,
  so the entities map onto the existing tables without a migration.

**Flow 1 — Auth & signup**

- `POST /api/auth/login` resolving the profile from `LoginDto`.
- Customer and seller signup creating the `users` row first, then the profile;
  a taken username/email raises `DuplicateEntryException`.
- Customer, seller and user read/delete endpoints.

**Flow 2 / 6 / 8 — Category & product**

- Product CRUD owned by a seller (`ProductDto`, `ProductRespDto`, `ProductInfoDto`).
- Derived-query endpoints for brand, price range, keyword search and category.
- **Dynamic filter** — `GET /api/product/filter` with optional `brand`,
  `categoryId`, `minPrice`, `maxPrice`, bound through the `(?N IS NULL OR …)`
  JPQL pattern.
- `PUT /api/product/{id}/out-of-stock` and category insert/update via `CategoryDto`.

**Flow 3 / 4 — Address & cart**

- Shipping-address add/list/get/delete with an `isDefault` flag.
- Cart add/view/update/remove with the **price snapshotted at add time** and a
  live line-total/total computed from the snapshot.

**Flow 4 / 5 / 7 / 8 — Checkout, payment, seller & admin**

- `POST /api/order/checkout/{customerId}/{shippingAddressId}?method=…` — a
  single `@Transactional` method: address and cart validation,
  stock re-check, order number generation, order + order items + payment
  records, cart clear.
- Order reads by id and by customer, plus the `order_tracking` timeline.
- `POST /api/payment/{orderId}/complete`.
- Seller order-item list through a JPQL join across orders, customers and
  products, and order-item status updates (`PUT …/status`).
- Admin analytics: `GROUP BY` order-status counts plus user and seller counts.

**Validation & error handling**

- Jakarta Bean Validation on the request records (`@NotBlank`, …) enforced at
  the controller boundary with `@Valid`.
- `GlobalExceptionHandler` (`@ControllerAdvice`) mapping
  `ResourceNotFoundException`, `MethodArgumentNotValidException` (field →
  message map), `InvalidCallException`, `InsufficientStockException` and
  `DuplicateEntryException` to structured HTTP 400 responses
  (`ErrorDto`: `message`, `comments`, `timestamp`).

**API testing**

- `postman/QuitQ-Ecommerce.postman_collection.json` — Postman v2.1 collection
  with 4 folders and 49 requests, a `baseUrl` variable, seed credentials in the
  request descriptions and three error-scenario requests (duplicate signup,
  empty-field validation, unknown product id).

### Endpoints

| Area | Method summary |
| :-- | :-- |
| Auth & users (13) | `POST /api/auth/login`, `POST /api/customer/signup`, `POST /api/seller/signup`, `POST /api/user/add`, read/delete for customer, seller, user |
| Category & product (15) | Category CRUD, product CRUD, `brand`, `price`, `search`, `category/{id}`, `filter`, `out-of-stock` |
| Address & cart (8) | Address add/all/get/delete, `POST /api/cart/add/…`, `GET /api/cart/{id}`, `PUT|DELETE /api/cart/item/{id}` |
| Order, payment, seller, admin (10) | `checkout`, order reads, tracking, seller items, item status, payment complete, three admin analytics |

### Notes

- The `User` entity maps to the existing table through
  `@Table(name = "users")`, keeping Hibernate from creating an empty `user`
  shadow table.
- Stock is double-guarded: the Java re-check inside checkout raises
  `InsufficientStockException`, and `trg_check_stock_before_order` refuses the
  order line at the database — a rejected checkout rolls back with no partial
  order or payment row.
- No schema change in this release: `ddl-auto=update` only aligns the entity
  mappings with the v1.0.0 tables.

### Known limitations

- No JWT or role-based authorisation yet — `SecurityAutoConfiguration` is
  excluded and every endpoint is open; login matches the username only and the
  password column holds placeholders.
- No Swagger/OpenAPI document and no automated test suite — both on the roadmap.

### Upgrade notes

`git pull`, then run — no need to re-import the schema:

```bash
cd quitq_backend
./mvnw spring-boot:run
```

---

## [1.1.0] — 2026-10-06

Category CRUD, the first application layer on top of the schema.

### Added

**Spring Boot project (`quitq_backend/`)**

- Maven, Java 21, Spring Boot 3.5.16, packaged as a runnable JAR.
- Dependencies: Web, Data JPA, MySQL Driver, Security, Lombok, Validation.
  `SecurityAutoConfiguration` is excluded so the app starts without auth.
- `application.properties` targets `quitq_db` with
  `createDatabaseIfNotExist=true` and `spring.jpa.hibernate.ddl-auto=update`.

**Layered code**

- `Category` entity mapped to the existing `category` table.
- `CategoryRepository` extending `JpaRepository`.
- `CategoryService` with `insert`, `getAll`, `getById`, `deleteById`;
  an invalid id raises `ResourceNotFoundException`.
- `GlobalExceptionHandler` replying with an `ErrorDto`
  (`message`, `comments`, `timestamp`) and HTTP 400.
- `CategoryController` exposing the four endpoints.

### Endpoints

| Method | URL | Result |
| :-- | :-- | :-- |
| POST | `/api/category/add` | 200 with the saved record |
| GET | `/api/category/all` | 200 with every category |
| GET | `/api/category/{id}` | 200, or 400 `ErrorDto` for an unknown id |
| DELETE | `/api/category/{id}` | 200 |

### Notes

- `Category.id` is a `Long` but declares
  `columnDefinition = "INT AUTO_INCREMENT"` so Hibernate matches the existing
  `id INT` column instead of widening it to `BIGINT`, which MySQL rejects
  because `product.category_id` references it.

---

## [1.0.0] — 2026-10-02

First public cut of the QuitQ design and persistence layer.

### Added

**Database schema (`quitq_ecommerce.sql`)**
- `quitq_db` database definition targeting **MySQL 8.0+ / InnoDB / `utf8mb4`**.
- **12 normalised tables** covering the full commerce domain:
  `users`, `customer`, `seller`, `shipping_address`, `category`, `product`,
  `cart`, `cart_item`, `orders`, `order_item`, `payment`, `order_tracking`.
- Referential integrity across every relationship with explicit
  `ON DELETE` / `ON UPDATE` rules (`CASCADE` for owned data, `RESTRICT`
  for data referenced by financial records).
- Unique constraints on `users.username`, `users.email`,
  `shipping_address` defaults, `orders.order_number` and `payment.order_id`
  (one payment record per order).

**Business-rule triggers**
- `trg_check_stock_before_order` — `BEFORE INSERT` guard that raises
  `SQLSTATE '45000'` on missing product or insufficient stock, preventing overselling.
- `trg_reduce_stock_after_order` — `AFTER INSERT` that decrements `stock_qty`
  and transitions `product.status` to `OUT_OF_STOCK` at zero.
- `trg_order_initial_tracking` — `AFTER INSERT` that seeds the `order_tracking`
  audit row so every order has a timeline entry from creation.

**Seed data**
- 4 users (1 admin, 2 sellers, 1 customer), 1 customer profile,
  2 seller profiles, 1 default shipping address.
- 4 categories — Electronics, Mobile, Fashion, Home & Furniture.
- 5 products with brand, price, stock and image references.
- 1 cart with 1 line item for walkthrough testing.

**Modelling & documentation**
- `quitq_ecommerce.mwb` — editable MySQL Workbench EER model.
- `docs/QuitQ - Ecom.pdf` — project specification and architecture document.
- `docs/E-commerce Registration &-2026-09-25-055625.png` — end-to-end workflow
  diagram spanning all eight business flows.
- `README.md` — project overview, feature set per flow, Mermaid ER diagram,
  trigger reference, getting-started guide and roadmap.
- `LICENSE` — MIT License, Copyright (c) 2026 Kishore Muruganantham.

### Design decisions

| Decision | Rationale |
| :-- | :-- |
| Stock validation in a trigger, not the application | Invariants hold regardless of which client writes to the database |
| `order_number` as a unique `VARCHAR(64)` | Human-readable reference on confirmation screens, decoupled from the surrogate key |
| `cart_item.price` snapshot | A cart line keeps the price it was added at, so totals do not drift when the catalogue changes |
| Soft status columns (`VARCHAR` + `COMMENT`) | Keeps the schema portable across MySQL versions while documenting the allowed states |
| `RESTRICT` on `orders` and `order_item` parents | Historical financial records cannot be orphaned by a cascade delete |

### Known limitations

- Passwords are BCrypt **placeholders**, not usable credentials.
- `product.price` and monetary columns use `DOUBLE`; `DECIMAL` is planned for
  payment-accurate arithmetic.
- No application layer yet — API and front-end are on the roadmap.

### Upgrade notes

Fresh install only. Run:

```bash
mysql -u root -p < quitq_ecommerce.sql
```

The script drops and recreates all tables, so it is **not** safe to run against
a database containing data you wish to keep.

---

[2.3.0]: https://github.com/KishoreMuruganantham/QuitQ-Ecommerce/releases/tag/v2.3.0
[2.2.0]: https://github.com/KishoreMuruganantham/QuitQ-Ecommerce/releases/tag/v2.2.0
[2.1.0]: https://github.com/KishoreMuruganantham/QuitQ-Ecommerce/releases/tag/v2.1.0
[2.0.0]: https://github.com/KishoreMuruganantham/QuitQ-Ecommerce/releases/tag/v2.0.0
[1.2.0]: https://github.com/KishoreMuruganantham/QuitQ-Ecommerce/releases/tag/v1.2.0
[1.1.0]: https://github.com/KishoreMuruganantham/QuitQ-Ecommerce/releases/tag/v1.1.0
[1.0.0]: https://github.com/KishoreMuruganantham/QuitQ-Ecommerce/releases/tag/v1.0.0
