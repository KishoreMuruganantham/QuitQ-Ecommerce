# Release Notes

All notable changes to **QuitQ — E-Commerce Platform** are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
  single `@Transactional` method (Steps 1–10): address and cart validation,
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

[1.2.0]: https://github.com/KishoreMuruganantham/QuitQ-Ecommerce/releases/tag/v1.2.0
[1.1.0]: https://github.com/KishoreMuruganantham/QuitQ-Ecommerce/releases/tag/v1.1.0
[1.0.0]: https://github.com/KishoreMuruganantham/QuitQ-Ecommerce/releases/tag/v1.0.0
