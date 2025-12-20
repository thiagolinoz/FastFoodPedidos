package br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WebhookPagamentoRequest(
    @NotNull(message = "Número do pedido é obrigatório")
    Integer numeroPedido,
    
    @NotNull(message = "Status do pagamento é obrigatório")
    String statusPagamento,
    
    @NotNull(message = "Valor é obrigatório")
    BigDecimal valor,
    
    @NotNull(message = "Data/Hora do pagamento é obrigatória")
    LocalDateTime dataHoraPagamento,
    
    String transacaoId,
    String origem
) {}
