package br.com.fiap.postechfasfood.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do enum StatusPagamento")
class StatusPagamentoTest {

    @Test
    @DisplayName("Deve retornar descrição correta para PENDENTE")
    void deveRetornarDescricaoCorretaParaPendente() {
        // Act & Assert
        assertEquals("Pendente", StatusPagamento.PENDENTE.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar descrição correta para APROVADO")
    void deveRetornarDescricaoCorretaParaAprovado() {
        // Act & Assert
        assertEquals("Aprovado", StatusPagamento.APROVADO.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar descrição correta para RECUSADO")
    void deveRetornarDescricaoCorretaParaRecusado() {
        // Act & Assert
        assertEquals("Recusado", StatusPagamento.RECUSADO.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar descrição correta para CANCELADO")
    void deveRetornarDescricaoCorretaParaCancelado() {
        // Act & Assert
        assertEquals("Cancelado", StatusPagamento.CANCELADO.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar true para isAprovado apenas quando status for APROVADO")
    void deveRetornarTrueParaIsAprovadoApenasQuandoStatusForAprovado() {
        // Act & Assert
        assertTrue(StatusPagamento.APROVADO.isAprovado());
        assertFalse(StatusPagamento.PENDENTE.isAprovado());
        assertFalse(StatusPagamento.RECUSADO.isAprovado());
        assertFalse(StatusPagamento.CANCELADO.isAprovado());
    }

    @Test
    @DisplayName("Deve retornar true para isRecusado quando status for RECUSADO ou CANCELADO")
    void deveRetornarTrueParaIsRecusadoQuandoStatusForRecusadoOuCancelado() {
        // Act & Assert
        assertTrue(StatusPagamento.RECUSADO.isRecusado());
        assertTrue(StatusPagamento.CANCELADO.isRecusado());
        assertFalse(StatusPagamento.APROVADO.isRecusado());
        assertFalse(StatusPagamento.PENDENTE.isRecusado());
    }

    @Test
    @DisplayName("Deve encontrar status por string exata maiúscula")
    void deveEncontrarStatusPorStringExataMaiuscula() {
        // Act & Assert
        assertEquals(StatusPagamento.PENDENTE, StatusPagamento.fromString("PENDENTE"));
        assertEquals(StatusPagamento.APROVADO, StatusPagamento.fromString("APROVADO"));
        assertEquals(StatusPagamento.RECUSADO, StatusPagamento.fromString("RECUSADO"));
        assertEquals(StatusPagamento.CANCELADO, StatusPagamento.fromString("CANCELADO"));
    }

    @Test
    @DisplayName("Deve encontrar status por string minúscula")
    void deveEncontrarStatusPorStringMinuscula() {
        // Act & Assert
        assertEquals(StatusPagamento.PENDENTE, StatusPagamento.fromString("pendente"));
        assertEquals(StatusPagamento.APROVADO, StatusPagamento.fromString("aprovado"));
        assertEquals(StatusPagamento.RECUSADO, StatusPagamento.fromString("recusado"));
        assertEquals(StatusPagamento.CANCELADO, StatusPagamento.fromString("cancelado"));
    }

    @Test
    @DisplayName("Deve encontrar status por string com case misto")
    void deveEncontrarStatusPorStringComCaseMisto() {
        // Act & Assert
        assertEquals(StatusPagamento.PENDENTE, StatusPagamento.fromString("Pendente"));
        assertEquals(StatusPagamento.APROVADO, StatusPagamento.fromString("AprovaDo"));
        assertEquals(StatusPagamento.RECUSADO, StatusPagamento.fromString("RecusAdo"));
        assertEquals(StatusPagamento.CANCELADO, StatusPagamento.fromString("CancelAdo"));
    }

    @Test
    @DisplayName("Deve lançar exceção para status inválido")
    void deveLancarExcecaoParaStatusInvalido() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StatusPagamento.fromString("STATUS_INEXISTENTE")
        );

        assertEquals("Status de pagamento inválido: STATUS_INEXISTENTE", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para string nula")
    void deveLancarExcecaoParaStringNula() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StatusPagamento.fromString(null)
        );

        assertEquals("Status de pagamento inválido: null", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para string vazia")
    void deveLancarExcecaoParaStringVazia() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StatusPagamento.fromString("")
        );

        assertEquals("Status de pagamento inválido: ", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para string com espaços")
    void deveLancarExcecaoParaStringComEspacos() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> StatusPagamento.fromString("   ")
        );

        assertEquals("Status de pagamento inválido:    ", exception.getMessage());
    }

    @Test
    @DisplayName("Deve ter exatamente 4 status")
    void deveTerExatamente4Status() {
        // Act
        StatusPagamento[] status = StatusPagamento.values();

        // Assert
        assertEquals(4, status.length);
    }

    @Test
    @DisplayName("Deve conter todos os status esperados")
    void deveConterTodosOsStatusEsperados() {
        // Act
        StatusPagamento[] status = StatusPagamento.values();

        // Assert
        assertTrue(java.util.Arrays.asList(status).contains(StatusPagamento.PENDENTE));
        assertTrue(java.util.Arrays.asList(status).contains(StatusPagamento.APROVADO));
        assertTrue(java.util.Arrays.asList(status).contains(StatusPagamento.RECUSADO));
        assertTrue(java.util.Arrays.asList(status).contains(StatusPagamento.CANCELADO));
    }

    @Test
    @DisplayName("Deve retornar name() corretamente para cada status")
    void deveRetornarNameCorretamenteParaCadaStatus() {
        // Act & Assert
        assertEquals("PENDENTE", StatusPagamento.PENDENTE.name());
        assertEquals("APROVADO", StatusPagamento.APROVADO.name());
        assertEquals("RECUSADO", StatusPagamento.RECUSADO.name());
        assertEquals("CANCELADO", StatusPagamento.CANCELADO.name());
    }

    @Test
    @DisplayName("Deve funcionar com valueOf()")
    void deveFuncionarComValueOf() {
        // Act & Assert
        assertEquals(StatusPagamento.PENDENTE, StatusPagamento.valueOf("PENDENTE"));
        assertEquals(StatusPagamento.APROVADO, StatusPagamento.valueOf("APROVADO"));
        assertEquals(StatusPagamento.RECUSADO, StatusPagamento.valueOf("RECUSADO"));
        assertEquals(StatusPagamento.CANCELADO, StatusPagamento.valueOf("CANCELADO"));
    }

    @Test
    @DisplayName("Deve lançar exceção no valueOf() para valor inválido")
    void deveLancarExcecaoNoValueOfParaValorInvalido() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> StatusPagamento.valueOf("STATUS_INEXISTENTE")
        );
    }

    @Test
    @DisplayName("Deve funcionar corretamente em switch statement")
    void deveFuncionarCorretamenteEmSwitchStatement() {
        // Act & Assert
        for (StatusPagamento status : StatusPagamento.values()) {
            String resultado = switch (status) {
                case PENDENTE -> "É pendente";
                case APROVADO -> "É aprovado";
                case RECUSADO -> "É recusado";
                case CANCELADO -> "É cancelado";
            };
            assertNotNull(resultado);
        }
    }

    @Test
    @DisplayName("Deve ter ordinal correto para cada status")
    void deveTerOrdinalCorretoParaCadaStatus() {
        // Act & Assert
        assertEquals(0, StatusPagamento.PENDENTE.ordinal());
        assertEquals(1, StatusPagamento.APROVADO.ordinal());
        assertEquals(2, StatusPagamento.RECUSADO.ordinal());
        assertEquals(3, StatusPagamento.CANCELADO.ordinal());
    }

    @Test
    @DisplayName("Deve testar compareTo()")
    void deveTestarCompareTo() {
        // Act & Assert
        assertTrue(StatusPagamento.PENDENTE.compareTo(StatusPagamento.APROVADO) < 0);
        assertTrue(StatusPagamento.APROVADO.compareTo(StatusPagamento.RECUSADO) < 0);
        assertTrue(StatusPagamento.RECUSADO.compareTo(StatusPagamento.CANCELADO) < 0);
        assertEquals(0, StatusPagamento.PENDENTE.compareTo(StatusPagamento.PENDENTE));
    }

    @Test
    @DisplayName("Deve testar lógica de negócio dos métodos de validação")
    void deveTestarLogicaDeNegociosDosMetodosDeValidacao() {
        // Para todos os status, apenas um deve ser aprovado
        int aprovados = 0;
        for (StatusPagamento status : StatusPagamento.values()) {
            if (status.isAprovado()) {
                aprovados++;
            }
        }
        assertEquals(1, aprovados, "Apenas APROVADO deve retornar true para isAprovado()");

        // RECUSADO e CANCELADO devem ser considerados recusados
        assertTrue(StatusPagamento.RECUSADO.isRecusado());
        assertTrue(StatusPagamento.CANCELADO.isRecusado());

        // PENDENTE não deve ser nem aprovado nem recusado
        assertFalse(StatusPagamento.PENDENTE.isAprovado());
        assertFalse(StatusPagamento.PENDENTE.isRecusado());
    }
}
