package br.com.fiap.postechfasfood.infrastructure.adapters.external;

import br.com.fiap.postechfasfood.domain.ports.output.PagamentoServicePort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class PagamentoServiceAdapter implements PagamentoServicePort {
    
    private static final Logger logger = LoggerFactory.getLogger(PagamentoServiceAdapter.class);
    
    // Simulação de estado de pagamentos em memória
    private final Map<Integer, StatusPagamento> pagamentosSimulados = new HashMap<>();
    
    @Override
    public StatusPagamento consultarStatusPagamento(Integer numeroPedido) {
        logger.info("Consultando status de pagamento externo para pedido: {}", numeroPedido);

        StatusPagamento status = pagamentosSimulados.getOrDefault(
            numeroPedido, 
            StatusPagamento.PENDENTE
        );
        
        logger.info("Status do pagamento para pedido {}: {}", numeroPedido, status);
        return status;
    }
    
    @Override
    public String iniciarPagamento(Integer numeroPedido, double valor) {
        logger.info("Iniciando pagamento externo para pedido {} - Valor: R$ {}", 
            numeroPedido, valor);

        String transacaoId = UUID.randomUUID().toString();

        pagamentosSimulados.put(numeroPedido, StatusPagamento.PENDENTE);
        
        logger.info("Pagamento iniciado - Transação ID: {}", transacaoId);
        return transacaoId;
    }
    
    @Override
    public boolean isPagamentoAprovado(Integer numeroPedido) {
        StatusPagamento status = consultarStatusPagamento(numeroPedido);
        return status.isAprovado();
    }

    public void simularWebhookPagamento(Integer numeroPedido, StatusPagamento novoStatus) {
        logger.info("Simulando webhook de pagamento - Pedido: {} - Status: {}", 
            numeroPedido, novoStatus);
        pagamentosSimulados.put(numeroPedido, novoStatus);
    }
}
