function DatePicker({ availableDates, selectedDate, onSelectDate }) {
  const today = new Date().toISOString().split('T')[0]

  return (
    <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
      {availableDates.length > 0 ? (
        availableDates.map((date) => (
          <button
            key={date}
            onClick={() => onSelectDate(date)}
            className={`px-3 py-2 rounded-lg text-xs font-medium transition-colors whitespace-nowrap ${
              selectedDate === date
                ? 'bg-green-600 text-white'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
            }`}
          >
            {new Date(date + 'T12:00:00').toLocaleDateString('es-AR', { day: '2-digit', month: 'short' })}
          </button>
        ))
      ) : (
        <span className="text-xs text-gray-400">No hay fechas disponibles</span>
      )}
    </div>
  )
}

export default DatePicker