import { useEffect } from 'react'
import useRecommendationStore from './recommendationStore'
import RecommendationCard from './RecommendationCard'
import UserSelector from './UserSelector'

function Recomendaciones({ onNavigateToCatalogo }) {
  const { recommendations, selectedUser, loading, error, fetchRecommendations, setSelectedUser } = useRecommendationStore()

  useEffect(() => {
    if (selectedUser) {
      fetchRecommendations(selectedUser)
    }
  }, [selectedUser])

  const handleSelectUser = (userId) => {
    setSelectedUser(userId)
  }

  const handleViewProduct = (recommendation) => {
    if (onNavigateToCatalogo) {
      onNavigateToCatalogo()
    }
  }

  return (
    <div>
      <h2 className="text-lg font-semibold text-gray-800 mb-4">Recomendaciones</h2>

      <div className="mb-4">
        <span className="text-xs text-gray-500 block mb-2">Selecciona un usuario para ver sus recomendaciones</span>
        <UserSelector selectedUser={selectedUser} onSelectUser={handleSelectUser} />
      </div>

      {!selectedUser && (
        <div className="text-center py-8 text-gray-500 text-sm">
          Selecciona un usuario para ver sus recomendaciones personalizadas.
        </div>
      )}

      {loading && (
        <div className="text-center py-8 text-gray-500">Cargando recomendaciones...</div>
      )}

      {error && (
        <div className="text-center py-4 text-red-500 bg-red-50 rounded-lg">{error}</div>
      )}

      {selectedUser && !loading && !error && (
        <div className="flex flex-col gap-3">
          {recommendations.length === 0 ? (
            <div className="text-center py-8 text-gray-500 text-sm">
              No hay recomendaciones disponibles para este usuario.
            </div>
          ) : (
            recommendations.map((rec) => (
              <RecommendationCard key={rec.productId} recommendation={rec} onViewProduct={handleViewProduct} />
            ))
          )}
        </div>
      )}
    </div>
  )
}

export default Recomendaciones