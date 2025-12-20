package br.com.fiap.postechfasfood.infrastructure.adapters.webhook;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.ProcessarWebhookPagamentoUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.WebhookPagamentoRequest;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.WebhookPagamentoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Webhook Pagamento Controller - Testes")
class WebhookPagamentoControllerTest {
    
    @Mock
    private ProcessarWebhookPagamentoUseCase processarWebhookPagamentoUseCase;
    
    @Mock
    private PedidoRepositoryPort pedidoRepository;
    
    private WebhookPagamentoController controller;
    
    @BeforeEach
    void setUp() {
        controller = new WebhookPagamentoController(processarWebhookPagamentoUseCase, pedidoRepository);
    }
    
    @Test
    @DisplayName("Deve processar webhook com sucesso e retornar status OK")
    void deveProcessarWebhookComSucesso() {
        // Arrange
        Integer nrPedido = 1;
        WebhookPagamentoRequest request = new WebhookPagamentoRequest(
            nrPedido,
            "APROVADO",
            BigDecimal.valueOf(68.00),
            LocalDateTime.now(),
            "MP-123456789",
            "MERCADO_PAGO"
        );
        
        Pedido pedidoAtualizado = criarPedidoMock(nrPedido, StatusPedido.RECEBIDO);
        
        doNothing().when(processarWebhookPagamentoUseCase).executar(any());
        when(pedidoRepository.buscarPorNumeroPedido(nrPedido))
            .thenReturn(Optional.of(pedidoAtualizado));
        
        // Act
        ResponseEntity<WebhookPagamentoResponse> response = 
            controller.receberWebhookMercadoPago(nrPedido, request);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Webhook processado com sucesso", response.getBody().mensagem());
        assertEquals(nrPedido, response.getBody().numeroPedido());
        assertEquals("APROVADO", response.getBody().statusPagamento());
        assertEquals("RECEBIDO", response.getBody().statusPedido());
        
        // Verificar que o use case foi chamado
        ArgumentCaptor<ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest> captor = 
            ArgumentCaptor.forClass(ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest.class);
        verify(processarWebhookPagamentoUseCase, times(1)).executar(captor.capture());
        
        ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest useCaseRequest = captor.getValue();
        assertEquals(nrPedido, useCaseRequest.numeroPedido());
        assertEquals(StatusPagamento.APROVADO, useCaseRequest.statusPagamento());
    }
    
    @Test
    @DisplayName("Deve retornar BAD_REQUEST quando número do pedido inconsistente")
    void deveRetornarBadRequestQuandoNumeroPedidoInconsistente() {
        // Arrange
        Integer nrPedidoPath = 1;
        Integer nrPedidoBody = 2;
        
        WebhookPagamentoRequest request = new WebhookPagamentoRequest(
            nrPedidoBody,
            "APROVADO",
            BigDecimal.valueOf(68.00),
            LocalDateTime.now(),
            "MP-123456789",
            "MERCADO_PAGO"
        );
        
        // Act
        ResponseEntity<WebhookPagamentoResponse> response = 
            controller.receberWebhookMercadoPago(nrPedidoPath, request);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(processarWebhookPagamentoUseCase, never()).executar(any());
        verify(pedidoRepository, never()).buscarPorNumeroPedido(any());
    }
    
    @Test
    @DisplayName("Deve processar webhook de pagamento recusado e retornar pedido cancelado")
    void deveProcessarWebhookPagamentoRecusado() {
        // Arrange
        Integer nrPedido = 2;
        WebhookPagamentoRequest request = new WebhookPagamentoRequest(
            nrPedido,
            "RECUSADO",
            BigDecimal.valueOf(68.00),
            LocalDateTime.now(),
            "MP-987654321",
            "MERCADO_PAGO"
        );
        
        Pedido pedidoCancelado = criarPedidoMock(nrPedido, StatusPedido.CANCELADO);
        
        doNothing().when(processarWebhookPagamentoUseCase).executar(any());
        when(pedidoRepository.buscarPorNumeroPedido(nrPedido))
            .thenReturn(Optional.of(pedidoCancelado));
        
        // Act
        ResponseEntity<WebhookPagamentoResponse> response = 
            controller.receberWebhookMercadoPago(nrPedido, request);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RECUSADO", response.getBody().statusPagamento());
        assertEquals("CANCELADO", response.getBody().statusPedido());
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando pedido não encontrado após processamento")
    void deveLancarExcecaoQuandoPedidoNaoEncontradoAposProcessamento() {
        // Arrange
        Integer nrPedido = 999;
        WebhookPagamentoRequest request = new WebhookPagamentoRequest(
            nrPedido,
            "APROVADO",
            BigDecimal.valueOf(68.00),
            LocalDateTime.now(),
            "MP-123456789",
            "MERCADO_PAGO"
        );
        
        doNothing().when(processarWebhookPagamentoUseCase).executar(any());
        when(pedidoRepository.buscarPorNumeroPedido(nrPedido))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> controller.receberWebhookMercadoPago(nrPedido, request)
        );
        
        assertEquals("Pedido não encontrado após processamento", exception.getMessage());
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
