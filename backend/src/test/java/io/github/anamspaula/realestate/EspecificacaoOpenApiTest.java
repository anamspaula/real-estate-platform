package io.github.anamspaula.realestate;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class EspecificacaoOpenApiTest {

    @Value("${local.server.port}")
    private int porta;

    @Test
    void exportaEspecificacao() throws Exception {
        var requisicao = HttpRequest.newBuilder(
                URI.create("http://localhost:" + porta + "/api/v3/api-docs.yaml")).build();

        var resposta = HttpClient.newHttpClient()
                .send(requisicao, HttpResponse.BodyHandlers.ofString());

        assertThat(resposta.statusCode()).isEqualTo(200);
        Files.writeString(Path.of("../docs/api/openapi.yaml"), resposta.body());
    }
}
