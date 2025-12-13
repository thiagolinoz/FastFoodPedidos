package br.com.fiap.postechfasfood.domain.ports.output;

import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;


public interface PagamentoServicePort {

    StatusPagamento consultarStatusPagamento(Integer numeroPedido);

    String iniciarPagamento(Integer numeroPedido, double valor);

    boolean isPagamentoAprovado(Integer numeroPedido);
}
