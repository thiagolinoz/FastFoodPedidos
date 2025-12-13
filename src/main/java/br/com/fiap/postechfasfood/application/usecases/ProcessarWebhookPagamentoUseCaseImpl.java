package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.Pagamento;
import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.ProcessarWebhookPagamentoUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.PagamentoRepositoryPort;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessarWebhookPagamentoUseCaseImpl implements ProcessarWebhookPagamentoUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ProcessarWebhookPagamentoUseCaseImpl.class);
    
    private final PedidoRepositoryPort pedidoRepository;
    private final PagamentoRepositoryPort pagamentoRepository;
    
    public ProcessarWebhookPagamentoUseCaseImpl(PedidoRepositoryPort pedidoRepository,
                                                PagamentoRepositoryPort pagamentoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pagamentoRepository = pagamentoRepository;
    }
    
    @Override
    public void executar(WebhookPagamentoRequest request) {
        if (request == null || request.numeroPedido() == null) {
            throw new IllegalArgumentException("Dados do webhook inválidos");
        }
        
        logger.info("Processando webhook de pagamento para pedido: {} - Status: {} - Origem: {}",
            request.numeroPedido(), request.statusPagamento(), request.origem());

        Pedido pedido = pedidoRepository.buscarPorNumeroPedido(request.numeroPedido())
            .orElseThrow(() -> new IllegalArgumentException(
                "Pedido não encontrado: " + request.numeroPedido()));

        Pagamento pagamento = new Pagamento();
        pagamento.setPedidoId(pedido.getId());
        pagamento.setNumeroPedido(request.numeroPedido());
        pagamento.setStatusPagamento(request.statusPagamento());
        pagamento.setValor(request.valor());
        pagamento.setDataHoraPagamento(request.dataHoraPagamento());
        pagamento.setTransacaoId(request.transacaoId());
        pagamento.setOrigem(request.origem() != null ? request.origem() : "MERCADO_PAGO");
        
        pagamentoRepository.salvar(pagamento);
        logger.info("Dados de pagamento persistidos para pedido: {}", request.numeroPedido());

        if (request.statusPagamento().isAprovado()) {
            if (pedido.isAguardandoPagamento()) {
                pedido.atualizarStatus(StatusPedido.RECEBIDO);
                pedidoRepository.atualizar(pedido);
                logger.info("Pedido {} movido para RECEBIDO após aprovação do pagamento", 
                    request.numeroPedido());
            }
        } else if (request.statusPagamento().isRecusado()) {
            if (pedido.getStatus() != StatusPedido.CANCELADO) {
                pedido.atualizarStatus(StatusPedido.CANCELADO);
                pedidoRepository.atualizar(pedido);
                logger.info("Pedido {} cancelado devido a pagamento recusado", 
                    request.numeroPedido());
            }
        }
    }
}
