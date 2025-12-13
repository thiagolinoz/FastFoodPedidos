package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.AtualizarStatusPedidoUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;

public class AtualizarStatusPedidoUseCaseImpl implements AtualizarStatusPedidoUseCase {
    
    private final PedidoRepositoryPort pedidoRepository;
    
    public AtualizarStatusPedidoUseCaseImpl(PedidoRepositoryPort pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }
    
    @Override
    public Pedido executar(String pedidoId, StatusPedido novoStatus) {
        if (pedidoId == null || pedidoId.isBlank()) {
            throw new IllegalArgumentException("ID do pedido é obrigatório");
        }
        if (novoStatus == null) {
            throw new IllegalArgumentException("Novo status é obrigatório");
        }

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

        pedido.atualizarStatus(novoStatus);

        return pedidoRepository.atualizar(pedido);
    }
}
