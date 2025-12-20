package br.com.fiap.postechfasfood.infrastructure.adapters.external;

import br.com.fiap.postechfasfood.domain.entities.Produto;
import br.com.fiap.postechfasfood.domain.ports.output.CatalogoServicePort;
import br.com.fiap.postechfasfood.infrastructure.external.service.ProdutoExternoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
public class CatalogoExternoAdapter implements CatalogoServicePort {

    private final ProdutoExternoService produtoExternoService;

    @Override
    public Optional<Produto> buscarProdutoPorNome(String nomeProduto) {
        throw new UnsupportedOperationException("Busca por nome não suportada na API externa");
    }

    @Override
    public Optional<Produto> buscarProdutoPorCodigo(String codigoProduto) {
        try {
            Produto produto = produtoExternoService.buscarProdutoPorId(codigoProduto);
            return Optional.of(produto);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean produtoExisteEEstaAtivo(String nomeProduto) {
        throw new UnsupportedOperationException("Busca por nome não suportada na API externa");
    }
}
