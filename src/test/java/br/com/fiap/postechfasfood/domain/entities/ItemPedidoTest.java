package br.com.fiap.postechfasfood.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da Entidade ItemPedido")
class ItemPedidoTest {

    @Test
    @DisplayName("Deve criar item pedido com todos os parâmetros válidos")
    void deveCriarItemPedidoComParametrosValidos() {
        // Arrange & Act
        ItemPedido item = new ItemPedido("Hamburguer", "PROD-001", 2, 15.90);

        // Assert
        assertNotNull(item);
        assertEquals("Hamburguer", item.getNomeProduto());
        assertEquals("PROD-001", item.getCodigoProduto());
        assertEquals(2, item.getQuantidade());
        assertEquals(15.90, item.getPrecoUnitario(), 0.01);
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do produto é nulo")
    void deveLancarExcecaoQuandoNomeProdutoNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido(null, "PROD-001", 2, 15.90)
        );

        assertEquals("Nome do produto é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do produto está em branco")
    void deveLancarExcecaoQuandoNomeProdutoEmBranco() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("   ", "PROD-001", 2, 15.90)
        );

        assertEquals("Nome do produto é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é zero")
    void deveLancarExcecaoQuandoQuantidadeZero() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("Hamburguer", "PROD-001", 0, 15.90)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é negativa")
    void deveLancarExcecaoQuandoQuantidadeNegativa() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("Hamburguer", "PROD-001", -1, 15.90)
        );

        assertEquals("Quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é negativo")
    void deveLancarExcecaoQuandoPrecoUnitarioNegativo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ItemPedido("Hamburguer", "PROD-001", 2, -15.90)
        );

        assertEquals("Preço unitário não pode ser negativo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar preço unitário zero")
    void deveAceitarPrecoUnitarioZero() {
        // Arrange & Act
        ItemPedido item = new ItemPedido("Brinde", "PROD-BRINDE", 1, 0.0);

        // Assert
        assertNotNull(item);
        assertEquals(0.0, item.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve calcular subtotal corretamente")
    void deveCalcularSubtotalCorretamente() {
        // Arrange
        ItemPedido item = new ItemPedido("Hamburguer", "PROD-001", 3, 15.90);

        // Act
        double subtotal = item.calcularSubtotal();

        // Assert
        assertEquals(47.70, subtotal, 0.01);
    }

    @Test
    @DisplayName("Deve calcular subtotal zero quando preço é zero")
    void deveCalcularSubtotalZeroQuandoPrecoZero() {
        // Arrange
        ItemPedido item = new ItemPedido("Brinde", "PROD-BRINDE", 5, 0.0);

        // Act
        double subtotal = item.calcularSubtotal();

        // Assert
        assertEquals(0.0, subtotal);
    }

    @Test
    @DisplayName("Deve comparar itens por código do produto usando equals")
    void deveCompararItensPorCodigoProdutoUsandoEquals() {
        // Arrange
        ItemPedido item1 = new ItemPedido("Hamburguer", "PROD-001", 2, 15.90);
        ItemPedido item2 = new ItemPedido("Hamburguer Premium", "PROD-001", 1, 20.00);

        // Act & Assert
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar false no equals quando códigos diferentes")
    void deveRetornarFalseNoEqualsQuandoCodigosDiferentes() {
        // Arrange
        ItemPedido item1 = new ItemPedido("Hamburguer", "PROD-001", 2, 15.90);
        ItemPedido item2 = new ItemPedido("Hamburguer", "PROD-002", 2, 15.90);

        // Act & Assert
        assertNotEquals(item1, item2);
    }

    @Test
    @DisplayName("Deve retornar true no equals quando compara com o mesmo objeto")
    void deveRetornarTrueNoEqualsQuandoComparaComMesmoObjeto() {
        // Arrange
        ItemPedido item = new ItemPedido("Hamburguer", "PROD-001", 2, 15.90);

        // Act & Assert
        assertEquals(item, item);
    }

    @Test
    @DisplayName("Deve retornar false no equals quando compara com null")
    void deveRetornarFalseNoEqualsQuandoComparaComNull() {
        // Arrange
        ItemPedido item = new ItemPedido("Hamburguer", "PROD-001", 2, 15.90);

        // Act & Assert
        assertNotEquals(null, item);
    }

    @Test
    @DisplayName("Deve retornar false no equals quando compara com objeto de classe diferente")
    void deveRetornarFalseNoEqualsQuandoComparaComClasseDiferente() {
        // Arrange
        ItemPedido item = new ItemPedido("Hamburguer", "PROD-001", 2, 15.90);
        String outraClasse = "String";

        // Act & Assert
        assertNotEquals(item, outraClasse);
    }

    @Test
    @DisplayName("Deve retornar representação em string do item")
    void deveRetornarRepresentacaoEmString() {
        // Arrange
        ItemPedido item = new ItemPedido("Hamburguer", "PROD-001", 2, 15.90);

        // Act
        String resultado = item.toString();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.contains("Hamburguer"));
        assertTrue(resultado.contains("quantidade=2"));
        assertTrue(resultado.contains("15.9"));
    }

    @Test
    @DisplayName("Deve aceitar código do produto nulo")
    void deveAceitarCodigoProdutoNulo() {
        // Arrange & Act
        ItemPedido item = new ItemPedido("Produto Sem Código", null, 1, 10.0);

        // Assert
        assertNotNull(item);
        assertNull(item.getCodigoProduto());
    }

    @Test
    @DisplayName("Deve calcular subtotal com quantidade 1")
    void deveCalcularSubtotalComQuantidadeUm() {
        // Arrange
        ItemPedido item = new ItemPedido("Refrigerante", "PROD-002", 1, 5.50);

        // Act
        double subtotal = item.calcularSubtotal();

        // Assert
        assertEquals(5.50, subtotal, 0.01);
    }

    @Test
    @DisplayName("Deve calcular subtotal com valores decimais")
    void deveCalcularSubtotalComValoresDecimais() {
        // Arrange
        ItemPedido item = new ItemPedido("Pizza", "PROD-003", 2, 35.75);

        // Act
        double subtotal = item.calcularSubtotal();

        // Assert
        assertEquals(71.50, subtotal, 0.01);
    }
}
