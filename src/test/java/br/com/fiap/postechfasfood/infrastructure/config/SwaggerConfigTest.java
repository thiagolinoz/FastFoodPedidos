package br.com.fiap.postechfasfood.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SwaggerConfig.class)
class SwaggerConfigTest {

    @Autowired
    private SwaggerConfig swaggerConfig;

    @Test
    @DisplayName("Deve configurar OpenAPI corretamente")
    void deveConfigurarOpenAPICorretamente() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Assert
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());

        assertEquals("Fast Food Pedidos - API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getDescription());
        assertTrue(openAPI.getInfo().getDescription().contains("Arquitetura Hexagonal"));

        assertNotNull(openAPI.getInfo().getContact());
        assertEquals("FIAP - Pós Tech", openAPI.getInfo().getContact().getName());
    }

    @Test
    @DisplayName("Deve conter informações sobre endpoints na descrição")
    void deveConterInformacoesSobreEndpointsNaDescricao() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        String description = openAPI.getInfo().getDescription();

        // Assert
        assertTrue(description.contains("POST /api/v1/pedidos/checkout"));
        assertTrue(description.contains("PATCH /api/v1/pedidos"));
        assertTrue(description.contains("GET /api/v1/pedidos"));
        assertTrue(description.contains("POST /webhook/mercado-pago/pagamentos"));
    }

    @Test
    @DisplayName("Deve conter informações sobre tecnologias na descrição")
    void deveConterInformacoesSobreTecnologiasNaDescricao() {
        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        String description = openAPI.getInfo().getDescription();

        // Assert
        assertTrue(description.contains("Java 21"));
        assertTrue(description.contains("Spring Boot"));
        assertTrue(description.contains("MySQL"));
    }
}
