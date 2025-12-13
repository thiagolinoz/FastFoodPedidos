package br.com.fiap.postechfasfood.domain.ports.input;

import br.com.fiap.postechfasfood.domain.entities.Pedido;

import java.util.List;

public interface CadastrarPedidoUseCase {

    Pedido executar(CadastrarPedidoRequest request);

    record CadastrarPedidoRequest(
        String documentoCliente,
        List<ItemPedidoRequest> itens
    ) {
        public record ItemPedidoRequest(
            String nomeProduto,
            int quantidade
        ) {}
    }
}
