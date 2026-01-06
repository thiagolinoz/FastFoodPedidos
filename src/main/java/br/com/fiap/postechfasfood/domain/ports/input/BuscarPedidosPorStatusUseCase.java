package br.com.fiap.postechfasfood.domain.ports.input;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;

import java.util.List;

public interface BuscarPedidosPorStatusUseCase {

    List<Pedido> executar(StatusPedido status);
}
