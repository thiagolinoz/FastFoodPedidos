package br.com.fiap.postechfasfood.domain.entities;

import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da entidade Pagamento")
class PagamentoTest {

    @Test
    @DisplayName("Deve criar pagamento com construtor vazio")
    void deveCriarPagamentoComConstrutorVazio() {
        // Act
        Pagamento pagamento = new Pagamento();

        // Assert
        assertNotNull(pagamento);
        assertNull(pagamento.getId());
        assertNull(pagamento.getPedidoId());
        assertNull(pagamento.getNumeroPedido());
        assertNull(pagamento.getStatusPagamento());
        assertNull(pagamento.getValor());
        assertNull(pagamento.getDataHoraPagamento());
        assertNull(pagamento.getTransacaoId());
        assertNull(pagamento.getOrigem());
        assertNull(pagamento.getDataCriacao());
        assertNull(pagamento.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve criar pagamento com construtor completo")
    void deveCriarPagamentoComConstrutorCompleto() {
        // Arrange
        String id = "pag-123";
        String pedidoId = "pedido-456";
        Integer numeroPedido = 1;
        StatusPagamento status = StatusPagamento.APROVADO;
        BigDecimal valor = new BigDecimal("50.90");
        LocalDateTime dataHoraPagamento = LocalDateTime.now();
        String transacaoId = "trans-789";
        String origem = "MERCADO_PAGO";
        LocalDateTime dataCriacao = LocalDateTime.now().minusMinutes(5);
        LocalDateTime dataAtualizacao = LocalDateTime.now();

        // Act
        Pagamento pagamento = new Pagamento(
            id, pedidoId, numeroPedido, status, valor,
            dataHoraPagamento, transacaoId, origem, dataCriacao, dataAtualizacao
        );

        // Assert
        assertEquals(id, pagamento.getId());
        assertEquals(pedidoId, pagamento.getPedidoId());
        assertEquals(numeroPedido, pagamento.getNumeroPedido());
        assertEquals(status, pagamento.getStatusPagamento());
        assertEquals(valor, pagamento.getValor());
        assertEquals(dataHoraPagamento, pagamento.getDataHoraPagamento());
        assertEquals(transacaoId, pagamento.getTransacaoId());
        assertEquals(origem, pagamento.getOrigem());
        assertEquals(dataCriacao, pagamento.getDataCriacao());
        assertEquals(dataAtualizacao, pagamento.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve definir e obter ID corretamente")
    void deveDefinirEObterIdCorretamente() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        String id = "pag-test-123";

        // Act
        pagamento.setId(id);

        // Assert
        assertEquals(id, pagamento.getId());
    }

    @Test
    @DisplayName("Deve definir e obter pedidoId corretamente")
    void deveDefinirEObterPedidoIdCorretamente() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        String pedidoId = "pedido-test-456";

        // Act
        pagamento.setPedidoId(pedidoId);

        // Assert
        assertEquals(pedidoId, pagamento.getPedidoId());
    }

    @Test
    @DisplayName("Deve definir e obter numeroPedido corretamente")
    void deveDefinirEObterNumeroPedidoCorretamente() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        Integer numeroPedido = 42;

        // Act
        pagamento.setNumeroPedido(numeroPedido);

        // Assert
        assertEquals(numeroPedido, pagamento.getNumeroPedido());
    }

    @Test
    @DisplayName("Deve definir e obter statusPagamento corretamente")
    void deveDefinirEObterStatusPagamentoCorretamente() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        StatusPagamento status = StatusPagamento.RECUSADO;

        // Act
        pagamento.setStatusPagamento(status);

        // Assert
        assertEquals(status, pagamento.getStatusPagamento());
    }

    @Test
    @DisplayName("Deve definir e obter valor corretamente")
    void deveDefinirEObterValorCorretamente() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        BigDecimal valor = new BigDecimal("123.45");

        // Act
        pagamento.setValor(valor);

