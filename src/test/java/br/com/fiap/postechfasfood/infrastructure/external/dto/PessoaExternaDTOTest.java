package br.com.fiap.postechfasfood.infrastructure.external.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do PessoaExternaDTO")
class PessoaExternaDTOTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Deve criar instância com construtor padrão")
    void deveCriarInstanciaComConstrutorPadrao() {
        // Act
        PessoaExternaDTO pessoa = new PessoaExternaDTO();

        // Assert
        assertNotNull(pessoa);
        assertNull(pessoa.getCdDocPessoa());
        assertNull(pessoa.getNmPessoa());
        assertNull(pessoa.getDsEmail());
        assertNull(pessoa.getTpPessoa());
        assertTrue(pessoa.isEmpty());
    }

    @Test
    @DisplayName("Deve definir e obter todos os campos corretamente")
    void deveDefinirEObterTodosCamposCorretamente() {
        // Arrange
        PessoaExternaDTO pessoa = new PessoaExternaDTO();

        // Act
        pessoa.setCdDocPessoa("00974950009");
        pessoa.setNmPessoa("myke");
        pessoa.setDsEmail("eumesmo@gmail.com");
        pessoa.setTpPessoa("CLIENTE");

        // Assert
        assertEquals("00974950009", pessoa.getCdDocPessoa());
        assertEquals("myke", pessoa.getNmPessoa());
        assertEquals("eumesmo@gmail.com", pessoa.getDsEmail());
        assertEquals("CLIENTE", pessoa.getTpPessoa());
        assertFalse(pessoa.isEmpty());
    }

    @Test
    @DisplayName("Deve deserializar JSON com todos os campos preenchidos")
    void deveDeserializarJsonComTodosCamposPreenchidos() throws Exception {
        // Arrange
        String json = """
                {
                  "cdDocPessoa": "00974950009",
                  "nmPessoa": "myke",
                  "tpPessoa": "CLIENTE",
                  "dsEmail": "eumesmo@gmail.com"
                }
                """;

        // Act
        PessoaExternaDTO pessoa = objectMapper.readValue(json, PessoaExternaDTO.class);

        // Assert
        assertEquals("00974950009", pessoa.getCdDocPessoa());
        assertEquals("myke", pessoa.getNmPessoa());
        assertEquals("eumesmo@gmail.com", pessoa.getDsEmail());
        assertEquals("CLIENTE", pessoa.getTpPessoa());
        assertFalse(pessoa.isEmpty());
    }

    @Test
    @DisplayName("Deve deserializar JSON sem cdDocPessoa")
    void deveDeserializarJsonSemCdDocPessoa() throws Exception {
        // Arrange
        String json = """
                {
                  "nmPessoa": "myke",
                  "tpPessoa": "CLIENTE",
                  "dsEmail": "eumesmo@gmail.com"
                }
                """;

        // Act
        PessoaExternaDTO pessoa = objectMapper.readValue(json, PessoaExternaDTO.class);

        // Assert
        assertNull(pessoa.getCdDocPessoa());
        assertEquals("myke", pessoa.getNmPessoa());
        assertEquals("eumesmo@gmail.com", pessoa.getDsEmail());
        assertEquals("CLIENTE", pessoa.getTpPessoa());
        assertFalse(pessoa.isEmpty());
    }

    @Test
    @DisplayName("Deve deserializar JSON com todos os campos null")
    void deveDeserializarJsonComTodosCamposNull() throws Exception {
        // Arrange
        String json = """
                {
                  "cdDocPessoa": null,
                  "nmPessoa": null,
                  "tpPessoa": null,
                  "dsEmail": null
                }
                """;

        // Act
        PessoaExternaDTO pessoa = objectMapper.readValue(json, PessoaExternaDTO.class);

        // Assert
        assertNull(pessoa.getCdDocPessoa());
        assertNull(pessoa.getNmPessoa());
        assertNull(pessoa.getDsEmail());
        assertNull(pessoa.getTpPessoa());
        assertTrue(pessoa.isEmpty());
    }

    @Test
    @DisplayName("Deve deserializar JSON vazio")
    void deveDeserializarJsonVazio() throws Exception {
        // Arrange
        String json = "{}";

        // Act
        PessoaExternaDTO pessoa = objectMapper.readValue(json, PessoaExternaDTO.class);

        // Assert
        assertNull(pessoa.getCdDocPessoa());
        assertNull(pessoa.getNmPessoa());
        assertNull(pessoa.getDsEmail());
        assertNull(pessoa.getTpPessoa());
        assertTrue(pessoa.isEmpty());
    }

    @Test
    @DisplayName("Deve serializar para JSON corretamente")
    void deveSerializarParaJsonCorretamente() throws Exception {
        // Arrange
        PessoaExternaDTO pessoa = new PessoaExternaDTO();
        pessoa.setCdDocPessoa("00974950009");
        pessoa.setNmPessoa("myke");
        pessoa.setDsEmail("eumesmo@gmail.com");
        pessoa.setTpPessoa("CLIENTE");

        // Act
        String json = objectMapper.writeValueAsString(pessoa);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("\"cdDocPessoa\":\"00974950009\""));
        assertTrue(json.contains("\"nmPessoa\":\"myke\""));
        assertTrue(json.contains("\"dsEmail\":\"eumesmo@gmail.com\""));
        assertTrue(json.contains("\"tpPessoa\":\"CLIENTE\""));
    }

    @Test
    @DisplayName("Deve ignorar campos desconhecidos no JSON")
    void deveIgnorarCamposDesconhecidosNoJson() throws Exception {
        // Arrange
        String json = """
                {
                  "cdDocPessoa": "00974950009",
                  "nmPessoa": "myke",
                  "tpPessoa": "CLIENTE",
                  "dsEmail": "eumesmo@gmail.com",
                  "campoDesconhecido1": "valor1",
                  "campoDesconhecido2": 123,
                  "campoDesconhecido3": true
                }
                """;

        // Act & Assert - não deve lançar exceção
        assertDoesNotThrow(() -> {
            PessoaExternaDTO pessoa = objectMapper.readValue(json, PessoaExternaDTO.class);

            assertEquals("00974950009", pessoa.getCdDocPessoa());
            assertEquals("myke", pessoa.getNmPessoa());
            assertEquals("eumesmo@gmail.com", pessoa.getDsEmail());
            assertEquals("CLIENTE", pessoa.getTpPessoa());
        });
    }

    @Test
    @DisplayName("Deve retornar true para isEmpty quando todos os campos são null")
    void deveRetornarTrueParaIsEmptyQuandoTodosCamposSaoNull() {
        // Arrange
        PessoaExternaDTO pessoa = new PessoaExternaDTO();

        // Act & Assert
        assertTrue(pessoa.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar false para isEmpty quando pelo menos um campo está preenchido")
    void deveRetornarFalseParaIsEmptyQuandoPeloMenosUmCampoEstaPreenchido() {
        // Test com cdDocPessoa preenchido
        PessoaExternaDTO pessoa1 = new PessoaExternaDTO();
        pessoa1.setCdDocPessoa("123456789");
        assertFalse(pessoa1.isEmpty());

        // Test com nmPessoa preenchido
        PessoaExternaDTO pessoa2 = new PessoaExternaDTO();
        pessoa2.setNmPessoa("João");
        assertFalse(pessoa2.isEmpty());

        // Test com dsEmail preenchido
        PessoaExternaDTO pessoa3 = new PessoaExternaDTO();
        pessoa3.setDsEmail("joao@email.com");
        assertFalse(pessoa3.isEmpty());

        // Test com tpPessoa preenchido
        PessoaExternaDTO pessoa4 = new PessoaExternaDTO();
        pessoa4.setTpPessoa("CLIENTE");
        assertFalse(pessoa4.isEmpty());
    }

    @Test
    @DisplayName("Deve funcionar corretamente com equals e hashCode (Lombok)")
    void deveFuncionarCorretamenteComEqualsEHashCode() {
        // Arrange
        PessoaExternaDTO pessoa1 = new PessoaExternaDTO();
        pessoa1.setCdDocPessoa("00974950009");
        pessoa1.setNmPessoa("myke");
        pessoa1.setDsEmail("eumesmo@gmail.com");
        pessoa1.setTpPessoa("CLIENTE");

        PessoaExternaDTO pessoa2 = new PessoaExternaDTO();
        pessoa2.setCdDocPessoa("00974950009");
        pessoa2.setNmPessoa("myke");
        pessoa2.setDsEmail("eumesmo@gmail.com");
        pessoa2.setTpPessoa("CLIENTE");

        PessoaExternaDTO pessoa3 = new PessoaExternaDTO();
        pessoa3.setCdDocPessoa("11111111111");
        pessoa3.setNmPessoa("joão");
        pessoa3.setDsEmail("joao@email.com");
        pessoa3.setTpPessoa("FUNCIONARIO");

        // Act & Assert
        assertEquals(pessoa1, pessoa2);
        assertEquals(pessoa1.hashCode(), pessoa2.hashCode());
        assertNotEquals(pessoa1, pessoa3);
        assertNotEquals(pessoa1.hashCode(), pessoa3.hashCode());
    }

    @Test
    @DisplayName("Deve gerar toString corretamente (Lombok)")
    void deveGerarToStringCorretamente() {
        // Arrange
        PessoaExternaDTO pessoa = new PessoaExternaDTO();
        pessoa.setCdDocPessoa("00974950009");
        pessoa.setNmPessoa("myke");
        pessoa.setDsEmail("eumesmo@gmail.com");
        pessoa.setTpPessoa("CLIENTE");

        // Act
        String toString = pessoa.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("PessoaExternaDTO"));
        assertTrue(toString.contains("cdDocPessoa=00974950009"));
        assertTrue(toString.contains("nmPessoa=myke"));
        assertTrue(toString.contains("dsEmail=eumesmo@gmail.com"));
        assertTrue(toString.contains("tpPessoa=CLIENTE"));
    }

    @Test
    @DisplayName("Deve permitir alteração dos campos após criação")
    void devePermitirAlteracaoDosCamposAposCriacao() {
        // Arrange
        PessoaExternaDTO pessoa = new PessoaExternaDTO();
        pessoa.setCdDocPessoa("11111111111");
        pessoa.setNmPessoa("João");
        pessoa.setDsEmail("joao@teste.com");
        pessoa.setTpPessoa("FUNCIONARIO");

        // Act
        pessoa.setCdDocPessoa("22222222222");
        pessoa.setNmPessoa("Maria");
        pessoa.setDsEmail("maria@teste.com");
        pessoa.setTpPessoa("CLIENTE");

        // Assert
        assertEquals("22222222222", pessoa.getCdDocPessoa());
        assertEquals("Maria", pessoa.getNmPessoa());
        assertEquals("maria@teste.com", pessoa.getDsEmail());
        assertEquals("CLIENTE", pessoa.getTpPessoa());
    }
}
