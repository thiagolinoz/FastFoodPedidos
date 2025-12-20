package br.com.fiap.postechfasfood.domain.ports.input;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;

public interface AtualizarStatusPedidoUseCase {

    Pedido executar(String pedidoId, StatusPedido novoStatus);
}
