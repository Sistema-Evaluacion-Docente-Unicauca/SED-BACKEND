package co.edu.unicauca.sed.api.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente que se encarga de comunicarse con el microservicio de notificaciones,
 * permitiendo enviar correos electrónicos con contenido en UTF-8.
 */
@Service
public class ClienteNotificacion {

    private static final Logger logger = LoggerFactory.getLogger(ClienteNotificacion.class);

    /**
     * Cliente HTTP utilizado para la comunicación con el microservicio.
     */
    private final RestTemplate restTemplate;

    /**
     * URL base del microservicio de notificaciones, obtenida desde el application.yml.
     */
    @Value("${notification.service.url}")
    private String urlServicioNotificaciones;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param restTemplate Cliente HTTP proporcionado por Spring.
     */
    public ClienteNotificacion(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Envía una notificación al microservicio de notificaciones.
     *
     * @param correos Lista de correos electrónicos de los destinatarios.
     * @param asunto  Asunto del correo.
     * @param mensaje Cuerpo del correo, con caracteres especiales y tildes.
     */
    public void enviarNotificacion(List<String> correos, String asunto, String mensaje) {
        try {
            // Construir el cuerpo de la solicitud
            Map<String, Object> cuerpoSolicitud = new HashMap<>();
            cuerpoSolicitud.put("correos", correos);
            cuerpoSolicitud.put("asunto", asunto);
            cuerpoSolicitud.put("mensaje", mensaje);
            cuerpoSolicitud.put("documentos", Collections.emptyMap());

            // Configurar cabeceras HTTP para forzar el uso de UTF-8
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(cuerpoSolicitud, headers);

            // Enviar la solicitud POST al microservicio
            restTemplate.postForEntity(urlServicioNotificaciones, requestEntity, Void.class);

            logger.info("✅ Notificación enviada correctamente a: {}", correos);
        } catch (Exception e) {
            logger.error("❌ Error al enviar la notificación al microservicio de notificaciones: {}", e.getMessage(), e);
        }
    }
}
