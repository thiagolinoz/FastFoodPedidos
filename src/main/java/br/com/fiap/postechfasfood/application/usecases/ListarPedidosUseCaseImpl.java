package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.ListarPedidosUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListarPedidosUseCaseImpl implements ListarPedidosUseCase {
    
    private final PedidoRepositoryPort pedidoRepository;
    
    public ListarPedidosUseCaseImpl(PedidoRepositoryPort pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }
    
    @Override
    public List<Pedido> executar() {
        List<Pedido> todosPedidos = pedidoRepository.listarTodos();

        List<Pedido> pedidosAtivos = todosPedidos.stream()
            .filter(p -> p.getStatus() != StatusPedido.FINALIZADO)
            .collect(Collectors.toList());

        List<Pedido> prontos = filtrarEOrdenarPorStatus(pedidosAtivos, StatusPedido.PRONTO);
        List<Pedido> emPreparacao = filtrarEOrdenarPorStatus(pedidosAtivos, StatusPedido.EM_PREPARACAO);
        List<Pedido> recebidos = filtrarEOrdenarPorStatus(pedidosAtivos, StatusPedido.RECEBIDO);

        List<Pedido> pedidosOrdenados = new ArrayList<>();
        pedidosOrdenados.addAll(prontos);
        pedidosOrdenados.addAll(emPreparacao);
        pedidosOrdenados.addAll(recebidos);

        return pedidosOrdenados;
    }

    private List<Pedido> filtrarEOrdenarPorStatus(List<Pedido> pedidos, StatusPedido status) {
        return pedidos.stream()
            .filter(p -> p.getStatus() == status)
            .sorted(Comparator.comparing(Pedido::getDataCriacao))
            .collect(Collectors.toList());
    }
}
