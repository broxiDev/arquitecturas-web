#!/bin/bash

# Levanta discovery-service, api-gateway y opcionalmente kitchen-service
# Uso: ./start-infrastructure.sh [--with-kitchen]

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PID_DIR="/tmp/farmacyfood-pids"
mkdir -p "$PID_DIR"

TIMEOUT=60

echo "=== Levantando Discovery Service (puerto 8761) ==="
cd "$PROJECT_DIR/discovery-service"
mvn spring-boot:run &
DISCOVERY_PID=$!
echo "$DISCOVERY_PID" > "$PID_DIR/discovery-service.pid"

echo "Esperando a que Eureka este listo (timeout: ${TIMEOUT}s)..."
elapsed=0
until curl -s http://localhost:8761/actuator/health > /dev/null 2>&1; do
    sleep 2
    elapsed=$((elapsed + 2))
    if [ "$elapsed" -ge "$TIMEOUT" ]; then
        echo "ERROR: Discovery Service no respondio en ${TIMEOUT}s"
        exit 1
    fi
done
echo "Discovery Service listo"

echo ""
echo "=== Levantando API Gateway (puerto 8080) ==="
cd "$PROJECT_DIR/api-gateway"
mvn spring-boot:run &
GATEWAY_PID=$!
echo "$GATEWAY_PID" > "$PID_DIR/api-gateway.pid"

echo "Esperando a que Gateway este listo (timeout: ${TIMEOUT}s)..."
elapsed=0
until curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; do
    sleep 2
    elapsed=$((elapsed + 2))
    if [ "$elapsed" -ge "$TIMEOUT" ]; then
        echo "ERROR: API Gateway no respondio en ${TIMEOUT}s"
        exit 1
    fi
done
echo "API Gateway listo"

if [ "$1" = "--with-kitchen" ]; then
    echo ""
    echo "=== Levantando Kitchen Service (puerto 8084) ==="
    cd "$PROJECT_DIR/kitchen-service"
    mvn spring-boot:run
else
    echo ""
    echo "Infraestructura lista. Para levantar kitchen-service:"
    echo "  cd kitchen-service && mvn spring-boot:run"
    echo ""
    echo "PIDs: discovery=$DISCOVERY_PID, gateway=$GATEWAY_PID"
    echo "Pidfiles en $PID_DIR/"
    echo "Para detener: ./scripts/stop-infrastructure.sh"
    wait
fi