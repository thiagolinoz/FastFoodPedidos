package br.com.fiap.postechfasfood.infrastructure.external.service;

import br.com.fiap.postechfasfood.domain.entities.Produto;
import br.com.fiap.postechfasfood.domain.valueobjects.CategoriaProduto;
import br.com.fiap.postechfasfood.infrastructure.external.client.ProdutoFeignClient;
import br.com.fiap.postechfasfood.infrastructure.external.dto.ProdutoExternoDTO;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ProdutoExternoService")
class ProdutoExternoServiceTest {

    @Mock
    private ProdutoFeignClient produtoFeignClient;

    private ProdutoExternoService produtoExternoService;

    @BeforeEach
    void setUp() {
        produtoExternoService = new ProdutoExternoService(produtoFeignClient);
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorIdComSucesso() {
        // Arrange
        String cdProduto = "lanche-001";
        ProdutoExternoDTO produtoDTO = new ProdutoExternoDTO();
        produtoDTO.setCdProduto(cdProduto);
        produtoDTO.setNmProduto("X-Burger");
        produtoDTO.setDsDescricao("Hambúrguer delicioso");
        produtoDTO.setVlPreco(25.90);
        produtoDTO.setSnAtivo(true);
        produtoDTO.setTpCategoria("LANCHE");

        when(produtoFeignClient.buscarProdutoPorId(cdProduto)).thenReturn(produtoDTO);

        // Act
        Produto produto = produtoExternoService.buscarProdutoPorId(cdProduto);

        // Assert
        assertNotNull(produto);
        assertEquals(cdProduto, produto.getId());
        assertEquals("X-Burger", produto.getNome());
        assertEquals("Hambúrguer delicioso", produto.getDescricao());
        assertEquals(25.90, produto.getPreco());
        assertTrue(produto.isAtivo());
        assertEquals(CategoriaProduto.LANCHE, produto.getCategoria());

        verify(produtoFeignClient, times(1)).buscarProdutoPorId(cdProduto);
    }

    @Test
    @DisplayName("Deve lançar exceção quando API retornar null")
    void deveLancarExcecaoQuandoApiRetornarNull() {
        // Arrange
        String cdProduto = "produto-inexistente";
        when(produtoFeignClient.buscarProdutoPorId(cdProduto)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> produtoExternoService.buscarProdutoPorId(cdProduto)
        );

        assertTrue(exception.getMessage().contains("Erro interno ao processar produto"));
        verify(produtoFeignClient, times(1)).buscarProdutoPorId(cdProduto);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto estiver inativo")
    void deveLancarExcecaoQuandoProdutoEstiverInativo() {
        // Arrange
        String cdProduto = "produto-inativo";
        ProdutoExternoDTO produtoDTO = new ProdutoExternoDTO();
        produtoDTO.setCdProduto(cdProduto);
        produtoDTO.setNmProduto("Produto Inativo");
        produtoDTO.setDsDescricao("Produto desabilitado");
        produtoDTO.setVlPreco(10.00);
        produtoDTO.setSnAtivo(false); // INATIVO
        produtoDTO.setTpCategoria("LANCHE");

        when(produtoFeignClient.buscarProdutoPorId(cdProduto)).thenReturn(produtoDTO);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> produtoExternoService.buscarProdutoPorId(cdProduto)
        );

        assertTrue(exception.getMessage().contains("Erro interno ao processar produto"));
        verify(produtoFeignClient, times(1)).buscarProdutoPorId(cdProduto);
    }

    @Test
    @DisplayName("Deve lançar exceção quando API retornar 404 Not Found")
    void deveLancarExcecaoQuandoApiRetornar404NotFound() {
        // Arrange
        String cdProduto = "produto-404";
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(produtoFeignClient.buscarProdutoPorId(cdProduto)).thenThrow(notFoundException);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> produtoExternoService.buscarProdutoPorId(cdProduto)
        );

        assertEquals("Produto não encontrado: " + cdProduto, exception.getMessage());
        verify(produtoFeignClient, times(1)).buscarProdutoPorId(cdProduto);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para erro 500 Internal Server Error")
    void deveLancarRuntimeExceptionParaErro500InternalServerError() {
        // Arrange
        String cdProduto = "produto-500";
        FeignException.InternalServerError internalServerError = mock(FeignException.InternalServerError.class);
        when(internalServerError.getMessage()).thenReturn("Internal Server Error");
        when(produtoFeignClient.buscarProdutoPorId(cdProduto)).thenThrow(internalServerError);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> produtoExternoService.buscarProdutoPorId(cdProduto)
        );

        assertTrue(exception.getMessage().contains("API de produtos temporariamente indisponível"));
        assertEquals(internalServerError, exception.getCause());
        verify(produtoFeignClient, times(1)).buscarProdutoPorId(cdProduto);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para outros erros Feign")
    void deveLancarRuntimeExceptionParaOutrosErrosFeign() {
        // Arrange
        String cdProduto = "produto-erro";
        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(503);
        when(feignException.getMessage()).thenReturn("Service Unavailable");
        when(produtoFeignClient.buscarProdutoPorId(cdProduto)).thenThrow(feignException);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> produtoExternoService.buscarProdutoPorId(cdProduto)
        );

        assertTrue(exception.getMessage().contains("Erro ao comunicar com serviço de produtos"));
        assertEquals(feignException, exception.getCause());
        verify(produtoFeignClient, times(1)).buscarProdutoPorId(cdProduto);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para exceções gerais")
    void deveLancarRuntimeExceptionParaExcecoesGerais() {
        // Arrange
        String cdProduto = "produto-exception";
        RuntimeException runtimeException = new RuntimeException("Erro inesperado");
        when(produtoFeignClient.buscarProdutoPorId(cdProduto)).thenThrow(runtimeException);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> produtoExternoService.buscarProdutoPorId(cdProduto)
        );

        assertTrue(exception.getMessage().contains("Erro interno ao processar produto"));
        assertEquals(runtimeException, exception.getCause());
        verify(produtoFeignClient, times(1)).buscarProdutoPorId(cdProduto);
    }

