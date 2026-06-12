const USUARIOS = [
  { id: 'u001', name: 'Maria Gonzalez' },
  { id: 'u002', name: 'Juan Perez' },
  { id: 'u003', name: 'Carolina Ruiz' },
]

function UserSelector({ selectedUser, onSelectUser }) {
  return (
    <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
      {USUARIOS.map((u) => (
        <button
          key={u.id}
          onClick={() => onSelectUser(u.id)}
          className={`px-3 py-1.5 rounded-full text-sm font-medium transition-colors whitespace-nowrap ${
            selectedUser === u.id
              ? 'bg-green-600 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          }`}
        >
          {u.name}
        </button>
      ))}
    </div>
  )
}

export default UserSelector