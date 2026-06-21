// Datos de ejemplo para MongoDB (solo dev)

db = db.getSiblingDB('notification_db');

// ─── Suscripciones ─────────────────────────────────
// Usuarios existentes (IDs 1-10 de user-service)
db.suscripciones.insertMany([
  {
    userId: NumberLong(1),
    deviceToken: "device-token-maria-001",
    productPreferences: [NumberLong(1), NumberLong(2), NumberLong(3)],
    createdAt: ISODate("2026-06-01T10:00:00Z"),
    updatedAt: ISODate("2026-06-01T10:00:00Z")
  },
  {
    userId: NumberLong(2),
    deviceToken: "device-token-juan-002",
    productPreferences: [NumberLong(4), NumberLong(5), NumberLong(6)],
    createdAt: ISODate("2026-06-01T10:00:00Z"),
    updatedAt: ISODate("2026-06-01T10:00:00Z")
  },
  {
    userId: NumberLong(6),
    deviceToken: "device-token-matias-006",
    productPreferences: [NumberLong(7), NumberLong(8), NumberLong(9)],
    createdAt: ISODate("2026-06-01T10:00:00Z"),
    updatedAt: ISODate("2026-06-01T10:00:00Z")
  }
]);

// ─── Notificaciones demo ──────────────────────────
db.notificaciones.insertMany([
  {
    userId: NumberLong(1),
    productId: NumberLong(1),
    fridgeId: NumberLong(1),
    message: "El producto 1 (Brownie de Chocolate) ya esta disponible en la heladera 1 (Palermo Dulce)",
    read: false,
    sentAt: ISODate("2026-06-18T08:30:00Z"),
    readAt: null
  },
  {
    userId: NumberLong(1),
    productId: NumberLong(3),
    fridgeId: NumberLong(2),
    message: "El producto 3 (Tiramisú) ya esta disponible en la heladera 2 (Centro Dulce)",
    read: true,
    sentAt: ISODate("2026-06-17T14:00:00Z"),
    readAt: ISODate("2026-06-17T16:30:00Z")
  },
  {
    userId: NumberLong(6),
    productId: NumberLong(7),
    fridgeId: NumberLong(5),
    message: "El producto 7 (Buddha Bowl Vegano) ya esta disponible en la heladera 5 (Palermo Vegana)",
    read: false,
    sentAt: ISODate("2026-06-19T09:00:00Z"),
    readAt: null
  },
  {
    userId: NumberLong(2),
    productId: NumberLong(5),
    fridgeId: NumberLong(3),
    message: "El producto 5 (Bowl de Quinoa Sin Gluten) ya esta disponible en la heladera 3 (Belgrano Celiaca)",
    read: true,
    sentAt: ISODate("2026-06-15T11:00:00Z"),
    readAt: ISODate("2026-06-15T12:00:00Z")
  },
  {
    userId: NumberLong(2),
    productId: NumberLong(4),
    fridgeId: NumberLong(4),
    message: "El producto 4 (Tostada de Palta Sin Gluten) ya esta disponible en la heladera 4 (Devoto Celiaca)",
    read: false,
    sentAt: ISODate("2026-06-20T07:00:00Z"),
    readAt: null
  }
]);
