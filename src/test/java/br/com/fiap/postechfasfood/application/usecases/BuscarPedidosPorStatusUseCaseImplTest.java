package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.BuscarPedidosPorStatusUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Buscar Pedidos Por Status Use Case - Testes")
class BuscarPedidosPorStatusUseCaseImplTest {

    @Mock
    private PedidoRepositoryPort pedidoRepository;

    private BuscarPedidosPorStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BuscarPedidosPorStatusUseCaseImpl(pedidoRepository);
    }

    @Test
    @DisplayName("Deve buscar pedidos por status com sucesso")
    void deveBuscarPedidosPorStatusComSucesso() {
        // Arrange
        StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;
        List<Pedido> pedidosMock = Arrays.asList(
            criarPedidoMock(1, status),
            criarPedidoMock(2, status),
            criarPedidoMock(3, status)
        );

        when(pedidoRepository.listarPorStatus(status))
            .thenReturn(pedidosMock);

        // Act
        List<Pedido> pedidosEncontrados = useCase.executar(status);

        // Assert
        assertNotNull(pedidosEncontrados);
        assertEquals(3, pedidosEncontrados.size());

        for (int i = 0; i < pedidosEncontrados.size(); i++) {
            Pedido pedido = pedidosEncontrados.get(i);
            assertEquals(status, pedido.getStatus());
            assertEquals(i + 1, pedido.getNumeroPedido());
        }

        verify(pedidoRepository, times(1)).listarPorStatus(status);
    }

    @Test
    @DisplayName("Deve lançar exceção quando status é null")
    void deveLancarExcecaoQuandoStatusNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.executar(null)
        );

        assertEquals("Status é obrigatório", exception.getMessage());
        verify(pedidoRepository, never()).listarPorStatus(any(StatusPedido.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há pedidos com o status")
    void deveRetornarListaVaziaQuandoNaoHaPedidos() {
        // Arrange
        StatusPedido status = StatusPedido.CANCELADO;

        when(pedidoRepository.listarPorStatus(status))
            .thenReturn(Collections.emptyList());

        // Act
        List<Pedido> pedidosEncontrados = useCase.executar(status);

        // Assert
        assertNotNull(pedidosEncontrados);
        assertTrue(pedidosEncontrados.isEmpty());
        assertEquals(0, pedidosEncontrados.size());

        verify(pedidoRepository, times(1)).listarPorStatus(status);
    }

    @Test
    @DisplayName("Deve buscar pedidos com status AGUARDANDO_PAGAMENTO")
    void deveBuscarPedidosAguardandoPagamento() {
        // Arrange
        StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;
        List<Pedido> pedidosMock = Arrays.asList(
            criarPedidoMock(10, status),
            criarPedidoMock(20, status)
        );

        when(pedidoRepository.listarPorStatus(status))
            .thenReturn(pedidosMock);

        // Act
        List<Pedido> pedidosEncontrados = useCase.executar(status);

        // Assert
        assertNotNull(pedidosEncontrados);
        assertEquals(2, pedidosEncontrados.size());
        pedidosEncontrados.forEach(pedido ->
            assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedido.getStatus())
        );

        verify(pedidoRepository, times(1)).listarPorStatus(status);
    }

    @Test
    @DisplayName("Deve buscar pedidos com status RECEBIDO")
    void deveBuscarPedidosConfirmados() {
        // Arrange
        StatusPedido status = StatusPedido.RECEBIDO;
        List<Pedido> pedidosMock = Arrays.asList(
            criarPedidoMock(100, status)
        );

        when(pedidoRepository.listarPorStatus(status))
            .thenReturn(pedidosMock);

        // Act
        List<Pedido> pedidosEncontrados = useCase.executar(status);

        // Assert
        assertNotNull(pedidosEncontrados);
        assertEquals(1, pedidosEncontrados.size());
        assertEquals(StatusPedido.RECEBIDO, pedidosEncontrados.get(0).getStatus());
        assertEquals(100, pedidosEncontrados.get(0).getNumeroPedido());

        verify(pedidoRepository, times(1)).listarPorStatus(status);
    }

    @Test
    @DisplayName("Deve buscar pedidos com status EM_PREPARACAO")
    void deveBuscarPedidosEmPreparacao() {
        // Arrange
        StatusPedido status = StatusPedido.EM_PREPARACAO;
        List<Pedido> pedidosMock = Arrays.asList(
            criarPedidoMock(50, status),
            criarPedidoMock(51, status),
            criarPedidoMock(52, status),
            criarPedidoMock(53, status)
        );

        when(pedidoRepository.listarPorStatus(status))
            .thenReturn(pedidosMock);

        // Act
        List<Pedido> pedidosEncontrados = useCase.executar(status);

        // Assert
        assertNotNull(pedidosEncontrados);
        assertEquals(4, pedidosEncontrados.size());
        pedidosEncontrados.forEach(pedido ->
            assertEquals(StatusPedido.EM_PREPARACAO, pedido.getStatus())
        );

        verify(pedidoRepository, times(1)).listarPorStatus(status);
    }

    @Test
    @DisplayName("Deve buscar pedidos com status PRONTO")
    void deveBuscarPedidosProntos() {
        // Arrange
        StatusPedido status = StatusPedido.PRONTO;
        List<Pedido> pedidosMock = Arrays.asList(
            criarPedidoMock(200, status),
            criarPedidoMock(201, status)
        );

        when(pedidoRepository.listarPorStatus(status))
            .thenReturn(pedidosMock);

        // Act
        List<Pedido> pedidosEncontrados = useCase.executar(status);

        // Assert
        assertNotNull(pedidosEncontrados);
        assertEquals(2, pedidosEncontrados.size());
        pedidosEncontrados.forEach(pedido ->
            assertEquals(StatusPedido.PRONTO, pedido.getStatus())
        );

        verify(pedidoRepository, times(1)).listarPorStatus(status);
    }

    @Test
    @DisplayName("Deve buscar pedidos com status PRONTO")
    void deveBuscarPedidosEntregues() {
        // Arrange
        StatusPedido status = StatusPedido.PRONTO;
        List<Pedido> pedidosMock = Arrays.asList(
            criarPedidoMock(300, status)
        );

        when(pedidoRepository.listarPorStatus(status))
            .thenReturn(pedidosMock);

        // Act
        List<Pedido> pedidosEncontrados = useCase.executar(status);

        // Assert
        assertNotNull(pedidosEncontrados);
        assertEquals(1, pedidosEncontrados.size());
        assertEquals(StatusPedido.PRONTO, pedidosEncontrados.get(0).getStatus());

        verify(pedidoRepository, times(1)).listarPorStatus(status);
    }

    @Test
    @DisplayName("Deve buscar pedidos com status CANCELADO")
    void deveBuscarPedidosCancelados() {
        // Arrange
        StatusPedido status = StatusPedido.CANCELADO;
        List<Pedido> pedidosMock = Arrays.asList(
            criarPedidoMock(400, status),
            criarPedidoMock(401, status),
            criarPedidoMock(402, status)
        );

        when(pedidoRepository.listarPorStatus(status))
            .thenReturn(pedidosMock);

        // Act
        List<Pedido> pedidosEncontrados = useCase.executar(status);

        // Assert
        assertNotNull(pedidosEncontrados);
        assertEquals(3, pedidosEncontrados.size());
        pedidosEncontrados.forEach(pedido ->
            assertEquals(StatusPedido.CANCELADO, pedido.getStatus())
        );

        verify(pedidoRepository, times(1)).listarPorStatus(status);
    }

    private Pedido criarPedidoMock(Integer numeroPedido, StatusPedido status) {
        return new Pedido.Builder()
            .id(UUID.randomUUID().toString())
            .documentoCliente("12345678900")
            .status(status)
            .numeroPedido(numeroPedido)
            .dataCriacao(LocalDateTime.now())
            .dataUltimaAtualizacao(LocalDateTime.now())
            .build();
    }
}
