#!/bin/bash

# Detiene discovery-service y api-gateway

echo "Buscando procesos de Spring Boot..."

pids=$(ps aux | grep -E "discovery-service|api-gateway" | grep "spring-boot:run" | grep -v grep | awk '{print $2}')

if [ -z "$pids" ]; then
    echo "No se encontraron procesos corriendo."
    exit 0
fi

echo "Deteniendo procesos: $pids"
kill $pids 2>/dev/null

sleep 2

remaining=$(ps aux | grep -E "discovery-service|api-gateway" | grep "spring-boot:run" | grep -v grep | awk '{print $2}')
if [ -n "$remaining" ]; then
    echo "Forzando detención: $remaining"
    kill -9 $remaining 2>/dev/null
fi

echo "✓ Discovery Service y API Gateway detenidos."
