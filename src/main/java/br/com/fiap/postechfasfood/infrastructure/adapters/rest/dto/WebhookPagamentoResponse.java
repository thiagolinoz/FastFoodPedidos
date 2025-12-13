package br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto;

import java.time.LocalDateTime;


public record WebhookPagamentoResponse(
    String mensagem,
    Integer numeroPedido,
    String statusPagamento,
    String statusPedido,
    LocalDateTime dataProcessamento
) {
    public static WebhookPagamentoResponse sucesso(Integer numeroPedido, String statusPagamento, String statusPedido) {
        return new WebhookPagamentoResponse(
            "Webhook processado com sucesso",
            numeroPedido,
            statusPagamento,
            statusPedido,
            LocalDateTime.now()
        );
    }
}
