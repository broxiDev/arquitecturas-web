function NotificationList({ notifications, onMarkAsRead }) {
  if (notifications.length === 0) {
    return (
      <div className="text-center py-8 text-gray-500 text-sm">
        No tenes notificaciones.
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-2">
      {notifications.map((notif) => (
        <div
          key={notif.id}
          className={`rounded-xl border p-3 transition-colors ${
            notif.read
              ? 'bg-white border-gray-200'
              : 'bg-green-50 border-green-200'
          }`}
        >
          <div className="flex items-start gap-3">
            <div className={`w-2.5 h-2.5 rounded-full mt-1.5 flex-shrink-0 ${
              notif.read ? 'bg-gray-300' : 'bg-green-500'
            }`} />
            <div className="flex-1 min-w-0">
              <p className={`text-sm ${notif.read ? 'text-gray-600' : 'text-gray-900 font-medium'}`}>
                {notif.message}
              </p>
              <div className="flex items-center gap-2 mt-1">
                <span className="text-xs text-gray-400">
                  {new Date(notif.createdAt).toLocaleDateString('es-AR', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' })}
                </span>
              </div>
            </div>
            {!notif.read && (
              <button
                onClick={() => onMarkAsRead(notif.id)}
                className="text-xs text-green-600 hover:text-green-800 font-medium whitespace-nowrap"
              >
                Marcar leido
              </button>
            )}
          </div>
        </div>
      ))}
    </div>
  )
}

export default NotificationList