# Specification: Implement Kitchen Service Endpoints

## Overview
This track focuses on implementing the REST API endpoints for the `kitchen-service` (F4 - Planificación de Cocina). The service provides a daily production plan based on historical sales and allows viewing sales history across products and fridges.

**Target Service:** `kitchen-service` (Port 8084)
**Base Path:** `/api/v1/cocina`

## Functional Requirements

### 1. Daily Production Plan (`/plan-diario`)
- **GET `/api/v1/cocina/plan-diario`**
  - Retrieves the production plan for the current date.
  - If no plan exists for today, return **404 Not Found** (no auto-generation).
- **GET `/api/v1/cocina/plan-diario?fecha=YYYY-MM-DD`**
  - Retrieves the production plan for a specific date.
  - Returns **404** if not found.
- **POST `/api/v1/cocina/plan-diario?fecha=YYYY-MM-DD`**
  - Generates or updates the production plan for the specified date.
  - **Logic:** 
    1. Fetch sales history from `order-service` (via OpenFeign) for the last N days.
    2. Calculate suggested quantities based on average sales and trends.
    3. Store as `DailyPlan` with `PlanItems` in PostgreSQL.
    4. If a plan for the date already exists, overwrite it (PUT semantics via POST).

### 2. Sales History (`/historial-ventas`)
- **GET `/api/v1/cocina/historial-ventas`**
  - Retrieves a list of historical sales records.
- **Filters (Optional Query Params):**
  - `from` & `to`: Date range (YYYY-MM-DD)
  - `productId`: Filter by specific product
  - `fridgeId`: Filter by specific fridge
- **Response:** List of `VentaHistoricaResponseDTO` (detailed records).

## Non-Functional Requirements
- **Validation:** Input parameters (dates, IDs) must be validated.
- **Documentation:** Endpoints must be documented with SpringDoc OpenAPI.
- **Integration:** Use OpenFeign for synchronous calls to `order-service`.

## Acceptance Criteria
1. `POST` creates a plan and stores it in PostgreSQL.
2. `GET` retrieves the plan for the requested date.
3. `GET /historial-ventas` returns sales records with working filters.
4. Missing plan returns 404, not an empty object.

## Out of Scope
- Automatic daily plan generation (cron jobs).
- Complex trend analysis algorithms (use simple averages for now).
- UI Implementation.
