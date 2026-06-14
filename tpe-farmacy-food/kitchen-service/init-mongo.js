// Datos de ejemplo para MongoDB (solo dev)

db = db.getSiblingDB('kitchen_db');

db.ventas_historicas.insertMany([
  {
    productId: NumberLong(101),
    productName: "Ensalada César",
    fridgeId: NumberLong(1),
    quantity: 12,
    totalAmount: NumberDecimal("6000.00"),
    date: ISODate(new Date(Date.now() - 86400000))  // ayer
  },
  {
    productId: NumberLong(101),
    productName: "Ensalada César",
    fridgeId: NumberLong(2),
    quantity: 8,
    totalAmount: NumberDecimal("4000.00"),
    date: ISODate(new Date(Date.now() - 172800000))  // hace 2 días
  },
  {
    productId: NumberLong(102),
    productName: "Bowl Proteico",
    fridgeId: NumberLong(1),
    quantity: 5,
    totalAmount: NumberDecimal("3500.00"),
    date: ISODate(new Date(Date.now() - 86400000))
  },
  {
    productId: NumberLong(102),
    productName: "Bowl Proteico",
    fridgeId: NumberLong(1),
    quantity: 7,
    totalAmount: NumberDecimal("4900.00"),
    date: ISODate(new Date(Date.now() - 259200000))  // hace 3 días
  },
  {
    productId: NumberLong(103),
    productName: "Wrap de Pollo",
    fridgeId: NumberLong(3),
    quantity: 10,
    totalAmount: NumberDecimal("4500.00"),
    date: ISODate(new Date(Date.now() - 86400000))
  },
  {
    productId: NumberLong(103),
    productName: "Wrap de Pollo",
    fridgeId: NumberLong(2),
    quantity: 6,
    totalAmount: NumberDecimal("2700.00"),
    date: ISODate(new Date(Date.now() - 172800000))
  }
]);