    @Test
    @DisplayName("Deve buscar produto de diferentes categorias")
    void deveBuscarProdutoDeDiferentesCategorias() {
        // Arrange & Act & Assert
        String[] categorias = {"LANCHE", "ACOMPANHAMENTO", "BEBIDA", "SOBREMESA"};

        for (int i = 0; i < categorias.length; i++) {
            String cdProduto = "produto-" + i;
            ProdutoExternoDTO produtoDTO = new ProdutoExternoDTO();
            produtoDTO.setCdProduto(cdProduto);
            produtoDTO.setNmProduto("Produto " + categorias[i]);
            produtoDTO.setDsDescricao("Descrição");
            produtoDTO.setVlPreco(10.00 + i);
            produtoDTO.setSnAtivo(true);
            produtoDTO.setTpCategoria(categorias[i]);

            when(produtoFeignClient.buscarProdutoPorId(cdProduto)).thenReturn(produtoDTO);

            Produto produto = produtoExternoService.buscarProdutoPorId(cdProduto);

            assertEquals(CategoriaProduto.fromString(categorias[i]), produto.getCategoria());
        }

        verify(produtoFeignClient, times(categorias.length)).buscarProdutoPorId(any());
    }

    @Test
    @DisplayName("Deve buscar produto com preço zero")
    void deveBuscarProdutoComPrecoZero() {
        // Arrange
        String cdProduto = "produto-gratis";
        ProdutoExternoDTO produtoDTO = new ProdutoExternoDTO();
        produtoDTO.setCdProduto(cdProduto);
        produtoDTO.setNmProduto("Produto Gratuito");
        produtoDTO.setDsDescricao("Promoção especial");
        produtoDTO.setVlPreco(0.0);
        produtoDTO.setSnAtivo(true);
        produtoDTO.setTpCategoria("LANCHE");

        when(produtoFeignClient.buscarProdutoPorId(cdProduto)).thenReturn(produtoDTO);

        // Act
        Produto produto = produtoExternoService.buscarProdutoPorId(cdProduto);

        // Assert
        assertEquals(0.0, produto.getPreco());
        verify(produtoFeignClient, times(1)).buscarProdutoPorId(cdProduto);
    }

    @Test
    @DisplayName("Deve buscar produto com descrição nula")
    void deveBuscarProdutoComDescricaoNula() {
        // Arrange
        String cdProduto = "produto-sem-desc";
        ProdutoExternoDTO produtoDTO = new ProdutoExternoDTO();
        produtoDTO.setCdProduto(cdProduto);
        produtoDTO.setNmProduto("Produto Sem Descrição");
        produtoDTO.setDsDescricao(null);
        produtoDTO.setVlPreco(15.00);
        produtoDTO.setSnAtivo(true);
        produtoDTO.setTpCategoria("BEBIDA");

        when(produtoFeignClient.buscarProdutoPorId(cdProduto)).thenReturn(produtoDTO);

        // Act
        Produto produto = produtoExternoService.buscarProdutoPorId(cdProduto);

        // Assert
        assertNull(produto.getDescricao());
        verify(produtoFeignClient, times(1)).buscarProdutoPorId(cdProduto);
    }

    @Test
    @DisplayName("Deve lançar exceção para categoria inválida")
    void deveLancarExcecaoParaCategoriaInvalida() {
        // Arrange
        String cdProduto = "produto-categoria-invalida";
        ProdutoExternoDTO produtoDTO = new ProdutoExternoDTO();
        produtoDTO.setCdProduto(cdProduto);
        produtoDTO.setNmProduto("Produto Categoria Inválida");
        produtoDTO.setDsDescricao("Teste");
        produtoDTO.setVlPreco(10.00);
        produtoDTO.setSnAtivo(true);
        produtoDTO.setTpCategoria("CATEGORIA_INEXISTENTE");

        when(produtoFeignClient.buscarProdutoPorId(cdProduto)).thenReturn(produtoDTO);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> produtoExternoService.buscarProdutoPorId(cdProduto)
        );

        assertTrue(exception.getMessage().contains("Erro interno ao processar produto"));
        verify(produtoFeignClient, times(1)).buscarProdutoPorId(cdProduto);
    }
}
