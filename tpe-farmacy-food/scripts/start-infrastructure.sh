#!/bin/bash

# Levanta discovery-service, api-gateway y opcionalmente kitchen-service
# Uso: ./start-infrastructure.sh [--with-kitchen]

set -e

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

echo "=== Levantando Discovery Service (puerto 8761) ==="
cd "$PROJECT_DIR/discovery-service"
mvn spring-boot:run &
DISCOVERY_PID=$!

echo "Esperando a que Eureka esté listo..."
until curl -s http://localhost:8761/actuator/health > /dev/null 2>&1; do
    sleep 2
done
echo "✓ Discovery Service listo"

echo ""
echo "=== Levantando API Gateway (puerto 8080) ==="
cd "$PROJECT_DIR/api-gateway"
mvn spring-boot:run &
GATEWAY_PID=$!

echo "Esperando a que Gateway esté listo..."
until curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; do
    sleep 2
done
echo "✓ API Gateway listo"

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
    echo "Para detener: kill $DISCOVERY_PID $GATEWAY_PID"
    wait
fi
