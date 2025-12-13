package br.com.fiap.postechfasfood.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Value Object StatusPedido")
class StatusPedidoTest {

    @Test
    @DisplayName("Deve retornar descrição do status")
    void deveRetornarDescricaoDoStatus() {
        assertEquals("Aguardando Pagamento", StatusPedido.AGUARDANDO_PAGAMENTO.getDescricao());
        assertEquals("Recebido", StatusPedido.RECEBIDO.getDescricao());
        assertEquals("Em Preparação", StatusPedido.EM_PREPARACAO.getDescricao());
        assertEquals("Pronto", StatusPedido.PRONTO.getDescricao());
        assertEquals("Finalizado", StatusPedido.FINALIZADO.getDescricao());
        assertEquals("Cancelado", StatusPedido.CANCELADO.getDescricao());
    }

    @Test
    @DisplayName("Deve permitir transição de AGUARDANDO_PAGAMENTO para RECEBIDO")
    void devePermitirTransicaoAguardandoPagamentoParaRecebido() {
        assertTrue(StatusPedido.AGUARDANDO_PAGAMENTO.podeTransicionarPara(StatusPedido.RECEBIDO));
    }

    @Test
    @DisplayName("Deve permitir transição de AGUARDANDO_PAGAMENTO para CANCELADO")
    void devePermitirTransicaoAguardandoPagamentoParaCancelado() {
        assertTrue(StatusPedido.AGUARDANDO_PAGAMENTO.podeTransicionarPara(StatusPedido.CANCELADO));
    }

    @Test
    @DisplayName("Não deve permitir transição de AGUARDANDO_PAGAMENTO para EM_PREPARACAO")
    void naoDevePermitirTransicaoAguardandoPagamentoParaEmPreparacao() {
        assertFalse(StatusPedido.AGUARDANDO_PAGAMENTO.podeTransicionarPara(StatusPedido.EM_PREPARACAO));
    }

    @Test
    @DisplayName("Deve permitir transição de RECEBIDO para EM_PREPARACAO")
    void devePermitirTransicaoRecebidoParaEmPreparacao() {
        assertTrue(StatusPedido.RECEBIDO.podeTransicionarPara(StatusPedido.EM_PREPARACAO));
    }

    @Test
    @DisplayName("Deve permitir transição de RECEBIDO para CANCELADO")
    void devePermitirTransicaoRecebidoParaCancelado() {
        assertTrue(StatusPedido.RECEBIDO.podeTransicionarPara(StatusPedido.CANCELADO));
    }

    @Test
    @DisplayName("Não deve permitir transição de RECEBIDO para PRONTO")
    void naoDevePermitirTransicaoRecebidoParaPronto() {
        assertFalse(StatusPedido.RECEBIDO.podeTransicionarPara(StatusPedido.PRONTO));
    }

    @Test
    @DisplayName("Deve permitir transição de EM_PREPARACAO para PRONTO")
    void devePermitirTransicaoEmPreparacaoParaPronto() {
        assertTrue(StatusPedido.EM_PREPARACAO.podeTransicionarPara(StatusPedido.PRONTO));
    }

    @Test
    @DisplayName("Deve permitir transição de EM_PREPARACAO para CANCELADO")
    void devePermitirTransicaoEmPreparacaoParaCancelado() {
        assertTrue(StatusPedido.EM_PREPARACAO.podeTransicionarPara(StatusPedido.CANCELADO));
    }

    @Test
    @DisplayName("Não deve permitir transição de EM_PREPARACAO para RECEBIDO")
    void naoDevePermitirTransicaoEmPreparacaoParaRecebido() {
        assertFalse(StatusPedido.EM_PREPARACAO.podeTransicionarPara(StatusPedido.RECEBIDO));
    }

    @Test
    @DisplayName("Deve permitir transição de PRONTO para FINALIZADO")
    void devePermitirTransicaoProntoParaFinalizado() {
        assertTrue(StatusPedido.PRONTO.podeTransicionarPara(StatusPedido.FINALIZADO));
    }

