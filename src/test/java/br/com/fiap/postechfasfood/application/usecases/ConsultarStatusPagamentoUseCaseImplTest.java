package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.Pagamento;
import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.output.PagamentoRepositoryPort;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Consultar Status Pagamento Use Case - Testes")
class ConsultarStatusPagamentoUseCaseImplTest {
    
    @Mock
    private PagamentoRepositoryPort pagamentoRepository;
    
    @Mock
    private PedidoRepositoryPort pedidoRepository;
    
    private ConsultarStatusPagamentoUseCaseImpl useCase;
    
    @BeforeEach
    void setUp() {
        useCase = new ConsultarStatusPagamentoUseCaseImpl(pagamentoRepository, pedidoRepository);
    }
    
    @Test
    @DisplayName("Deve retornar status APROVADO quando pagamento existe e está aprovado")
    void deveRetornarStatusAprovadoQuandoExiste() {
        // Arrange
        Integer numeroPedido = 1;
        Pedido pedido = criarPedidoMock(numeroPedido, StatusPedido.RECEBIDO);
        Pagamento pagamento = criarPagamentoMock(numeroPedido, StatusPagamento.APROVADO);
        
        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pedido));
        when(pagamentoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pagamento));
        
        // Act
        StatusPagamento status = useCase.executar(numeroPedido);
        
        // Assert
        assertNotNull(status);
        assertEquals(StatusPagamento.APROVADO, status);
        verify(pedidoRepository, times(1)).buscarPorNumeroPedido(numeroPedido);
        verify(pagamentoRepository, times(1)).buscarPorNumeroPedido(numeroPedido);
    }
    
    @Test
    @DisplayName("Deve retornar status RECUSADO quando pagamento existe e está recusado")
    void deveRetornarStatusRecusadoQuandoExiste() {
        // Arrange
        Integer numeroPedido = 2;
        Pedido pedido = criarPedidoMock(numeroPedido, StatusPedido.CANCELADO);
        Pagamento pagamento = criarPagamentoMock(numeroPedido, StatusPagamento.RECUSADO);
        
        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pedido));
        when(pagamentoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pagamento));
        
        // Act
        StatusPagamento status = useCase.executar(numeroPedido);
        
        // Assert
        assertNotNull(status);
        assertEquals(StatusPagamento.RECUSADO, status);
    }
    
    @Test
    @DisplayName("Deve retornar status PENDENTE quando não há pagamento registrado")
    void deveRetornarPendenteQuandoNaoHaPagamento() {
        // Arrange
        Integer numeroPedido = 3;
        Pedido pedido = criarPedidoMock(numeroPedido, StatusPedido.AGUARDANDO_PAGAMENTO);
        
        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pedido));
        when(pagamentoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.empty());
        
        // Act
        StatusPagamento status = useCase.executar(numeroPedido);
        
        // Assert
        assertNotNull(status);
        assertEquals(StatusPagamento.PENDENTE, status);
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando pedido não for encontrado")
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
        
        assertEquals("Pedido não encontrado: " + numeroPedido, exception.getMessage());
        verify(pagamentoRepository, never()).buscarPorNumeroPedido(any());
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando número do pedido for nulo")
    void deveLancarExcecaoQuandoNumeroPedidoNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.executar(null)
        );
        
        assertEquals("Número do pedido é obrigatório", exception.getMessage());
        verify(pedidoRepository, never()).buscarPorNumeroPedido(any());
        verify(pagamentoRepository, never()).buscarPorNumeroPedido(any());
    }
    
    private Pedido criarPedidoMock(Integer numeroPedido, StatusPedido status) {
        return new Pedido(
            "pedido-" + numeroPedido,
            "12345678900",
            status,
            numeroPedido,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );
    }
    
    private Pagamento criarPagamentoMock(Integer numeroPedido, StatusPagamento status) {
        return new Pagamento(
            "pag-" + numeroPedido,
            "pedido-" + numeroPedido,
            numeroPedido,
            status,
            BigDecimal.valueOf(68.00),
            LocalDateTime.now(),
            "MP-123456789",
            "MERCADO_PAGO",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
