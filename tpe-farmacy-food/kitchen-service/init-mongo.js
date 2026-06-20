// Datos de ejemplo para MongoDB (solo dev)

db = db.getSiblingDB('kitchen_db');

// Ventas históricas para COCINA-DULCE
db.ventas_historicas.insertMany([
  {
    productId: NumberLong(101),
    productName: "Brownie de Chocolate",
    fridgeId: NumberLong(1),
    quantity: 15,
    totalAmount: NumberDecimal("11250.00"),
    date: ISODate(new Date(Date.now() - 86400000))
  },
  {
    productId: NumberLong(101),
    productName: "Brownie de Chocolate",
    fridgeId: NumberLong(2),
    quantity: 10,
    totalAmount: NumberDecimal("7500.00"),
    date: ISODate(new Date(Date.now() - 172800000))
  },
  {
    productId: NumberLong(102),
    productName: "Cheesecake",
    fridgeId: NumberLong(1),
    quantity: 8,
    totalAmount: NumberDecimal("7600.00"),
    date: ISODate(new Date(Date.now() - 86400000))
  },
  {
    productId: NumberLong(102),
    productName: "Cheesecake",
    fridgeId: NumberLong(2),
    quantity: 12,
    totalAmount: NumberDecimal("11400.00"),
    date: ISODate(new Date(Date.now() - 259200000))
  },
  {
    productId: NumberLong(103),
    productName: "Tiramisú",
    fridgeId: NumberLong(1),
    quantity: 10,
    totalAmount: NumberDecimal("8800.00"),
    date: ISODate(new Date(Date.now() - 86400000))
  },
  {
    productId: NumberLong(103),
    productName: "Tiramisú",
    fridgeId: NumberLong(2),
    quantity: 8,
    totalAmount: NumberDecimal("7040.00"),
    date: ISODate(new Date(Date.now() - 172800000))
  },

  // Ventas históricas para COCINA-CELIACA
  {
    productId: NumberLong(201),
    productName: "Tostada de Palta Sin Gluten",
    fridgeId: NumberLong(3),
    quantity: 18,
    totalAmount: NumberDecimal("12960.00"),
    date: ISODate(new Date(Date.now() - 86400000))
  },
  {
    productId: NumberLong(201),
    productName: "Tostada de Palta Sin Gluten",
    fridgeId: NumberLong(4),
    quantity: 14,
    totalAmount: NumberDecimal("10080.00"),
    date: ISODate(new Date(Date.now() - 172800000))
  },
  {
    productId: NumberLong(202),
    productName: "Bowl de Quinoa Sin Gluten",
    fridgeId: NumberLong(3),
    quantity: 12,
    totalAmount: NumberDecimal("11760.00"),
    date: ISODate(new Date(Date.now() - 86400000))
  },
  {
    productId: NumberLong(202),
    productName: "Bowl de Quinoa Sin Gluten",
    fridgeId: NumberLong(4),
    quantity: 12,
    totalAmount: NumberDecimal("11760.00"),
    date: ISODate(new Date(Date.now() - 259200000))
  },
  {
    productId: NumberLong(203),
    productName: "Rolls de Primavera de Arroz",
    fridgeId: NumberLong(3),
    quantity: 16,
    totalAmount: NumberDecimal("10400.00"),
    date: ISODate(new Date(Date.now() - 86400000))
  },
  {
    productId: NumberLong(203),
    productName: "Rolls de Primavera de Arroz",
    fridgeId: NumberLong(4),
    quantity: 12,
    totalAmount: NumberDecimal("7800.00"),
    date: ISODate(new Date(Date.now() - 172800000))
  },

  // Ventas históricas para COCINA-VEGANA
  {
    productId: NumberLong(301),
    productName: "Buddha Bowl Vegano",
    fridgeId: NumberLong(5),
    quantity: 22,
    totalAmount: NumberDecimal("18700.00"),
    date: ISODate(new Date(Date.now() - 86400000))
  },
  {
    productId: NumberLong(301),
    productName: "Buddha Bowl Vegano",
    fridgeId: NumberLong(6),
    quantity: 16,
    totalAmount: NumberDecimal("13600.00"),
    date: ISODate(new Date(Date.now() - 172800000))
  },
  {
    productId: NumberLong(302),
    productName: "Salteado de Tofu",
    fridgeId: NumberLong(5),
    quantity: 15,
    totalAmount: NumberDecimal("11700.00"),
    date: ISODate(new Date(Date.now() - 86400000))
  },
  {
    productId: NumberLong(302),
    productName: "Salteado de Tofu",
    fridgeId: NumberLong(6),
    quantity: 15,
    totalAmount: NumberDecimal("11700.00"),
    date: ISODate(new Date(Date.now() - 259200000))
  },
  {
    productId: NumberLong(303),
    productName: "Curry de Garbanzos",
    fridgeId: NumberLong(5),
    quantity: 14,
    totalAmount: NumberDecimal("12880.00"),
    date: ISODate(new Date(Date.now() - 86400000))
  },
  {
    productId: NumberLong(303),
    productName: "Curry de Garbanzos",
    fridgeId: NumberLong(6),
    quantity: 12,
    totalAmount: NumberDecimal("11040.00"),
    date: ISODate(new Date(Date.now() - 172800000))
  }
]);