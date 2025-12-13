package br.com.fiap.postechfasfood.domain.ports.input;

import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public interface ProcessarWebhookPagamentoUseCase {

    void executar(WebhookPagamentoRequest request);

    record WebhookPagamentoRequest(
        Integer numeroPedido,
        StatusPagamento statusPagamento,
        BigDecimal valor,
        LocalDateTime dataHoraPagamento,
        String transacaoId,
        String origem  // ex: "mercado-pago"
    ) {}
}
