package br.com.fiap.postechfasfood.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Fast Food Pedidos - API")
                .version("1.0.0")
                .description("""
                    API de Gestão de Pedidos seguindo Arquitetura Hexagonal (Ports and Adapters) 
                    e Clean Architecture.
                    
                    ## Arquitetura
                    - **Domínio**: Entidades e regras de negócio puras
                    - **Ports**: Interfaces que definem contratos
                    - **Use Cases**: Implementação das regras de negócio
                    - **Adapters**: Implementações concretas (REST, JDBC, etc.)
                    
                    ## Tecnologias
                    - Java 21
                    - Spring Boot 3.4.5
                    - MySQL (com JDBC puro, sem JPA)
                    
                    ## Endpoints
                    - POST /api/v1/pedidos/checkout - Realizar checkout (usa nome do produto)
                    - PATCH /api/v1/pedidos/{id}/status/{status} - Atualizar status
                    - GET /api/v1/pedidos - Listar pedidos ordenados
                    - GET /api/v1/pedidos/{nr}/pagamento/status - Consultar pagamento
                    - POST /webhook/mercado-pago/pagamentos/{nr} - Webhook de pagamento
                """)
                .contact(new Contact()
                    .name("FIAP - Pós Tech")));
    }
}
