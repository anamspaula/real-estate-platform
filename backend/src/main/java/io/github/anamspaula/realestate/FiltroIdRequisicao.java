package io.github.anamspaula.realestate;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class FiltroIdRequisicao extends OncePerRequestFilter {

    static final String CABECALHO = "X-Request-Id";
    static final String CHAVE_MDC = "requisicao";

    // Aceita só valores simples, para que um cabeçalho malicioso não injete texto nos logs
    private static final Pattern FORMATO_VALIDO = Pattern.compile("[A-Za-z0-9-]{1,64}");

    @Override
    protected void doFilterInternal(HttpServletRequest requisicao, HttpServletResponse resposta,
            FilterChain cadeia) throws ServletException, IOException {

        String id = Optional.ofNullable(requisicao.getHeader(CABECALHO))
                .filter(valor -> FORMATO_VALIDO.matcher(valor).matches())
                .orElseGet(() -> UUID.randomUUID().toString());

        MDC.put(CHAVE_MDC, id);
        resposta.setHeader(CABECALHO, id);
        try {
            cadeia.doFilter(requisicao, resposta);
        } finally {
            // A thread volta para o pool e atende outra requisição; o valor não pode vazar
            MDC.remove(CHAVE_MDC);
        }
    }
}