        // Assert
        assertEquals(valor, pagamento.getValor());
    }

    @Test
    @DisplayName("Deve definir e obter dataHoraPagamento corretamente")
    void deveDefinirEObterDataHoraPagamentoCorretamente() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        LocalDateTime dataHora = LocalDateTime.of(2025, 12, 20, 15, 30);

        // Act
        pagamento.setDataHoraPagamento(dataHora);

        // Assert
        assertEquals(dataHora, pagamento.getDataHoraPagamento());
    }

    @Test
    @DisplayName("Deve definir e obter transacaoId corretamente")
    void deveDefinirEObterTransacaoIdCorretamente() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        String transacaoId = "trans-test-789";

        // Act
        pagamento.setTransacaoId(transacaoId);

        // Assert
        assertEquals(transacaoId, pagamento.getTransacaoId());
    }

    @Test
    @DisplayName("Deve definir e obter origem corretamente")
    void deveDefinirEObterOrigemCorretamente() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        String origem = "PIX";

        // Act
        pagamento.setOrigem(origem);

        // Assert
        assertEquals(origem, pagamento.getOrigem());
    }

    @Test
    @DisplayName("Deve definir e obter dataCriacao corretamente")
    void deveDefinirEObterDataCriacaoCorretamente() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        LocalDateTime dataCriacao = LocalDateTime.of(2025, 12, 20, 10, 0);

        // Act
        pagamento.setDataCriacao(dataCriacao);

        // Assert
        assertEquals(dataCriacao, pagamento.getDataCriacao());
    }

    @Test
    @DisplayName("Deve definir e obter dataAtualizacao corretamente")
    void deveDefinirEObterDataAtualizacaoCorretamente() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        LocalDateTime dataAtualizacao = LocalDateTime.of(2025, 12, 20, 12, 0);

        // Act
        pagamento.setDataAtualizacao(dataAtualizacao);

        // Assert
        assertEquals(dataAtualizacao, pagamento.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve considerar pagamentos com mesmo ID como iguais")
    void deveConsiderarPagamentosComMesmoIdComoIguais() {
        // Arrange
        Pagamento pagamento1 = new Pagamento();
        pagamento1.setId("pag-123");
        pagamento1.setValor(new BigDecimal("50.00"));

        Pagamento pagamento2 = new Pagamento();
        pagamento2.setId("pag-123");
        pagamento2.setValor(new BigDecimal("100.00"));

        // Act & Assert
        assertEquals(pagamento1, pagamento2);
        assertEquals(pagamento1.hashCode(), pagamento2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar pagamentos com IDs diferentes como diferentes")
    void deveConsiderarPagamentosComIdsDiferentesComoDiferentes() {
        // Arrange
        Pagamento pagamento1 = new Pagamento();
        pagamento1.setId("pag-123");

        Pagamento pagamento2 = new Pagamento();
        pagamento2.setId("pag-456");

        // Act & Assert
        assertNotEquals(pagamento1, pagamento2);
    }

    @Test
    @DisplayName("Deve retornar true ao comparar pagamento consigo mesmo")
    void deveRetornarTrueAoCompararPagamentoConsigoMesmo() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        pagamento.setId("pag-123");

        // Act & Assert
        assertEquals(pagamento, pagamento);
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com null")
    void deveRetornarFalseAoCompararComNull() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        pagamento.setId("pag-123");

        // Act & Assert
        assertNotEquals(pagamento, null);
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com objeto de classe diferente")
    void deveRetornarFalseAoCompararComObjetoDeClasseDiferente() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        pagamento.setId("pag-123");
        String outroObjeto = "não é pagamento";

        // Act & Assert
        assertNotEquals(pagamento, outroObjeto);
    }

    @Test
    @DisplayName("Deve gerar toString com informações do pagamento")
    void deveGerarToStringComInformacoesDoPagamento() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        pagamento.setId("pag-123");
        pagamento.setNumeroPedido(42);
        pagamento.setStatusPagamento(StatusPagamento.APROVADO);
        pagamento.setValor(new BigDecimal("99.99"));
        pagamento.setOrigem("CARTAO");

        // Act
        String toString = pagamento.toString();

        // Assert
        assertTrue(toString.contains("pag-123"));
        assertTrue(toString.contains("42"));
        assertTrue(toString.contains("APROVADO"));
        assertTrue(toString.contains("99.99"));
        assertTrue(toString.contains("CARTAO"));
    }

    @Test
    @DisplayName("Deve lidar com valores nulos nos getters")
    void deveLidarComValoresNulosNosGetters() {
        // Arrange
        Pagamento pagamento = new Pagamento();

        // Act & Assert - todos os getters devem retornar null sem problemas
        assertNull(pagamento.getId());
        assertNull(pagamento.getPedidoId());
        assertNull(pagamento.getNumeroPedido());
        assertNull(pagamento.getStatusPagamento());
        assertNull(pagamento.getValor());
        assertNull(pagamento.getDataHoraPagamento());
        assertNull(pagamento.getTransacaoId());
        assertNull(pagamento.getOrigem());
        assertNull(pagamento.getDataCriacao());
        assertNull(pagamento.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve aceitar valores nulos nos setters")
    void deveAceitarValoresNulosNosSetters() {
        // Arrange
        Pagamento pagamento = new Pagamento();

        // Act & Assert - todos os setters devem aceitar null
        assertDoesNotThrow(() -> {
            pagamento.setId(null);
            pagamento.setPedidoId(null);
            pagamento.setNumeroPedido(null);
            pagamento.setStatusPagamento(null);
            pagamento.setValor(null);
            pagamento.setDataHoraPagamento(null);
            pagamento.setTransacaoId(null);
            pagamento.setOrigem(null);
            pagamento.setDataCriacao(null);
            pagamento.setDataAtualizacao(null);
        });
    }

    @Test
    @DisplayName("Deve testar todos os status de pagamento")
    void deveTestarTodosOsStatusDePagamento() {
        // Arrange
        Pagamento pagamento = new Pagamento();

        // Test APROVADO
        pagamento.setStatusPagamento(StatusPagamento.APROVADO);
        assertEquals(StatusPagamento.APROVADO, pagamento.getStatusPagamento());

        // Test RECUSADO
        pagamento.setStatusPagamento(StatusPagamento.RECUSADO);
        assertEquals(StatusPagamento.RECUSADO, pagamento.getStatusPagamento());

        // Test PENDENTE
        pagamento.setStatusPagamento(StatusPagamento.PENDENTE);
        assertEquals(StatusPagamento.PENDENTE, pagamento.getStatusPagamento());
    }
}
