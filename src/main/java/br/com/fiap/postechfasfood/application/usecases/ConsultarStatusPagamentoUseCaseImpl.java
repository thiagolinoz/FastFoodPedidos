package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.Pagamento;
import br.com.fiap.postechfasfood.domain.ports.input.ConsultarStatusPagamentoUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.PagamentoRepositoryPort;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsultarStatusPagamentoUseCaseImpl implements ConsultarStatusPagamentoUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsultarStatusPagamentoUseCaseImpl.class);
    
    private final PagamentoRepositoryPort pagamentoRepository;
    private final PedidoRepositoryPort pedidoRepository;
    
    public ConsultarStatusPagamentoUseCaseImpl(
            PagamentoRepositoryPort pagamentoRepository,
            PedidoRepositoryPort pedidoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }
    
    @Override
    public StatusPagamento executar(Integer numeroPedido) {
        if (numeroPedido == null) {
            throw new IllegalArgumentException("Número do pedido é obrigatório");
        }
        
        logger.info("Consultando status de pagamento para pedido: {}", numeroPedido);

        pedidoRepository.buscarPorNumeroPedido(numeroPedido)
            .orElseThrow(() -> new IllegalArgumentException(
                "Pedido não encontrado: " + numeroPedido));

        return pagamentoRepository.buscarPorNumeroPedido(numeroPedido)
            .map(pagamento -> {
                logger.info("Pagamento encontrado para pedido {}: Status={}, Valor={}, Data={}", 
                    numeroPedido, 
                    pagamento.getStatusPagamento(), 
                    pagamento.getValor(), 
                    pagamento.getDataHoraPagamento());
                return pagamento.getStatusPagamento();
            })
            .orElseGet(() -> {
                logger.info("Nenhum pagamento encontrado para pedido {}. Retornando PENDENTE", numeroPedido);
                return StatusPagamento.PENDENTE;
            });
    }
}
