# Release Notes

All notable changes to **QuitQ — E-Commerce Platform** are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

[1.0.0]: https://github.com/KishoreMuruganantham/QuitQ-Ecommerce/releases/tag/v1.0.0
