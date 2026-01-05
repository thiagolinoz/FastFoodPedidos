package br.com.fiap.postechfasfood.infrastructure.adapters.external;

import br.com.fiap.postechfasfood.domain.entities.Produto;
import br.com.fiap.postechfasfood.domain.valueobjects.CategoriaProduto;
import br.com.fiap.postechfasfood.infrastructure.external.service.ProdutoExternoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoExternoAdapterTest {

    @Mock
    private ProdutoExternoService produtoExternoService;

    @InjectMocks
    private CatalogoExternoAdapter catalogoExternoAdapter;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto(
                "produto-1",
                "Produto Teste",
                "Descrição do produto teste",
                10.0,
                true,
                CategoriaProduto.LANCHE
        );
    }

    @Test
    @DisplayName("Deve buscar produto por código com sucesso")
    void deveBuscarProdutoPorCodigoComSucesso() {
        // Arrange
        String codigoProduto = "produto-1";
        when(produtoExternoService.buscarProdutoPorId(codigoProduto)).thenReturn(produto);

        // Act
        Optional<Produto> resultado = catalogoExternoAdapter.buscarProdutoPorCodigo(codigoProduto);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(produto, resultado.get());
        verify(produtoExternoService, times(1)).buscarProdutoPorId(codigoProduto);
    }

    @Test
    @DisplayName("Deve retornar empty quando produto não encontrado")
    void deveRetornarEmptyQuandoProdutoNaoEncontrado() {
        // Arrange
        String codigoProduto = "produto-inexistente";
        when(produtoExternoService.buscarProdutoPorId(codigoProduto))
                .thenThrow(new RuntimeException("Produto não encontrado"));

        // Act
        Optional<Produto> resultado = catalogoExternoAdapter.buscarProdutoPorCodigo(codigoProduto);

        // Assert
        assertFalse(resultado.isPresent());
        verify(produtoExternoService, times(1)).buscarProdutoPorId(codigoProduto);
    }

    @Test
    @DisplayName("Deve lançar exceção para busca por nome")
    void deveLancarExcecaoParaBuscaPorNome() {
        // Arrange
        String nomeProduto = "Nome do Produto";

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> catalogoExternoAdapter.buscarProdutoPorNome(nomeProduto)
        );

        assertEquals("Busca por nome não suportada na API externa", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para verificação de produto ativo por nome")
    void deveLancarExcecaoParaVerificacaoProdutoAtivoPorNome() {
        // Arrange
        String nomeProduto = "Nome do Produto";

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> catalogoExternoAdapter.produtoExisteEEstaAtivo(nomeProduto)
        );

        assertEquals("Busca por nome não suportada na API externa", exception.getMessage());
    }
}
