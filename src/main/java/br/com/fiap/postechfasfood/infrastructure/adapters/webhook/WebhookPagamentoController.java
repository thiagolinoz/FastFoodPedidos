package br.com.fiap.postechfasfood.infrastructure.adapters.webhook;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.ProcessarWebhookPagamentoUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.WebhookPagamentoRequest;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.WebhookPagamentoResponse;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.mapper.PedidoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@Tag(name = "Webhooks", description = "Webhooks de sistemas externos")
public class WebhookPagamentoController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookPagamentoController.class);
    
    private final ProcessarWebhookPagamentoUseCase processarWebhookPagamentoUseCase;
    private final PedidoRepositoryPort pedidoRepository;
    
    public WebhookPagamentoController(
            ProcessarWebhookPagamentoUseCase processarWebhookPagamentoUseCase,
            PedidoRepositoryPort pedidoRepository) {
        this.processarWebhookPagamentoUseCase = processarWebhookPagamentoUseCase;
        this.pedidoRepository = pedidoRepository;
    }

    @PostMapping("/mercado-pago/pagamentos")
    @Operation(summary = "Webhook Mercado Pago",
               description = "Recebe notificações de status de pagamento do Mercado Pago")
    public ResponseEntity<WebhookPagamentoResponse> receberWebhookMercadoPago(
            @Valid @RequestBody WebhookPagamentoRequest request) {
        
        logger.info("Webhook recebido do Mercado Pago - Pedido: {} - Status: {}", 
            request.numeroPedido(), request.statusPagamento());

        ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest useCaseRequest = 
            PedidoMapper.toWebhookUseCaseRequest(request);

        processarWebhookPagamentoUseCase.executar(useCaseRequest);

        Pedido pedidoAtualizado = pedidoRepository.buscarPorNumeroPedido(request.numeroPedido())
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado após processamento"));
        
        logger.info("Webhook processado com sucesso - Pedido: {} - Novo status do pedido: {}",
            request.numeroPedido(), pedidoAtualizado.getStatus());

        WebhookPagamentoResponse response = WebhookPagamentoResponse.sucesso(
            request.numeroPedido(),
            request.statusPagamento(),
            pedidoAtualizado.getStatus().name()
        );
        
        return ResponseEntity.ok(response);
    }
}
