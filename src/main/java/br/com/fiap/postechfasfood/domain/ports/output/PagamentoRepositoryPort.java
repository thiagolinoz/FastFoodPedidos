package br.com.fiap.postechfasfood.domain.ports.output;

import br.com.fiap.postechfasfood.domain.entities.Pagamento;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepositoryPort {
    
    Pagamento salvar(Pagamento pagamento);
    
    Optional<Pagamento> buscarPorId(String id);
    
    Optional<Pagamento> buscarPorNumeroPedido(Integer numeroPedido);
    
    List<Pagamento> buscarPorPedidoId(String pedidoId);
    
    List<Pagamento> listarTodos();
}
