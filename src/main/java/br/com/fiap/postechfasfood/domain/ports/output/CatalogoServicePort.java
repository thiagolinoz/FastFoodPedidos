package br.com.fiap.postechfasfood.domain.ports.output;

import br.com.fiap.postechfasfood.domain.entities.Produto;

import java.util.Optional;

public interface CatalogoServicePort {

    Optional<Produto> buscarProdutoPorNome(String nomeProduto);

    Optional<Produto> buscarProdutoPorCodigo(String codigoProduto);

    boolean produtoExisteEEstaAtivo(String nomeProduto);
}
