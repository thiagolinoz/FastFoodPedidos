package br.com.fiap.postechfasfood.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do enum CategoriaProduto")
class CategoriaProdutoTest {

    @Test
    @DisplayName("Deve retornar descrição correta para LANCHE")
    void deveRetornarDescricaoCorretaParaLanche() {
        // Act & Assert
        assertEquals("Lanche", CategoriaProduto.LANCHE.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar descrição correta para ACOMPANHAMENTO")
    void deveRetornarDescricaoCorretaParaAcompanhamento() {
        // Act & Assert
        assertEquals("Acompanhamento", CategoriaProduto.ACOMPANHAMENTO.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar descrição correta para BEBIDA")
    void deveRetornarDescricaoCorretaParaBebida() {
        // Act & Assert
        assertEquals("Bebida", CategoriaProduto.BEBIDA.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar descrição correta para SOBREMESA")
    void deveRetornarDescricaoCorretaParaSobremesa() {
        // Act & Assert
        assertEquals("Sobremesa", CategoriaProduto.SOBREMESA.getDescricao());
    }

    @Test
    @DisplayName("Deve encontrar categoria por string exata maiúscula")
    void deveEncontrarCategoriaPorStringExataMaiuscula() {
        // Act & Assert
        assertEquals(CategoriaProduto.LANCHE, CategoriaProduto.fromString("LANCHE"));
        assertEquals(CategoriaProduto.ACOMPANHAMENTO, CategoriaProduto.fromString("ACOMPANHAMENTO"));
        assertEquals(CategoriaProduto.BEBIDA, CategoriaProduto.fromString("BEBIDA"));
        assertEquals(CategoriaProduto.SOBREMESA, CategoriaProduto.fromString("SOBREMESA"));
    }

    @Test
    @DisplayName("Deve encontrar categoria por string minúscula")
    void deveEncontrarCategoriaPorStringMinuscula() {
        // Act & Assert
        assertEquals(CategoriaProduto.LANCHE, CategoriaProduto.fromString("lanche"));
        assertEquals(CategoriaProduto.ACOMPANHAMENTO, CategoriaProduto.fromString("acompanhamento"));
        assertEquals(CategoriaProduto.BEBIDA, CategoriaProduto.fromString("bebida"));
        assertEquals(CategoriaProduto.SOBREMESA, CategoriaProduto.fromString("sobremesa"));
    }

    @Test
    @DisplayName("Deve encontrar categoria por string com case misto")
    void deveEncontrarCategoriaPorStringComCaseMisto() {
        // Act & Assert
        assertEquals(CategoriaProduto.LANCHE, CategoriaProduto.fromString("Lanche"));
        assertEquals(CategoriaProduto.ACOMPANHAMENTO, CategoriaProduto.fromString("AcompanhaMento"));
        assertEquals(CategoriaProduto.BEBIDA, CategoriaProduto.fromString("BeBiDa"));
        assertEquals(CategoriaProduto.SOBREMESA, CategoriaProduto.fromString("SobreMesa"));
    }

    @Test
    @DisplayName("Deve lançar exceção para categoria inválida")
    void deveLancarExcecaoParaCategoriaInvalida() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CategoriaProduto.fromString("CATEGORIA_INEXISTENTE")
        );

        assertEquals("Categoria inválida: CATEGORIA_INEXISTENTE", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para string nula")
    void deveLancarExcecaoParaStringNula() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CategoriaProduto.fromString(null)
        );

        assertEquals("Categoria inválida: null", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para string vazia")
    void deveLancarExcecaoParaStringVazia() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CategoriaProduto.fromString("")
        );

        assertEquals("Categoria inválida: ", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para string com espaços")
    void deveLancarExcecaoParaStringComEspacos() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> CategoriaProduto.fromString("   ")
        );

        assertEquals("Categoria inválida:    ", exception.getMessage());
    }

    @Test
    @DisplayName("Deve ter exatamente 4 categorias")
    void deveTerExatamente4Categorias() {
        // Act
        CategoriaProduto[] categorias = CategoriaProduto.values();

        // Assert
        assertEquals(4, categorias.length);
    }

    @Test
    @DisplayName("Deve conter todas as categorias esperadas")
    void deveConterTodasAsCategoriasEsperadas() {
        // Act
        CategoriaProduto[] categorias = CategoriaProduto.values();

        // Assert
        assertTrue(java.util.Arrays.asList(categorias).contains(CategoriaProduto.LANCHE));
        assertTrue(java.util.Arrays.asList(categorias).contains(CategoriaProduto.ACOMPANHAMENTO));
        assertTrue(java.util.Arrays.asList(categorias).contains(CategoriaProduto.BEBIDA));
        assertTrue(java.util.Arrays.asList(categorias).contains(CategoriaProduto.SOBREMESA));
    }

    @Test
    @DisplayName("Deve retornar name() corretamente para cada categoria")
    void deveRetornarNameCorretamenteParaCadaCategoria() {
        // Act & Assert
        assertEquals("LANCHE", CategoriaProduto.LANCHE.name());
        assertEquals("ACOMPANHAMENTO", CategoriaProduto.ACOMPANHAMENTO.name());
        assertEquals("BEBIDA", CategoriaProduto.BEBIDA.name());
        assertEquals("SOBREMESA", CategoriaProduto.SOBREMESA.name());
    }

    @Test
    @DisplayName("Deve funcionar com valueOf()")
    void deveFuncionarComValueOf() {
        // Act & Assert
        assertEquals(CategoriaProduto.LANCHE, CategoriaProduto.valueOf("LANCHE"));
        assertEquals(CategoriaProduto.ACOMPANHAMENTO, CategoriaProduto.valueOf("ACOMPANHAMENTO"));
        assertEquals(CategoriaProduto.BEBIDA, CategoriaProduto.valueOf("BEBIDA"));
        assertEquals(CategoriaProduto.SOBREMESA, CategoriaProduto.valueOf("SOBREMESA"));
    }

    @Test
    @DisplayName("Deve lançar exceção no valueOf() para valor inválido")
    void deveLancarExcecaoNoValueOfParaValorInvalido() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> CategoriaProduto.valueOf("CATEGORIA_INEXISTENTE")
        );
    }

    @Test
    @DisplayName("Deve funcionar corretamente em switch statement")
    void deveFuncionarCorretamenteEmSwitchStatement() {
        // Act & Assert
        for (CategoriaProduto categoria : CategoriaProduto.values()) {
            String resultado = switch (categoria) {
                case LANCHE -> "É lanche";
                case ACOMPANHAMENTO -> "É acompanhamento";
                case BEBIDA -> "É bebida";
                case SOBREMESA -> "É sobremesa";
            };
            assertNotNull(resultado);
        }
    }

    @Test
    @DisplayName("Deve ter ordinal correto para cada categoria")
    void deveTerOrdinalCorretoParaCadaCategoria() {
        // Act & Assert
        assertEquals(0, CategoriaProduto.LANCHE.ordinal());
        assertEquals(1, CategoriaProduto.ACOMPANHAMENTO.ordinal());
        assertEquals(2, CategoriaProduto.BEBIDA.ordinal());
        assertEquals(3, CategoriaProduto.SOBREMESA.ordinal());
    }

    @Test
    @DisplayName("Deve testar compareTo()")
    void deveTestarCompareTo() {
        // Act & Assert
        assertTrue(CategoriaProduto.LANCHE.compareTo(CategoriaProduto.ACOMPANHAMENTO) < 0);
        assertTrue(CategoriaProduto.ACOMPANHAMENTO.compareTo(CategoriaProduto.BEBIDA) < 0);
        assertTrue(CategoriaProduto.BEBIDA.compareTo(CategoriaProduto.SOBREMESA) < 0);
        assertEquals(0, CategoriaProduto.LANCHE.compareTo(CategoriaProduto.LANCHE));
    }
}
