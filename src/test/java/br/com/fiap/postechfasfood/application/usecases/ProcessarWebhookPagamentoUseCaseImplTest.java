package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.Pagamento;
import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.ProcessarWebhookPagamentoUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.PagamentoRepositoryPort;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Processar Webhook Pagamento Use Case - Testes")
class ProcessarWebhookPagamentoUseCaseImplTest {
    
    @Mock
    private PedidoRepositoryPort pedidoRepository;
    
    @Mock
    private PagamentoRepositoryPort pagamentoRepository;
    
    private ProcessarWebhookPagamentoUseCaseImpl useCase;
    
    @BeforeEach
    void setUp() {
        useCase = new ProcessarWebhookPagamentoUseCaseImpl(pedidoRepository, pagamentoRepository);
    }
    
    @Test
    @DisplayName("Deve processar webhook de pagamento aprovado e atualizar pedido para RECEBIDO")
    void deveProcessarPagamentoAprovadoComSucesso() {
        // Arrange
        Integer numeroPedido = 1;
        Pedido pedido = criarPedidoMock(numeroPedido, StatusPedido.AGUARDANDO_PAGAMENTO);
        
        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pedido));
        when(pedidoRepository.atualizar(any(Pedido.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(pagamentoRepository.salvar(any(Pagamento.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest request = 
            new ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest(
                numeroPedido,
                StatusPagamento.APROVADO,
                BigDecimal.valueOf(68.00),
                LocalDateTime.now(),
                "MP-123456789",
                "MERCADO_PAGO"
            );
        
        // Act
        useCase.executar(request);
        
        // Assert
        assertEquals(StatusPedido.RECEBIDO, pedido.getStatus());

        ArgumentCaptor<Pagamento> pagamentoCaptor = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentoRepository, times(1)).salvar(pagamentoCaptor.capture());
        
        Pagamento pagamentoSalvo = pagamentoCaptor.getValue();
        assertNotNull(pagamentoSalvo);
        assertEquals(numeroPedido, pagamentoSalvo.getNumeroPedido());
        assertEquals(StatusPagamento.APROVADO, pagamentoSalvo.getStatusPagamento());
        assertEquals(BigDecimal.valueOf(68.00), pagamentoSalvo.getValor());
        assertEquals("MP-123456789", pagamentoSalvo.getTransacaoId());

        verify(pedidoRepository, times(1)).atualizar(pedido);
    }
    
    @Test
    @DisplayName("Deve processar webhook de pagamento recusado e cancelar pedido")
    void deveProcessarPagamentoRecusadoECancelarPedido() {
        // Arrange
        Integer numeroPedido = 2;
        Pedido pedido = criarPedidoMock(numeroPedido, StatusPedido.AGUARDANDO_PAGAMENTO);
        
        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pedido));
        when(pedidoRepository.atualizar(any(Pedido.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(pagamentoRepository.salvar(any(Pagamento.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest request = 
            new ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest(
                numeroPedido,
                StatusPagamento.RECUSADO,
                BigDecimal.valueOf(68.00),
                LocalDateTime.now(),
                "MP-987654321",
                "MERCADO_PAGO"
            );
        
        // Act
        useCase.executar(request);
        
        // Assert
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
        verify(pagamentoRepository, times(1)).salvar(any(Pagamento.class));
        verify(pedidoRepository, times(1)).atualizar(pedido);
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando pedido não for encontrado")
    void deveLancarExcecaoQuandoPedidoNaoEncontrado() {
        // Arrange
        Integer numeroPedido = 999;
        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.empty());
        
        ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest request = 
            new ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest(
                numeroPedido,
                StatusPagamento.APROVADO,
                BigDecimal.valueOf(68.00),
                LocalDateTime.now(),
                "MP-123456789",
                "MERCADO_PAGO"
            );
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.executar(request)
        );
        
        assertEquals("Pedido não encontrado: " + numeroPedido, exception.getMessage());
        verify(pagamentoRepository, never()).salvar(any(Pagamento.class));
        verify(pedidoRepository, never()).atualizar(any(Pedido.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando request for nulo")
    void deveLancarExcecaoQuandoRequestNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.executar(null)
        );
        
        assertEquals("Dados do webhook inválidos", exception.getMessage());
    }
    
    @Test
    @DisplayName("Não deve atualizar pedido que já está RECEBIDO quando pagamento for aprovado")
    void naoDeveAtualizarPedidoJaRecebido() {
        // Arrange
        Integer numeroPedido = 3;
        Pedido pedido = criarPedidoMock(numeroPedido, StatusPedido.RECEBIDO);
        
        when(pedidoRepository.buscarPorNumeroPedido(numeroPedido))
            .thenReturn(Optional.of(pedido));
        when(pagamentoRepository.salvar(any(Pagamento.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest request = 
            new ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest(
                numeroPedido,
                StatusPagamento.APROVADO,
                BigDecimal.valueOf(68.00),
                LocalDateTime.now(),
                "MP-123456789",
                "MERCADO_PAGO"
            );
        
        // Act
        useCase.executar(request);

        verify(pagamentoRepository, times(1)).salvar(any(Pagamento.class));
        verify(pedidoRepository, never()).atualizar(any(Pedido.class));
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
}
