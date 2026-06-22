import servicioMock from './mock.js'
import servicioHttp from './http.js'

const useMock = import.meta.env.VITE_USE_MOCK === 'true'
export default useMock ? servicioMock : servicioHttp
