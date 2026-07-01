package com.farmacyfood.audit.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Helper centralizado para que los servicios emitan eventos de auditoría
 * sin duplicar el patrón try-catch ni la serialización JSON.
 *
 * <p>Uso típico:
 * <pre>{@code
 * @Autowired
 * private AuditLogger auditLogger;
 *
 * public Response crearAlgo(Request req) {
 *     try {
 *         Response res = // lógica de negocio
 *         auditLogger.success("CREATE_SOMETHING", AuditMessages.SOMETHING_CREATED, res);
 *         return res;
 *     } catch (Exception e) {
 *         auditLogger.error("CREATE_SOMETHING", "Error al crear", e.getMessage());
 *         throw e;
 *     }
 * }
 * }</pre>
 */
@Slf4j
@Component
public class AuditLogger {

    private final Optional<AuditClient> auditClient;
    private final ObjectMapper objectMapper;
    private final String serviceName;

    @Autowired
    public AuditLogger(Optional<AuditClient> auditClient,
                       ObjectMapper objectMapper,
                       @Value("${audit.service-name:UNKNOWN}") String serviceName) {
        this.auditClient = auditClient;
        this.objectMapper = objectMapper;
        this.serviceName = serviceName;
    }

    // ---- API pública ----

    /**
     * Registra un evento exitoso (SUCCESS).
     *
     * @param action  Acción realizada (ej: "CREATE_ORDER")
     * @param message Mensaje legible del evento
     * @param detail  Objeto a serializar como detalle (puede ser null)
     */
    public void success(String action, String message, Object detail) {
        log(EventType.SUCCESS, action, message, detail);
    }

    /**
     * Registra un evento de error (ERROR).
     *
     * @param action  Acción que falló (ej: "PAY_ORDER")
     * @param message Mensaje del error
     * @param detail  Detalle del error (puede ser un String o un objeto)
     */
    public void error(String action, String message, Object detail) {
        log(EventType.ERROR, action, message, detail);
    }

    /**
     * Registra un evento informativo (INFO).
     *
     * @param action  Acción relacionada
     * @param message Mensaje informativo
     * @param detail  Detalle opcional
     */
    public void info(String action, String message, Object detail) {
        log(EventType.INFO, action, message, detail);
    }

    /**
     * Registra un evento pendiente (PENDING).
     *
     * @param action  Acción en proceso
     * @param message Mensaje del estado pendiente
     * @param detail  Detalle opcional
     */
    public void pending(String action, String message, Object detail) {
        log(EventType.PENDING, action, message, detail);
    }

    // ---- Método interno ----

    private void log(EventType type, String action, String message, Object detail) {
        auditClient.ifPresent(client -> {
            try {
                AuditEventRequestDTO dto = new AuditEventRequestDTO(
                        serviceName,
                        type.name(),
                        action,
                        message,
                        toJson(detail)
                );
                client.registrarEvento(dto);
            } catch (Exception e) {
                log.warn("Error al registrar auditoría para {} [{}]: {}", action, type, e.getMessage());
            }
        });
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        if (obj instanceof String) return (String) obj;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Error serializando detalle de auditoría: {}", e.getMessage());
            return "{}";
        }
    }
}
