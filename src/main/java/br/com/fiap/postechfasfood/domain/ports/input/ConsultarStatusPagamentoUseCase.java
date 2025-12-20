package br.com.fiap.postechfasfood.domain.ports.input;

import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;


public interface ConsultarStatusPagamentoUseCase {

    StatusPagamento executar(Integer numeroPedido);
}
