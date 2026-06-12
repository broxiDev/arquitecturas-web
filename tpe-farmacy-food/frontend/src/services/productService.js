import productosData from '../mocks/products.json'

const SIMULAR_DELAY = 300

const simularAsync = (data, delay = SIMULAR_DELAY) =>
  new Promise((resolve) => setTimeout(() => resolve(data), delay))

const productService = {
  getAll: async (categoria) => {
    let resultado = [...productosData]
    if (categoria) {
      resultado = resultado.filter((p) => p.dietaryCategory === categoria)
    }
    return simularAsync(resultado)
  },

  getById: async (id) => {
    const producto = productosData.find((p) => p.id === id)
    if (!producto) throw new Error(`Producto ${id} no encontrado`)
    return simularAsync(producto)
  },

  create: async (data) => {
    const nuevo = { id: `p${Date.now()}`, createdAt: new Date().toISOString(), ...data }
    productosData.push(nuevo)
    return simularAsync(nuevo)
  },

  update: async (id, data) => {
    const idx = productosData.findIndex((p) => p.id === id)
    if (idx === -1) throw new Error(`Producto ${id} no encontrado`)
    productosData[idx] = { ...productosData[idx], ...data, updatedAt: new Date().toISOString() }
    return simularAsync(productosData[idx])
  },

  delete: async (id) => {
    const idx = productosData.findIndex((p) => p.id === id)
    if (idx === -1) throw new Error(`Producto ${id} no encontrado`)
    productosData.splice(idx, 1)
    return simularAsync({ eliminado: true })
  },
}

export default productService