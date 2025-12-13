package br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;


public record CheckoutPedidoRequest(
    String documentoCliente,
    
    @NotNull(message = "Lista de itens é obrigatória")
    @NotEmpty(message = "Pedido deve conter pelo menos um item")
    @Valid
    List<ItemPedidoRequest> itens
) {
    public record ItemPedidoRequest(
        @NotNull(message = "Nome do produto é obrigatório")
        String nomeProduto,
        
        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantidade
    ) {}
}
