function DailyPlanTable({ items }) {
  if (!items || items.length === 0) {
    return <p className="text-gray-500 text-sm text-center py-4">No hay items en el plan del dia.</p>
  }

  const totalItems = items.reduce((sum, item) => sum + item.suggestedQuantity, 0)

  return (
    <div>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-200">
              <th className="text-left py-2.5 px-3 font-medium text-gray-600">Producto</th>
              <th className="text-right py-2.5 px-3 font-medium text-gray-600">Cant. sugerida</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.productId} className="border-b border-gray-50">
                <td className="py-2.5 px-3 text-gray-800">{item.productName}</td>
                <td className="py-2.5 px-3 text-right font-semibold text-green-700">{item.suggestedQuantity} u.</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="mt-3 pt-3 border-t border-gray-200 flex items-center justify-between">
        <span className="text-sm font-medium text-gray-700">Total productos sugeridos</span>
        <span className="text-lg font-bold text-green-700">{totalItems} u.</span>
      </div>
    </div>
  )
}

export default DailyPlanTable