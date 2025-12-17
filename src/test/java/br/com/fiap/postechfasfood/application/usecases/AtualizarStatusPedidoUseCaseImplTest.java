package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.ItemPedido;
import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AtualizarStatusPedidoUseCaseImpl")
class AtualizarStatusPedidoUseCaseImplTest {

    @Mock
    private PedidoRepositoryPort pedidoRepository;

    @InjectMocks
    private AtualizarStatusPedidoUseCaseImpl useCase;

    private Pedido pedidoMock;
    private String pedidoId;

    @BeforeEach
    void setUp() {
        pedidoId = UUID.randomUUID().toString();
        
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));

        pedidoMock = new Pedido.Builder()
                .id(pedidoId)
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(itens)
                .build();
    }

    @Test
    @DisplayName("Deve atualizar status do pedido com sucesso")
    void deveAtualizarStatusComSucesso() {
        // Given: um pedido existente com status RECEBIDO
        StatusPedido novoStatus = StatusPedido.EM_PREPARACAO;
        when(pedidoRepository.buscarPorId(pedidoId)).thenReturn(Optional.of(pedidoMock));
        when(pedidoRepository.atualizar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: o status do pedido é atualizado para EM_PREPARACAO
        Pedido resultado = useCase.executar(pedidoId, novoStatus);

        // Then: o pedido deve ter o novo status e as operações devem ser realizadas corretamente
        assertNotNull(resultado);
        assertEquals(novoStatus, resultado.getStatus());
        verify(pedidoRepository).buscarPorId(pedidoId);
        verify(pedidoRepository).atualizar(pedidoMock);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID do pedido for nulo")
    void deveLancarExcecaoQuandoIdNulo() {
        // Arrange
        StatusPedido novoStatus = StatusPedido.EM_PREPARACAO;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.executar(null, novoStatus)
        );

        assertEquals("ID do pedido é obrigatório", exception.getMessage());
        verify(pedidoRepository, never()).buscarPorId(any());
        verify(pedidoRepository, never()).atualizar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID do pedido estiver em branco")
    void deveLancarExcecaoQuandoIdEmBranco() {
        // Arrange
        StatusPedido novoStatus = StatusPedido.EM_PREPARACAO;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.executar("   ", novoStatus)
        );

        assertEquals("ID do pedido é obrigatório", exception.getMessage());
        verify(pedidoRepository, never()).buscarPorId(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando novo status for nulo")
    void deveLancarExcecaoQuandoStatusNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.executar(pedidoId, null)
        );

        assertEquals("Novo status é obrigatório", exception.getMessage());
        verify(pedidoRepository, never()).buscarPorId(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pedido não for encontrado")
    void deveLancarExcecaoQuandoPedidoNaoEncontrado() {
        // Arrange
        String pedidoInexistente = UUID.randomUUID().toString();
        StatusPedido novoStatus = StatusPedido.EM_PREPARACAO;
        
        when(pedidoRepository.buscarPorId(pedidoInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.executar(pedidoInexistente, novoStatus)
        );

        assertTrue(exception.getMessage().contains("Pedido não encontrado"));
        verify(pedidoRepository).buscarPorId(pedidoInexistente);
        verify(pedidoRepository, never()).atualizar(any());
    }

    @Test
    @DisplayName("Deve atualizar de RECEBIDO para EM_PREPARACAO")
    void deveAtualizarDeRecebidoParaEmPreparacao() {
        // Arrange
        when(pedidoRepository.buscarPorId(pedidoId)).thenReturn(Optional.of(pedidoMock));
        when(pedidoRepository.atualizar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pedido resultado = useCase.executar(pedidoId, StatusPedido.EM_PREPARACAO);

        // Assert
        assertEquals(StatusPedido.EM_PREPARACAO, resultado.getStatus());
        verify(pedidoRepository).atualizar(pedidoMock);
    }

    @Test
    @DisplayName("Deve atualizar de EM_PREPARACAO para PRONTO")
    void deveAtualizarDeEmPreparacaoParaPronto() {
        // Arrange
        pedidoMock.atualizarStatus(StatusPedido.EM_PREPARACAO);
        
        when(pedidoRepository.buscarPorId(pedidoId)).thenReturn(Optional.of(pedidoMock));
        when(pedidoRepository.atualizar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pedido resultado = useCase.executar(pedidoId, StatusPedido.PRONTO);

        // Assert
        assertEquals(StatusPedido.PRONTO, resultado.getStatus());
        verify(pedidoRepository).atualizar(pedidoMock);
    }

    @Test
    @DisplayName("Deve atualizar de PRONTO para FINALIZADO")
    void deveAtualizarDeProntoParaFinalizado() {
        // Arrange
        pedidoMock.atualizarStatus(StatusPedido.EM_PREPARACAO);
        pedidoMock.atualizarStatus(StatusPedido.PRONTO);
        
        when(pedidoRepository.buscarPorId(pedidoId)).thenReturn(Optional.of(pedidoMock));
        when(pedidoRepository.atualizar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Pedido resultado = useCase.executar(pedidoId, StatusPedido.FINALIZADO);

        // Assert
        assertEquals(StatusPedido.FINALIZADO, resultado.getStatus());
        verify(pedidoRepository).atualizar(pedidoMock);
    }
}