    @Test
    @DisplayName("Não deve permitir transição de PRONTO para CANCELADO")
    void naoDevePermitirTransicaoProntoParaCancelado() {
        assertFalse(StatusPedido.PRONTO.podeTransicionarPara(StatusPedido.CANCELADO));
    }

    @Test
    @DisplayName("Não deve permitir nenhuma transição a partir de FINALIZADO")
    void naoDevePermitirTransicaoAPartirDeFinalizado() {
        assertFalse(StatusPedido.FINALIZADO.podeTransicionarPara(StatusPedido.RECEBIDO));
        assertFalse(StatusPedido.FINALIZADO.podeTransicionarPara(StatusPedido.EM_PREPARACAO));
        assertFalse(StatusPedido.FINALIZADO.podeTransicionarPara(StatusPedido.PRONTO));
        assertFalse(StatusPedido.FINALIZADO.podeTransicionarPara(StatusPedido.CANCELADO));
        assertFalse(StatusPedido.FINALIZADO.podeTransicionarPara(StatusPedido.AGUARDANDO_PAGAMENTO));
    }

    @Test
    @DisplayName("Não deve permitir nenhuma transição a partir de CANCELADO")
    void naoDevePermitirTransicaoAPartirDeCancelado() {
        assertFalse(StatusPedido.CANCELADO.podeTransicionarPara(StatusPedido.RECEBIDO));
        assertFalse(StatusPedido.CANCELADO.podeTransicionarPara(StatusPedido.EM_PREPARACAO));
        assertFalse(StatusPedido.CANCELADO.podeTransicionarPara(StatusPedido.PRONTO));
        assertFalse(StatusPedido.CANCELADO.podeTransicionarPara(StatusPedido.FINALIZADO));
        assertFalse(StatusPedido.CANCELADO.podeTransicionarPara(StatusPedido.AGUARDANDO_PAGAMENTO));
    }

    @Test
    @DisplayName("Deve criar status a partir de string válida")
    void deveCriarStatusAPartirDeStringValida() {
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, StatusPedido.fromString("AGUARDANDO_PAGAMENTO"));
        assertEquals(StatusPedido.RECEBIDO, StatusPedido.fromString("RECEBIDO"));
        assertEquals(StatusPedido.EM_PREPARACAO, StatusPedido.fromString("EM_PREPARACAO"));
        assertEquals(StatusPedido.PRONTO, StatusPedido.fromString("PRONTO"));
        assertEquals(StatusPedido.FINALIZADO, StatusPedido.fromString("FINALIZADO"));
        assertEquals(StatusPedido.CANCELADO, StatusPedido.fromString("CANCELADO"));
    }

    @Test
    @DisplayName("Deve criar status a partir de string case insensitive")
    void deveCriarStatusAPartirDeStringCaseInsensitive() {
        assertEquals(StatusPedido.RECEBIDO, StatusPedido.fromString("recebido"));
        assertEquals(StatusPedido.EM_PREPARACAO, StatusPedido.fromString("em_preparacao"));
        assertEquals(StatusPedido.PRONTO, StatusPedido.fromString("Pronto"));
    }

    @Test
    @DisplayName("Deve lançar exceção para string inválida")
    void deveLancarExcecaoParaStringInvalida() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StatusPedido.fromString("INVALIDO")
        );

        assertEquals("Status inválido: INVALIDO", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para string nula ou vazia")
    void deveLancarExcecaoParaStringNulaOuVazia() {
        assertThrows(IllegalArgumentException.class, () -> StatusPedido.fromString(null));
        assertThrows(IllegalArgumentException.class, () -> StatusPedido.fromString(""));
    }

    @ParameterizedTest
    @EnumSource(StatusPedido.class)
    @DisplayName("Deve verificar que todos os status têm descrição não nula")
    void deveVerificarQueTodosStatusTemDescricaoNaoNula(StatusPedido status) {
        assertNotNull(status.getDescricao());
        assertFalse(status.getDescricao().isEmpty());
    }

    @Test
    @DisplayName("Deve ter exatamente 6 valores de status")
    void deveTerSeisValoresDeStatus() {
        assertEquals(6, StatusPedido.values().length);
    }
}
