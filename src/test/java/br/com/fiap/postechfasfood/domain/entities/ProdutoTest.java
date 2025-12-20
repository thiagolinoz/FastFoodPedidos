package br.com.fiap.postechfasfood.domain.entities;

import br.com.fiap.postechfasfood.domain.valueobjects.CategoriaProduto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da entidade Produto")
class ProdutoTest {

    @Test
    @DisplayName("Deve criar produto válido com sucesso")
    void deveCriarProdutoValidoComSucesso() {
        // Arrange & Act
        Produto produto = new Produto(
            "prod-001",
            "X-Burger",
            "Hambúrguer delicioso",
            25.90,
            true,
            CategoriaProduto.LANCHE
        );

        // Assert
        assertEquals("prod-001", produto.getId());
        assertEquals("X-Burger", produto.getNome());
        assertEquals("Hambúrguer delicioso", produto.getDescricao());
        assertEquals(25.90, produto.getPreco());
        assertTrue(produto.isAtivo());
        assertEquals(CategoriaProduto.LANCHE, produto.getCategoria());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo")
    void deveLancarExcecaoQuandoNomeForNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Produto("prod-001", null, "Descrição", 10.0, true, CategoriaProduto.LANCHE)
        );

        assertEquals("Nome do produto é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for vazio")
    void deveLancarExcecaoQuandoNomeForVazio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Produto("prod-001", "", "Descrição", 10.0, true, CategoriaProduto.LANCHE)
        );

        assertEquals("Nome do produto é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for apenas espaços")
    void deveLancarExcecaoQuandoNomeForApenasEspacos() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Produto("prod-001", "   ", "Descrição", 10.0, true, CategoriaProduto.LANCHE)
        );

        assertEquals("Nome do produto é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço for negativo")
    void deveLancarExcecaoQuandoPrecoForNegativo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Produto("prod-001", "Produto", "Descrição", -5.0, true, CategoriaProduto.LANCHE)
        );

        assertEquals("Preço não pode ser negativo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria for nula")
    void deveLancarExcecaoQuandoCategoriaForNula() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Produto("prod-001", "Produto", "Descrição", 10.0, true, null)
        );

        assertEquals("Categoria é obrigatória", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar preço zero")
    void deveAceitarPrecoZero() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            Produto produto = new Produto("prod-001", "Produto Grátis", "Descrição", 0.0, true, CategoriaProduto.LANCHE);
            assertEquals(0.0, produto.getPreco());
        });
    }

    @Test
    @DisplayName("Deve ativar produto corretamente")
    void deveAtivarProdutoCorretamente() {
        // Arrange
        Produto produto = new Produto("prod-001", "Produto", "Descrição", 10.0, false, CategoriaProduto.LANCHE);

        // Act
        produto.ativar();

        // Assert
        assertTrue(produto.isAtivo());
    }

    @Test
    @DisplayName("Deve desativar produto corretamente")
    void deveDesativarProdutoCorretamente() {
        // Arrange
        Produto produto = new Produto("prod-001", "Produto", "Descrição", 10.0, true, CategoriaProduto.LANCHE);

        // Act
        produto.desativar();

        // Assert
        assertFalse(produto.isAtivo());
    }

    @Test
    @DisplayName("Deve atualizar preço válido corretamente")
    void deveAtualizarPrecoValidoCorretamente() {
        // Arrange
        Produto produto = new Produto("prod-001", "Produto", "Descrição", 10.0, true, CategoriaProduto.LANCHE);

        // Act
        produto.atualizarPreco(15.50);

        // Assert
        assertEquals(15.50, produto.getPreco());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar preço negativo")
    void deveLancarExcecaoAoAtualizarPrecoNegativo() {
        // Arrange
        Produto produto = new Produto("prod-001", "Produto", "Descrição", 10.0, true, CategoriaProduto.LANCHE);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> produto.atualizarPreco(-5.0)
        );

        assertEquals("Preço não pode ser negativo", exception.getMessage());
        assertEquals(10.0, produto.getPreco()); // Preço não deve ter mudado
    }

    @Test
    @DisplayName("Deve aceitar atualização de preço para zero")
    void deveAceitarAtualizacaoDePrecoParaZero() {
        // Arrange
        Produto produto = new Produto("prod-001", "Produto", "Descrição", 10.0, true, CategoriaProduto.LANCHE);

        // Act
        produto.atualizarPreco(0.0);

        // Assert
        assertEquals(0.0, produto.getPreco());
    }

    @Test
    @DisplayName("Deve considerar produtos com mesmo ID como iguais")
    void deveConsiderarProdutosComMesmoIdComoIguais() {
        // Arrange
        Produto produto1 = new Produto("prod-001", "Produto A", "Descrição A", 10.0, true, CategoriaProduto.LANCHE);
        Produto produto2 = new Produto("prod-001", "Produto B", "Descrição B", 20.0, false, CategoriaProduto.BEBIDA);

        // Act & Assert
        assertEquals(produto1, produto2);
        assertEquals(produto1.hashCode(), produto2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar produtos com IDs diferentes como diferentes")
    void deveConsiderarProdutosComIdsDiferentesComoDiferentes() {
        // Arrange
        Produto produto1 = new Produto("prod-001", "Produto", "Descrição", 10.0, true, CategoriaProduto.LANCHE);
        Produto produto2 = new Produto("prod-002", "Produto", "Descrição", 10.0, true, CategoriaProduto.LANCHE);

        // Act & Assert
        assertNotEquals(produto1, produto2);
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com null")
    void deveRetornarFalseAoCompararComNull() {
        // Arrange
        Produto produto = new Produto("prod-001", "Produto", "Descrição", 10.0, true, CategoriaProduto.LANCHE);

        // Act & Assert
        assertNotEquals(produto, null);
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com objeto de classe diferente")
    void deveRetornarFalseAoCompararComObjetoDeClasseDiferente() {
        // Arrange
        Produto produto = new Produto("prod-001", "Produto", "Descrição", 10.0, true, CategoriaProduto.LANCHE);
        String outroObjeto = "não é produto";

        // Act & Assert
        assertNotEquals(produto, outroObjeto);
    }

    @Test
    @DisplayName("Deve retornar true ao comparar produto consigo mesmo")
    void deveRetornarTrueAoCompararProdutoConsigoMesmo() {
        // Arrange
        Produto produto = new Produto("prod-001", "Produto", "Descrição", 10.0, true, CategoriaProduto.LANCHE);

        // Act & Assert
        assertEquals(produto, produto);
    }

    @Test
    @DisplayName("Deve gerar toString com informações do produto")
    void deveGerarToStringComInformacoesDoProduto() {
        // Arrange
        Produto produto = new Produto("prod-001", "X-Burger", "Descrição", 25.90, true, CategoriaProduto.LANCHE);

        // Act
        String toString = produto.toString();

        // Assert
        assertTrue(toString.contains("prod-001"));
        assertTrue(toString.contains("X-Burger"));
        assertTrue(toString.contains("25.9"));
        assertTrue(toString.contains("LANCHE"));
    }

    @Test
    @DisplayName("Deve aceitar descrição nula")
    void deveAceitarDescricaoNula() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            Produto produto = new Produto("prod-001", "Produto", null, 10.0, true, CategoriaProduto.LANCHE);
            assertNull(produto.getDescricao());
        });
    }

    @Test
    @DisplayName("Deve aceitar ID nulo")
    void deveAceitarIdNulo() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            Produto produto = new Produto(null, "Produto", "Descrição", 10.0, true, CategoriaProduto.LANCHE);
            assertNull(produto.getId());
        });
    }

    @Test
    @DisplayName("Deve testar todas as categorias de produto")
    void deveTestarTodasAsCategoriasDeProduto() {
        // Test LANCHE
        Produto lanche = new Produto("1", "Lanche", "Desc", 10.0, true, CategoriaProduto.LANCHE);
        assertEquals(CategoriaProduto.LANCHE, lanche.getCategoria());

        // Test ACOMPANHAMENTO
        Produto acompanhamento = new Produto("2", "Batata", "Desc", 8.0, true, CategoriaProduto.ACOMPANHAMENTO);
        assertEquals(CategoriaProduto.ACOMPANHAMENTO, acompanhamento.getCategoria());

        // Test BEBIDA
        Produto bebida = new Produto("3", "Coca", "Desc", 5.0, true, CategoriaProduto.BEBIDA);
        assertEquals(CategoriaProduto.BEBIDA, bebida.getCategoria());

        // Test SOBREMESA
        Produto sobremesa = new Produto("4", "Sorvete", "Desc", 7.0, true, CategoriaProduto.SOBREMESA);
        assertEquals(CategoriaProduto.SOBREMESA, sobremesa.getCategoria());
    }
}
