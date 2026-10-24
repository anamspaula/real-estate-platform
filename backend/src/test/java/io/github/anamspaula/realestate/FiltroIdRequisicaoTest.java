package io.github.anamspaula.realestate;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class FiltroIdRequisicaoTest {

    private final FiltroIdRequisicao filtro = new FiltroIdRequisicao();

    @Test
    void geraIdQuandoRequisicaoNaoInformaNenhum() throws Exception {
        var resposta = new MockHttpServletResponse();

        filtro.doFilter(new MockHttpServletRequest(), resposta, new MockFilterChain());

        assertThat(resposta.getHeader(FiltroIdRequisicao.CABECALHO)).isNotBlank();
    }

    @Test
    void reaproveitaIdValidoRecebido() throws Exception {
        var requisicao = new MockHttpServletRequest();
        requisicao.addHeader(FiltroIdRequisicao.CABECALHO, "abc-123");
        var resposta = new MockHttpServletResponse();

        filtro.doFilter(requisicao, resposta, new MockFilterChain());

        assertThat(resposta.getHeader(FiltroIdRequisicao.CABECALHO)).isEqualTo("abc-123");
    }

    @Test
    void substituiIdComConteudoMalicioso() throws Exception {
        var requisicao = new MockHttpServletRequest();
        requisicao.addHeader(FiltroIdRequisicao.CABECALHO, "abc\nlinha de log forjada");
        var resposta = new MockHttpServletResponse();

        filtro.doFilter(requisicao, resposta, new MockFilterChain());

        assertThat(resposta.getHeader(FiltroIdRequisicao.CABECALHO))
                .doesNotContain("\n")
                .isNotEqualTo("abc\nlinha de log forjada");
    }

    @Test
    void limpaMdcAoFinalDaRequisicao() throws Exception {
        filtro.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());

        assertThat(MDC.get(FiltroIdRequisicao.CHAVE_MDC)).isNull();
    }
}
