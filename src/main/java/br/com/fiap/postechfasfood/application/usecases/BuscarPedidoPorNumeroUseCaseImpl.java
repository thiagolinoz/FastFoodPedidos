package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.BuscarPedidoPorNumeroUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;

public class BuscarPedidoPorNumeroUseCaseImpl implements BuscarPedidoPorNumeroUseCase {

    private final PedidoRepositoryPort pedidoRepository;

    public BuscarPedidoPorNumeroUseCaseImpl(PedidoRepositoryPort pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Pedido executar(Integer numeroPedido) {
        if (numeroPedido == null) {
            throw new IllegalArgumentException("Número do pedido é obrigatório");
        }

        return pedidoRepository.buscarPorNumeroPedido(numeroPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com número: " + numeroPedido));
    }
}
