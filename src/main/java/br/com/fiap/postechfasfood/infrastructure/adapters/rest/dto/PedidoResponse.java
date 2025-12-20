package br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto;

import java.time.LocalDateTime;
import java.util.List;


public record PedidoResponse(
    String id,
    String documentoCliente,
    String status,
    Integer numeroPedido,
    LocalDateTime dataCriacao,
    LocalDateTime dataUltimaAtualizacao,
    List<ItemPedidoResponse> itens,
    Double valorTotal
) {
    public record ItemPedidoResponse(
        String nomeProduto,
        String codigoProduto,
        Integer quantidade,
        Double precoUnitario,
        Double subtotal
    ) {}
}
