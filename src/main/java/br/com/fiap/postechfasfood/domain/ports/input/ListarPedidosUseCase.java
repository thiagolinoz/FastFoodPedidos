package br.com.fiap.postechfasfood.domain.ports.input;

import br.com.fiap.postechfasfood.domain.entities.Pedido;

import java.util.List;

public interface ListarPedidosUseCase {

    List<Pedido> executar();
}
