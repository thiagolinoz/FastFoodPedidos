package br.com.fiap.postechfasfood.domain.entities;

import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da Entidade Pedido")
class PedidoTest {

    private List<ItemPedido> itens;

    @BeforeEach
    void setUp() {
        itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));
        itens.add(new ItemPedido("Refrigerante", "PROD-002", 1, 5.00));
    }

    @Test
    @DisplayName("Deve criar pedido com Builder")
    void deveCriarPedidoComBuilder() {
        // Arrange & Act
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(itens)
                .build();

        // Assert
        assertNotNull(pedido);
        assertNotNull(pedido.getId());
        assertEquals(StatusPedido.RECEBIDO, pedido.getStatus());
        assertEquals(1, pedido.getNumeroPedido());
        assertEquals(2, pedido.getItens().size());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo no Builder")
    void deveLancarExcecaoQuandoIdNulo() {
        // Arrange & Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new Pedido.Builder()
                    .documentoCliente("12345678900")
                    .status(StatusPedido.RECEBIDO)
                    .numeroPedido(1)
                    .build();
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando status é nulo no Builder")
    void deveLancarExcecaoQuandoStatusNulo() {
        // Arrange & Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new Pedido.Builder()
                    .id(UUID.randomUUID().toString())
                    .documentoCliente("12345678900")
                    .numeroPedido(1)
                    .build();
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando número do pedido é nulo no Builder")
    void deveLancarExcecaoQuandoNumeroPedidoNulo() {
        // Arrange & Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new Pedido.Builder()
                    .id(UUID.randomUUID().toString())
                    .documentoCliente("12345678900")
                    .status(StatusPedido.RECEBIDO)
                    .build();
        });
    }

    @Test
    @DisplayName("Deve adicionar item ao pedido")
    void deveAdicionarItemAoPedido() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>())
                .build();

        ItemPedido novoItem = new ItemPedido("Pizza", "PROD-003", 1, 35.00);

        // Act
        pedido.adicionarItem(novoItem);

        // Assert
        assertEquals(1, pedido.getItens().size());
        assertTrue(pedido.getItens().contains(novoItem));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar item nulo")
    void deveLancarExcecaoAoAdicionarItemNulo() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(new ArrayList<>())
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pedido.adicionarItem(null)
        );

        assertEquals("Item não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve calcular valor total do pedido")
    void deveCalcularValorTotal() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        // Act
        double valorTotal = pedido.calcularValorTotal();

        // Assert
        // 2 * 15.90 + 1 * 5.00 = 36.80
        assertEquals(36.80, valorTotal, 0.01);
    }

    @Test
    @DisplayName("Deve retornar zero quando não há itens")
    void deveRetornarZeroQuandoNaoHaItens() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(new ArrayList<>())
                .build();

        // Act
        double valorTotal = pedido.calcularValorTotal();

        // Assert
        assertEquals(0.0, valorTotal);
    }

    @Test
    @DisplayName("Deve atualizar status do pedido")
    void deveAtualizarStatusDoPedido() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        // Act
        pedido.atualizarStatus(StatusPedido.EM_PREPARACAO);

        // Assert
        assertEquals(StatusPedido.EM_PREPARACAO, pedido.getStatus());
        assertNotNull(pedido.getDataUltimaAtualizacao());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar para status nulo")
    void deveLancarExcecaoAoAtualizarParaStatusNulo() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pedido.atualizarStatus(null)
        );

        assertEquals("Status não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar transição inválida de status")
    void deveLancarExcecaoAoTentarTransicaoInvalida() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> pedido.atualizarStatus(StatusPedido.FINALIZADO)
        );

        assertTrue(exception.getMessage().contains("Não é possível transicionar"));
    }

    @Test
    @DisplayName("Deve retornar true para isPago quando status é RECEBIDO")
    void deveRetornarTrueParaIsPagoQuandoRecebido() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        // Act & Assert
        assertTrue(pedido.isPago());
    }

    @Test
    @DisplayName("Deve retornar true para isPago quando status é EM_PREPARACAO")
    void deveRetornarTrueParaIsPagoQuandoEmPreparacao() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(itens)
                .build();
        pedido.atualizarStatus(StatusPedido.EM_PREPARACAO);

        // Act & Assert
        assertTrue(pedido.isPago());
    }

    @Test
    @DisplayName("Deve retornar false para isPago quando status é AGUARDANDO_PAGAMENTO")
    void deveRetornarFalseParaIsPagoQuandoAguardandoPagamento() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.AGUARDANDO_PAGAMENTO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        // Act & Assert
        assertFalse(pedido.isPago());
    }

    @Test
    @DisplayName("Deve retornar true para isAguardandoPagamento quando status adequado")
    void deveRetornarTrueParaIsAguardandoPagamento() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.AGUARDANDO_PAGAMENTO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        // Act & Assert
        assertTrue(pedido.isAguardandoPagamento());
    }

    @Test
    @DisplayName("Deve retornar false para isAguardandoPagamento quando status diferente")
    void deveRetornarFalseParaIsAguardandoPagamentoQuandoStatusDiferente() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        // Act & Assert
        assertFalse(pedido.isAguardandoPagamento());
    }

    @Test
    @DisplayName("Deve retornar lista imutável de itens")
    void deveRetornarListaImutavelDeItens() {
        // Arrange
        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        // Act
        List<ItemPedido> itensRetornados = pedido.getItens();

        // Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            itensRetornados.add(new ItemPedido("Teste", "PROD-999", 1, 10.0));
        });
    }

    @Test
    @DisplayName("Deve comparar pedidos por ID usando equals")
    void deveCompararPedidosPorIdUsandoEquals() {
        // Arrange
        String id = UUID.randomUUID().toString();
        
        Pedido pedido1 = new Pedido.Builder()
                .id(id)
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        Pedido pedido2 = new Pedido.Builder()
                .id(id)
                .status(StatusPedido.EM_PREPARACAO)
                .numeroPedido(2)
                .itens(new ArrayList<>())
                .build();

        // Act & Assert
        assertEquals(pedido1, pedido2);
        assertEquals(pedido1.hashCode(), pedido2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar false no equals quando IDs diferentes")
    void deveRetornarFalseNoEqualsQuandoIdsDiferentes() {
        // Arrange
        Pedido pedido1 = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        Pedido pedido2 = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .itens(itens)
                .build();

        // Act & Assert
        assertNotEquals(pedido1, pedido2);
    }

    @Test
    @DisplayName("Deve retornar representação em string do pedido")
    void deveRetornarRepresentacaoEmString() {
        // Arrange
        String id = UUID.randomUUID().toString();
        Pedido pedido = new Pedido.Builder()
                .id(id)
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .itens(itens)
                .build();

        // Act
        String resultado = pedido.toString();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.contains(id));
        assertTrue(resultado.contains("numeroPedido=1"));
        assertTrue(resultado.contains("RECEBIDO"));
    }
}
