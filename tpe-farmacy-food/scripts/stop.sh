#!/bin/bash
# Stop all FarmacyFood microservices

BASE="$(cd "$(dirname "$0")/.." && pwd)"
cd "$BASE"

PID_DIR="/tmp/farmacyfood-pids"

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
  pid=""
  if [ -f "$PID_DIR/${svc}.pid" ]; then
    pid=$(cat "$PID_DIR/${svc}.pid" 2>/dev/null)
  fi
  if [ -z "$pid" ]; then
    pid=$(pgrep -f "spring-boot:run.*${svc}" 2>/dev/null || true)
  fi
  if [ -n "$pid" ]; then
    echo "  Deteniendo $svc (PID: $pid)..."
    kill $pid 2>/dev/null || true
  fi
  rm -f "$PID_DIR/${svc}.pid"
done

sleep 2
remaining=$(pgrep -f "com\.farmacyfood\." 2>/dev/null || true)
if [ -n "$remaining" ]; then
  echo "  Limpiando procesos restantes..."
  kill $remaining 2>/dev/null || true
fi

echo "  Docker Compose..."
docker compose down 2>/dev/null || true

rm -rf "$PID_DIR"

echo "=== Servicios detenidos ==="