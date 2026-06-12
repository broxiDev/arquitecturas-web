import { useState, useEffect } from 'react'
import useNotificationStore from './notificationStore'
import NotificationList from './NotificationList'
import SubscriptionForm from './SubscriptionForm'

const USUARIOS = [
  { id: 'u001', name: 'Maria Gonzalez' },
  { id: 'u002', name: 'Juan Perez' },
  { id: 'u003', name: 'Carolina Ruiz' },
]

function Notificaciones() {
  const { notifications, unreadCount, loading, error, fetchNotifications, markAsRead } = useNotificationStore()
  const [activeTab, setActiveTab] = useState('lista')
  const [selectedUser, setSelectedUser] = useState('u001')

  useEffect(() => {
    fetchNotifications(selectedUser)
  }, [selectedUser])

  const handleMarkAsRead = async (id) => {
    await markAsRead(id)
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold text-gray-800">
          Notificaciones
          {unreadCount > 0 && (
            <span className="ml-2 inline-flex items-center justify-center bg-green-600 text-white text-xs font-bold rounded-full w-5 h-5">
              {unreadCount}
            </span>
          )}
        </h2>
      </div>

      <div className="mb-3">
        <div className="flex gap-2 overflow-x-auto pb-1">
          {USUARIOS.map((u) => (
            <button
              key={u.id}
              onClick={() => setSelectedUser(u.id)}
              className={`px-3 py-1.5 rounded-full text-xs font-medium whitespace-nowrap transition-colors ${
                selectedUser === u.id
                  ? 'bg-green-600 text-white'
                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
              }`}
            >
              {u.name}
            </button>
          ))}
        </div>
      </div>

      <div className="flex border-b border-gray-200 mb-4">
        <button
          onClick={() => setActiveTab('lista')}
          className={`flex-1 py-2.5 text-sm font-medium text-center border-b-2 transition-colors ${
            activeTab === 'lista'
              ? 'border-green-600 text-green-700'
              : 'border-transparent text-gray-500 hover:text-gray-700'
          }`}
        >
          Notificaciones
        </button>
        <button
          onClick={() => setActiveTab('suscribirse')}
          className={`flex-1 py-2.5 text-sm font-medium text-center border-b-2 transition-colors ${
            activeTab === 'suscribirse'
              ? 'border-green-600 text-green-700'
              : 'border-transparent text-gray-500 hover:text-gray-700'
          }`}
        >
          Suscribirse
        </button>
      </div>

      {activeTab === 'lista' && (
        <div>
          {loading && <div className="text-center py-8 text-gray-500">Cargando notificaciones...</div>}
          {error && <div className="text-center py-4 text-red-500 bg-red-50 rounded-lg">{error}</div>}
          {!loading && (
            <NotificationList notifications={notifications} onMarkAsRead={handleMarkAsRead} />
          )}
        </div>
      )}

      {activeTab === 'suscribirse' && <SubscriptionForm />}
    </div>
  )
}

export default Notificaciones