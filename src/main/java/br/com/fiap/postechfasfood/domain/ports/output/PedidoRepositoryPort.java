package br.com.fiap.postechfasfood.domain.ports.output;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;

import java.util.List;
import java.util.Optional;

public interface PedidoRepositoryPort {

    Pedido salvar(Pedido pedido);

    Pedido atualizar(Pedido pedido);

    Optional<Pedido> buscarPorId(String id);

    Optional<Pedido> buscarPorNumeroPedido(Integer numeroPedido);

    List<Pedido> listarTodos();

    List<Pedido> listarPorStatus(StatusPedido status);

    Integer gerarProximoNumeroPedido();
}
