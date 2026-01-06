package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.BuscarPedidoPorNumeroUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Buscar Pedido Por Numero Use Case - Testes")
class BuscarPedidoPorNumeroUseCaseImplTest {

    @Mock
    private PedidoRepositoryPort pedidoRepository;

    private BuscarPedidoPorNumeroUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BuscarPedidoPorNumeroUseCaseImpl(pedidoRepository);
    }

    @Test
    @DisplayName("Deve buscar pedido por número com sucesso")
    void deveBuscarPedidoPorNumeroComSucesso() {
        // Arrange
        Integer numeroPedido = 123;
        Pedido pedidoMock = criarPedidoMock(numeroPedido);

        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pedidoMock));

        // Act
        Pedido pedidoEncontrado = useCase.executar(numeroPedido);

        // Assert
        assertNotNull(pedidoEncontrado);
        assertEquals(numeroPedido, pedidoEncontrado.getNumeroPedido());
        assertEquals(pedidoMock.getId(), pedidoEncontrado.getId());
        assertEquals(pedidoMock.getDocumentoCliente(), pedidoEncontrado.getDocumentoCliente());

        verify(pedidoRepository, times(1)).buscarPorNumeroPedido(numeroPedido);
    }

    @Test
    @DisplayName("Deve lançar exceção quando número do pedido é null")
    void deveLancarExcecaoQuandoNumeroPedidoNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.executar(null)
        );

        assertEquals("Número do pedido é obrigatório", exception.getMessage());
        verify(pedidoRepository, never()).buscarPorNumeroPedido(anyInt());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pedido não é encontrado")
    void deveLancarExcecaoQuandoPedidoNaoEncontrado() {
        // Arrange
        Integer numeroPedido = 999;

        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.executar(numeroPedido)
        );

        assertEquals("Pedido não encontrado com número: " + numeroPedido, exception.getMessage());
        verify(pedidoRepository, times(1)).buscarPorNumeroPedido(numeroPedido);
    }

    @Test
    @DisplayName("Deve buscar pedido com número zero")
    void deveBuscarPedidoComNumeroZero() {
        // Arrange
        Integer numeroPedido = 0;
        Pedido pedidoMock = criarPedidoMock(numeroPedido);

        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pedidoMock));

        // Act
        Pedido pedidoEncontrado = useCase.executar(numeroPedido);

        // Assert
        assertNotNull(pedidoEncontrado);
        assertEquals(numeroPedido, pedidoEncontrado.getNumeroPedido());
        verify(pedidoRepository, times(1)).buscarPorNumeroPedido(numeroPedido);
    }

    @Test
    @DisplayName("Deve buscar pedido com número negativo")
    void deveBuscarPedidoComNumeroNegativo() {
        // Arrange
        Integer numeroPedido = -1;
        Pedido pedidoMock = criarPedidoMock(numeroPedido);

        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pedidoMock));

        // Act
        Pedido pedidoEncontrado = useCase.executar(numeroPedido);

        // Assert
        assertNotNull(pedidoEncontrado);
        assertEquals(numeroPedido, pedidoEncontrado.getNumeroPedido());
        verify(pedidoRepository, times(1)).buscarPorNumeroPedido(numeroPedido);
    }

    @Test
    @DisplayName("Deve buscar pedido com número muito grande")
    void deveBuscarPedidoComNumeroMuitoGrande() {
        // Arrange
        Integer numeroPedido = Integer.MAX_VALUE;
        Pedido pedidoMock = criarPedidoMock(numeroPedido);

        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pedidoMock));

        // Act
        Pedido pedidoEncontrado = useCase.executar(numeroPedido);

        // Assert
        assertNotNull(pedidoEncontrado);
        assertEquals(numeroPedido, pedidoEncontrado.getNumeroPedido());
        verify(pedidoRepository, times(1)).buscarPorNumeroPedido(numeroPedido);
    }

    private Pedido criarPedidoMock(Integer numeroPedido) {
        return new Pedido.Builder()
            .id(UUID.randomUUID().toString())
            .documentoCliente("12345678900")
            .status(StatusPedido.AGUARDANDO_PAGAMENTO)
            .numeroPedido(numeroPedido)
            .dataCriacao(LocalDateTime.now())
            .dataUltimaAtualizacao(LocalDateTime.now())
            .build();
    }
}
