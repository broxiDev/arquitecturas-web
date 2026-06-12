import { useEffect } from 'react'
import useKitchenStore from './kitchenStore'
import DailyPlanTable from './DailyPlanTable'
import DatePicker from './DatePicker'

function Cocina() {
  const { dailyPlan, availableDates, selectedDate, loading, error, fetchDailyPlan, fetchAvailableDates, setDate } = useKitchenStore()

  useEffect(() => {
    fetchAvailableDates()
    fetchDailyPlan()
  }, [])

  const handleDateChange = (date) => {
    setDate(date)
    fetchDailyPlan(date)
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-800">Plan de Produccion Diario</h2>
      </div>

      <DatePicker
        availableDates={availableDates}
        selectedDate={selectedDate}
        onSelectDate={handleDateChange}
      />

      {loading && !dailyPlan && (
        <div className="text-center py-8 text-gray-500">Cargando plan...</div>
      )}

      {error && (
        <div className="text-center py-4 text-red-500 bg-red-50 rounded-lg">{error}</div>
      )}

      {dailyPlan && !loading && (
        <div className="mt-4 bg-white rounded-xl shadow-sm border border-gray-200 p-4">
          <div className="mb-3">
            <span className="text-sm text-gray-500">
              Plan para el {new Date(dailyPlan.date + 'T12:00:00').toLocaleDateString('es-AR', { weekday: 'long', day: 'numeric', month: 'long' })}
            </span>
          </div>
          <DailyPlanTable items={dailyPlan.items} />
        </div>
      )}

      {!dailyPlan && !loading && !error && (
        <div className="text-center py-8 text-gray-500">
          Selecciona una fecha para ver el plan de produccion.
        </div>
      )}
    </div>
  )
}

export default Cocina