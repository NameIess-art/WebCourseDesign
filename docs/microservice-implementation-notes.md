# Mall Microservice Implementation Notes

This project keeps one runnable Spring Boot backend for the course demo, while the domain boundaries, APIs, tables and infrastructure are prepared for a Spring Cloud Alibaba split.

## Logical Service Boundaries

- `mall-auth-service`: login, register, JWT, role identity.
- `mall-product-service`: categories, products, SKU, specs, rich detail blocks, stock deduction.
- `mall-order-service`: cart, checkout, order status, audit, modification, shipment, reconciliation.
- `mall-member-service`: addresses, coupons claimed by user, points, member profile, messages.
- `mall-marketing-service`: activities, promotion rules, group-buy, presale, bundle and distribution demo flows.
- `mall-operation-service`: configs, dictionaries, announcements, merchants, penalties, RBAC, content audit, analytics.
- `mall-seckill-service`: seckill events, local rate limit, stock pre-deduction model and idempotent order creation.
- `mall-gateway`: planned `/api/**` ingress when the modules are physically split.

## Infrastructure

`docker-compose.yml` starts MySQL, Redis, Nacos, RocketMQ, Seata and Sentinel Dashboard. The current backend can still run with H2 for classroom demonstration. When split into modules, each service should register with Nacos and expose APIs through the gateway.

## High-Concurrency Mapping

- Normal stock: database conditional update, `update stock = stock - n where stock >= n`.
- Seckill stock: Redis pre-deduction plus request idempotency and user/activity rate limit.
- Async flows: payment callback, logistics, member message and seckill order creation map to RocketMQ topics.
- Distributed transaction: checkout stock/order/member consistency maps to Seata after physical service split.
- Hot cache: product detail, categories, recommendations and activity venue config map to Redis.

## Demo Accounts

- Admin: `admin / admin123`
- Merchant: `merchant / merchant123`
- Customer: `demo / demo123`
