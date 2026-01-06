package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.BuscarPedidosPorStatusUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;

import java.util.List;

public class BuscarPedidosPorStatusUseCaseImpl implements BuscarPedidosPorStatusUseCase {

    private final PedidoRepositoryPort pedidoRepository;

    public BuscarPedidosPorStatusUseCaseImpl(PedidoRepositoryPort pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public List<Pedido> executar(StatusPedido status) {
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório");
        }

        return pedidoRepository.listarPorStatus(status);
    }
}
