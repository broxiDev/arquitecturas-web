```
 Cron/Scheduler       Kitchen              Order               Product             Fridge
      │                  │                   │                    │                   │
      │ todos los días   │                   │                    │                   │
      │ a las 6:00       │                   │                    │                   │
      │ ────────────────>│                   │                    │                   │
      │                  │                   │                    │                   │
      │                  │ GET /historial-   │                    │                   │
      │                  │     ventas/cocina │                    │                   │
      │                  │ /{cocinaId}       │                    │                   │
      │                  │ ?from=&to=        │                    │                   │
      │                  │ (ultimos 7 dias)  │                    │                   │
      │                  │ ──────────────────>│                   │                   │
      │                  │                   │                    │                   │
      │                  │                   │ GET productos/     │                   │
      │                  │                   │     cocina/{id}    │                   │
      │                  │                   │ ───────────────────>│                   │
      │                  │                   │                    │                   │
      │                  │                   │     productos[]    │                   │
      │                  │                   │ <───────────────────│                   │
      │                  │                   │                    │                   │
      │                  │                   │ SELECT FROM orders │                   │
      │                  │                   │ WHERE status IN    │                   │
      │                  │                   │  ('PAID','PICKED') │                   │
      │                  │                   │ AND created_at     │                   │
      │                  │                   │  BETWEEN ? AND ?   │                   │
      │                  │                   │       │            │                   │
      │                  │                   │       │ (DB order)  │                   │
      │                  │                   │<──────│            │                   │
      │                  │                   │                    │                   │
      │                  │  sales[]          │                    │                   │
      │                  │ <──────────────────│                    │                   │
      │                  │                   │                    │                   │
      │                  │ GET /remanente    │                    │                   │
      │                  │ /{cocinaId}       │                    │                   │
      │                  │ ──────────────────────────────────────────────────────────>│
      │                  │                   │                    │                   │
      │                  │  stock[]          │                    │                   │
      │                  │ <──────────────────────────────────────────────────────────│
      │                  │                   │                    │                   │
      │                  │ calc:             │                    │                   │
      │                  │  avg = ceil(      │                    │                   │
      │                  │   total/7)        │                    │                   │
      │                  │  suggested =      │                    │                   │
      │                  │   avg - stock     │                    │                   │
      │                  │  (solo si >0)     │                    │                   │
      │                  │       │           │                    │                   │
      │                  │       │ (DB       │                    │                   │
      │                  │       │  kitchen) │                    │                   │
      │                  │<──────│           │                    │                   │
      │                  │                   │                    │                   │
      │  plan generado   │                   │                    │                   │
      │<─────────────────│                   │                    │                   │
```
