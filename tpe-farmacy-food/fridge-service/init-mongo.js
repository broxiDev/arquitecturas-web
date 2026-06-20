// Datos de ejemplo para MongoDB (solo dev)

db = db.getSiblingDB('fridge_db');

// Eventos de estado de heladeras
db.eventos_estado_heladera.insertMany([
  // Heladera 1 - COCINA-DULCE
  {
    heladeraId: NumberLong(1),
    oldStatus: "MAINTENANCE",
    newStatus: "ACTIVE",
    timestamp: ISODate("2026-06-01T08:00:00Z")
  },
  {
    heladeraId: NumberLong(1),
    oldStatus: "ACTIVE",
    newStatus: "OUT_OF_SERVICE",
    timestamp: ISODate("2026-06-10T12:00:00Z")
  },
  {
    heladeraId: NumberLong(1),
    oldStatus: "OUT_OF_SERVICE",
    newStatus: "ACTIVE",
    timestamp: ISODate("2026-06-12T09:00:00Z")
  },

  // Heladera 2 - COCINA-DULCE
  {
    heladeraId: NumberLong(2),
    oldStatus: "ACTIVE",
    newStatus: "MAINTENANCE",
    timestamp: ISODate("2026-06-15T10:00:00Z")
  },
  {
    heladeraId: NumberLong(2),
    oldStatus: "MAINTENANCE",
    newStatus: "ACTIVE",
    timestamp: ISODate("2026-06-16T14:00:00Z")
  },

  // Heladera 3 - COCINA-CELIACA
  {
    heladeraId: NumberLong(3),
    oldStatus: "MAINTENANCE",
    newStatus: "ACTIVE",
    timestamp: ISODate("2026-06-05T11:00:00Z")
  },

  // Heladera 5 - COCINA-VEGANA
  {
    heladeraId: NumberLong(5),
    oldStatus: "ACTIVE",
    newStatus: "OUT_OF_SERVICE",
    timestamp: ISODate("2026-06-18T16:00:00Z")
  },
  {
    heladeraId: NumberLong(5),
    oldStatus: "OUT_OF_SERVICE",
    newStatus: "ACTIVE",
    timestamp: ISODate("2026-06-19T08:00:00Z")
  }
]);
