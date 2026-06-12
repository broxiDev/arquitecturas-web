# FarmacyFood — Guías de Estilo del Producto

## Tono de Comunicación
**Cercano y moderno.** El sistema se comunica con los usuarios en un lenguaje amigable y actual, como corresponde a una app de estilo de vida. Los mensajes deben sentirse personales y motivadores, no fríos ni técnicos.

## Identidad de Marca
**Saludable y natural.** La marca transmite frescura, bienestar y un estilo de vida activo. Se utilizan colores verdes, tonos tierra y elementos visuales que evoquen naturaleza y alimentos saludables.

## Principios de Experiencia de Usuario
- **Simplicidad ante todo:** Cada flujo debe completarse en la menor cantidad de pasos posible. El usuario no debe pensar más de lo necesario.
- **Consistencia visual y funcional:** Patrones uniformes en todas las pantallas y microservicios. Mismos nombres para mismas acciones.
- **Feedback inmediato:** Toda acción del usuario genera una respuesta visible del sistema en menos de 1 segundo.

## Principios de Diseño de API
- **RESTful:** APIs diseñadas con recursos bien definidos y verbos HTTP estándar (GET, POST, PUT, DELETE).
- **Documentación con OpenAPI/Swagger:** Cada microservicio expone su documentación interactiva desde el inicio.
- **Versionado:** Las APIs incluyen versión en la URL (ej. /api/v1/productos) para mantener compatibilidad hacia atrás.

## Convenciones de Código
- **Idioma:** Código fuente, comentarios y mensajes de commit en español.
- **Nomenclatura de APIs:** URLs en plural y snake_case para parámetros (ej. /api/v1/productos?categoria=vegano).
- **Mensajes de respuesta:** En español, usando el tono cercano y moderno definido.
