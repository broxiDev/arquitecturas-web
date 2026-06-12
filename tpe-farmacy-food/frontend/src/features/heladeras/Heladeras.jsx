import { useEffect } from 'react'
import useFridgeStore from './fridgeStore'
import FridgeCard from './FridgeCard'
import FridgeDetail from './FridgeDetail'

function Heladeras() {
  const { fridges, selectedFridge, loading, error, fetchFridges, fetchFridgeById, clearSelectedFridge } = useFridgeStore()

  useEffect(() => {
    fetchFridges()
  }, [])

  const handleSelectFridge = (fridge) => {
    fetchFridgeById(fridge.id)
  }

  if (selectedFridge) {
    return (
      <div className="px-2 md:px-0">
        <FridgeDetail fridge={selectedFridge} onBack={clearSelectedFridge} />
      </div>
    )
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <span className="text-sm text-gray-500">{fridges.length} heladeras</span>
      </div>

      {loading && (
        <div className="text-center py-8 text-gray-500">Cargando heladeras...</div>
      )}

      {error && (
        <div className="text-center py-4 text-red-500 bg-red-50 rounded-lg">{error}</div>
      )}

      {!loading && !error && (
        <div className="flex flex-col gap-3">
          {fridges.map((fridge) => (
            <FridgeCard key={fridge.id} fridge={fridge} onSelect={handleSelectFridge} />
          ))}
        </div>
      )}
    </div>
  )
}

export default Heladeras