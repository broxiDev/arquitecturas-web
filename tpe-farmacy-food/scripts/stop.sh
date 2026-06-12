#!/bin/bash
# Stop all FarmacyFood microservices
set -e

BASE="$(cd "$(dirname "$0")/.." && pwd)"
cd "$BASE"

echo "=== FarmacyFood - Deteniendo servicios ==="

SERVICES=(
  "discovery-service"
  "api-gateway"
  "product-service"
  "fridge-service"
  "order-service"
  "kitchen-service"
  "recommendation-service"
  "user-service"
  "notification-service"
)

for svc in "${SERVICES[@]}"; do
  pids=$(ps aux | grep "spring-boot:run.*${svc}" | grep -v grep | awk '{print $2}')
  if [ -n "$pids" ]; then
    echo "  Deteniendo $svc (PID: $pids)..."
    kill $pids 2>/dev/null
  fi
done

# Also kill any remaining java processes from this project
sleep 2
remaining=$(ps aux | grep -E "com\.farmacyfood\." | grep -v grep | awk '{print $2}')
if [ -n "$remaining" ]; then
  echo "  Limpiando procesos restantes..."
  kill $remaining 2>/dev/null
fi

echo "  Docker Compose..."
docker compose down 2>/dev/null || true

echo "=== Servicios detenidos ==="
