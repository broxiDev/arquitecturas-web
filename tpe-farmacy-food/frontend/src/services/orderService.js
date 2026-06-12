import ordenesData from '../mocks/orders.json'

const SIMULAR_DELAY = 300

const simularAsync = (data, delay = SIMULAR_DELAY) =>
  new Promise((resolve) => setTimeout(() => resolve(data), delay))

let ordenes = [...ordenesData]

const orderService = {
  create: async (data) => {
    const nueva = {
      id: `o${Date.now()}`,
      ...data,
      status: 'pending',
      paymentId: null,
      createdAt: new Date().toISOString(),
    }
    ordenes.push(nueva)
    return simularAsync(nueva)
  },

  getAll: async () => {
    return simularAsync([...ordenes])
  },

  getById: async (id) => {
    const orden = ordenes.find((o) => o.id === id)
    if (!orden) throw new Error(`Orden ${id} no encontrada`)
    return simularAsync(orden)
  },

  getUserOrders: async (userId) => {
    const ordenesUsuario = ordenes.filter((o) => o.userId === userId)
    return simularAsync(ordenesUsuario)
  },

  cancel: async (id) => {
    const orden = ordenes.find((o) => o.id === id)
    if (!orden) throw new Error(`Orden ${id} no encontrada`)
    if (orden.status !== 'pending') throw new Error('Solo se pueden cancelar ordenes pendientes')
    orden.status = 'cancelled'
    return simularAsync(orden)
  },

  pay: async (id) => {
    const orden = ordenes.find((o) => o.id === id)
    if (!orden) throw new Error(`Orden ${id} no encontrada`)
    if (orden.status !== 'pending') throw new Error('Solo se pueden pagar ordenes pendientes')
    orden.status = 'paid'
    orden.paymentId = `pay_${Date.now()}`
    return simularAsync(orden)
  },

  confirmPickup: async (id) => {
    const orden = ordenes.find((o) => o.id === id)
    if (!orden) throw new Error(`Orden ${id} no encontrada`)
    if (orden.status !== 'paid') throw new Error('Solo se puede confirmar retiro de ordenes pagadas')
    orden.status = 'picked_up'
    return simularAsync(orden)
  },
}

export default orderService