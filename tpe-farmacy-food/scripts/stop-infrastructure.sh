#!/bin/bash

# Detiene discovery-service y api-gateway

PID_DIR="/tmp/farmacyfood-pids"

echo "Buscando procesos de Spring Boot..."

SERVICES=("discovery-service" "api-gateway")
pids=""

for svc in "${SERVICES[@]}"; do
  if [ -f "$PID_DIR/${svc}.pid" ]; then
    pid=$(cat "$PID_DIR/${svc}.pid" 2>/dev/null)
    if [ -n "$pid" ]; then
      pids="$pids $pid"
      echo "  Encontrado $svc (PID: $pid) via pidfile"
    fi
    rm -f "$PID_DIR/${svc}.pid"
  fi
done

if [ -z "$pids" ]; then
  pids=$(pgrep -f "spring-boot:run.*\(discovery-service\|api-gateway\)" 2>/dev/null || true)
fi

if [ -z "$pids" ]; then
    echo "No se encontraron procesos corriendo."
    exit 0
fi

echo "Deteniendo procesos:$pids"
kill $pids 2>/dev/null || true

sleep 2

remaining=""
for pid in $pids; do
  if kill -0 "$pid" 2>/dev/null; then
    remaining="$remaining $pid"
  fi
done

if [ -n "$remaining" ]; then
    echo "Forzando detencion:$remaining"
    kill -9 $remaining 2>/dev/null || true
fi

echo "Discovery Service y API Gateway detenidos."