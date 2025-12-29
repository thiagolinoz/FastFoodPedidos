package br.com.fiap.postechfasfood.domain.ports.input;

import br.com.fiap.postechfasfood.domain.entities.Pedido;

public interface BuscarPedidoPorNumeroUseCase {

    Pedido executar(Integer numeroPedido);
}
