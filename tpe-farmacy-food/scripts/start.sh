#!/bin/bash
# Start all FarmacyFood microservices

BASE="$(cd "$(dirname "$0")/.." && pwd)"
cd "$BASE"

PID_DIR="/tmp/farmacyfood-pids"
mkdir -p "$PID_DIR"

echo "=== FarmacyFood - Iniciando servicios ==="

echo "[1/9] Iniciando bases de datos..."
docker compose up -d 2>/dev/null || echo "  Docker no disponible, omitiendo BD"

echo "[2/9] Iniciando Eureka (discovery-service :8761)..."
mvn spring-boot:run -pl discovery-service -q &>/tmp/discovery.log &
DISCOVERY_PID=$!
echo "$DISCOVERY_PID" > "$PID_DIR/discovery-service.pid"
sleep 8

for i in $(seq 1 10); do
  if curl -s http://localhost:8761/ > /dev/null 2>&1; then
    echo "  Eureka OK"
    break
  fi
  sleep 3
done

SERVICES=(
  "api-gateway:8080"
  "product-service:8081"
  "fridge-service:8082"
  "order-service:8083"
  "kitchen-service:8084"
  "recommendation-service:8085"
  "user-service:8086"
  "notification-service:8087"
)

PIDS=()
for entry in "${SERVICES[@]}"; do
  svc="${entry%%:*}"
  port="${entry##*:}"
  echo "[*] Iniciando $svc (:${port})..."
  mvn spring-boot:run -pl "$svc" -q &>"/tmp/${svc}.log" &
  pid=$!
  echo "$pid" > "$PID_DIR/${svc}.pid"
  PIDS+=($pid)
done

echo ""
echo "=== Esperando que todos los servicios esten listos... ==="
sleep 30

echo ""
echo "=== Verificando ==="
for entry in "${SERVICES[@]}"; do
  svc="${entry%%:*}"
  port="${entry##*:}"
  code=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:${port}/actuator/health" 2>/dev/null || echo "---")
  printf "  %-25s :%-5s -> HTTP %s\n" "$svc" "$port" "$code"
done

echo ""
echo "=== Eureka UI: http://localhost:8761 ==="
echo "=== Gateway:   http://localhost:8080 ==="
echo "=== Swagger:   http://localhost:8080/swagger-ui.html ==="
echo ""
echo "Para detener: ./scripts/stop.sh"
echo "Logs en /tmp/*.log"
echo "PIDs en $PID_DIR/"