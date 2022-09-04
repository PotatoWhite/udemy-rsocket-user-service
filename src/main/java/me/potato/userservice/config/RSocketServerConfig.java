package me.potato.userservice.config;

import io.rsocket.metadata.WellKnownMimeType;
import me.potato.userservice.dto.OperationType;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MimeTypeUtils;

@Configuration
public class RSocketServerConfig {
    @Bean
    public RSocketStrategiesCustomizer strategiesCustomizer() {
        var mimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.APPLICATION_CBOR.getString());
        return c -> c.metadataExtractorRegistry(
                r -> r.metadataToExtract(mimeType, OperationType.class, "operation-type")
        );
    }
}
