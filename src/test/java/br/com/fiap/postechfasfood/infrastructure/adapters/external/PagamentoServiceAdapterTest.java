package br.com.fiap.postechfasfood.infrastructure.adapters.external;

import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoServiceAdapterTest {

    private PagamentoServiceAdapter pagamentoServiceAdapter;

    @BeforeEach
    void setUp() {
        pagamentoServiceAdapter = new PagamentoServiceAdapter();
    }

    @Test
    @DisplayName("Deve retornar status PENDENTE para pedido sem pagamento")
    void deveRetornarStatusPendenteParaPedidoSemPagamento() {
        // Arrange
        Integer numeroPedido = 999;

        // Act
        StatusPagamento status = pagamentoServiceAdapter.consultarStatusPagamento(numeroPedido);

        // Assert
        assertEquals(StatusPagamento.PENDENTE, status);
    }

    @Test
    @DisplayName("Deve iniciar pagamento e retornar transação ID")
    void deveIniciarPagamentoERetornarTransacaoId() {
        // Arrange
        Integer numeroPedido = 123;
        double valor = 50.0;

        // Act
        String transacaoId = pagamentoServiceAdapter.iniciarPagamento(numeroPedido, valor);

        // Assert
        assertNotNull(transacaoId);
        assertFalse(transacaoId.isEmpty());

        // Verifica se o status foi definido como PENDENTE
        StatusPagamento status = pagamentoServiceAdapter.consultarStatusPagamento(numeroPedido);
        assertEquals(StatusPagamento.PENDENTE, status);
    }

    @Test
    @DisplayName("Deve retornar false para pagamento não aprovado")
    void deveRetornarFalseParaPagamentoNaoAprovado() {
        // Arrange
        Integer numeroPedido = 456;
        pagamentoServiceAdapter.iniciarPagamento(numeroPedido, 30.0);

        // Act
        boolean isAprovado = pagamentoServiceAdapter.isPagamentoAprovado(numeroPedido);

        // Assert
        assertFalse(isAprovado);
    }

    @Test
    @DisplayName("Deve retornar true para pagamento aprovado via webhook")
    void deveRetornarTrueParaPagamentoAprovadoViaWebhook() {
        // Arrange
        Integer numeroPedido = 789;
        pagamentoServiceAdapter.iniciarPagamento(numeroPedido, 75.0);

        // Simular webhook de aprovação
        pagamentoServiceAdapter.simularWebhookPagamento(numeroPedido, StatusPagamento.APROVADO);

        // Act
        boolean isAprovado = pagamentoServiceAdapter.isPagamentoAprovado(numeroPedido);
        StatusPagamento status = pagamentoServiceAdapter.consultarStatusPagamento(numeroPedido);

        // Assert
        assertTrue(isAprovado);
        assertEquals(StatusPagamento.APROVADO, status);
    }

    @Test
    @DisplayName("Deve simular webhook de pagamento recusado")
    void deveSimularWebhookDePagamentoRecusado() {
        // Arrange
        Integer numeroPedido = 101;
        pagamentoServiceAdapter.iniciarPagamento(numeroPedido, 25.0);

        // Act
        pagamentoServiceAdapter.simularWebhookPagamento(numeroPedido, StatusPagamento.RECUSADO);

        // Assert
        StatusPagamento status = pagamentoServiceAdapter.consultarStatusPagamento(numeroPedido);
        assertEquals(StatusPagamento.RECUSADO, status);
        assertFalse(pagamentoServiceAdapter.isPagamentoAprovado(numeroPedido));
    }

    @Test
    @DisplayName("Deve consultar status de pagamento inexistente")
    void deveConsultarStatusDePagamentoInexistente() {
        // Arrange
        Integer numeroPedidoInexistente = 999999;

        // Act
        StatusPagamento status = pagamentoServiceAdapter.consultarStatusPagamento(numeroPedidoInexistente);

        // Assert
        assertEquals(StatusPagamento.PENDENTE, status);
    }
}
