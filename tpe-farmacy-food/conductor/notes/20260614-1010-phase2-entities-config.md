# Phase 2: Entities & Database Config — Summary

**Track:** kitchen_endpoints_20260614
**Date:** 2026-06-14

## Tasks Completed

1. **Updated `application.yml`**
   - PostgreSQL datasource configured (`localhost:5432/kitchen_db`, user `root`, no password)
   - MongoDB connection configured (`localhost:27017/kitchen_db`)
   - JPA with `ddl-auto: update` and PostgreSQL dialect
   - Spring profile `dev` active by default

2. **Created Entities**
   - `DailyPlan.java` (PostgreSQL) — id, date (unique), createdAt, items (OneToMany)
   - `PlanItem.java` (PostgreSQL) — id, dailyPlan (FK), productId, productName, suggestedQuantity
   - `VentaHistorica.java` (MongoDB) — id, productId, productName, fridgeId, quantity, totalAmount, date

## Decisions
- `DailyPlan.date` has unique constraint — one plan per day
- `PlanItem` has unique constraint on `(daily_plan_id, product_id)` — no duplicate products per plan
- `productName` is denormalized to avoid frequent calls to product-service
- `VentaHistorica` uses MongoDB `@Document` annotation with collection `ventas_historicas`
- No separate config classes needed — Spring Boot auto-configuration handles dual datasource
