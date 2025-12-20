package br.com.fiap.postechfasfood.infrastructure.adapters.rest.mapper;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.CadastrarPedidoUseCase;
import br.com.fiap.postechfasfood.domain.ports.input.ProcessarWebhookPagamentoUseCase;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.CheckoutPedidoRequest;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.PedidoResponse;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.StatusPagamentoResponse;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.WebhookPagamentoRequest;

import java.util.List;
import java.util.stream.Collectors;


public class PedidoMapper {
    
    private PedidoMapper() {

    }

    public static CadastrarPedidoUseCase.CadastrarPedidoRequest toUseCaseRequest(
            CheckoutPedidoRequest dto) {
        
        List<CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest> itens = 
            dto.itens().stream()
                .map(item -> new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest(
                    item.idProduto(),
                    item.quantidade()
                ))
                .collect(Collectors.toList());
        
        return new CadastrarPedidoUseCase.CadastrarPedidoRequest(
            dto.documentoCliente(),
            itens
        );
    }

    public static PedidoResponse toResponse(Pedido pedido) {
        List<PedidoResponse.ItemPedidoResponse> itensResponse = 
            pedido.getItens().stream()
                .map(item -> new PedidoResponse.ItemPedidoResponse(
                    item.getNomeProduto(),
                    item.getCodigoProduto(),
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.calcularSubtotal()
                ))
                .collect(Collectors.toList());
        
        return new PedidoResponse(
            pedido.getId(),
            pedido.getDocumentoCliente(),
            pedido.getStatus().name(),
            pedido.getNumeroPedido(),
            pedido.getDataCriacao(),
            pedido.getDataUltimaAtualizacao(),
            itensResponse,
            pedido.calcularValorTotal()
        );
    }

    public static List<PedidoResponse> toResponseList(List<Pedido> pedidos) {
        return pedidos.stream()
            .map(PedidoMapper::toResponse)
            .collect(Collectors.toList());
    }

    public static ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest toWebhookUseCaseRequest(
            WebhookPagamentoRequest dto) {
        
        return new ProcessarWebhookPagamentoUseCase.WebhookPagamentoRequest(
            dto.numeroPedido(),
            StatusPagamento.fromString(dto.statusPagamento()),
            dto.valor(),
            dto.dataHoraPagamento(),
            dto.transacaoId(),
            dto.origem()
        );
    }

    public static StatusPagamentoResponse toStatusPagamentoResponse(
            Integer numeroPedido, StatusPagamento status) {
        
        return new StatusPagamentoResponse(
            numeroPedido,
            status.name(),
            status.getDescricao()
        );
    }
}
