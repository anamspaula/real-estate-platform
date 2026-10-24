package io.github.anamspaula.realestate;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ConfiguracaoOpenApi {

    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info().title("Real Estate Platform API").version("v1"))
                .servers(List.of(new Server().url("/api")));
    }
}
