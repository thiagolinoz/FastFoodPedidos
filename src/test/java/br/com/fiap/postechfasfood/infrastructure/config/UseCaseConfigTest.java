package br.com.fiap.postechfasfood.infrastructure.config;

import br.com.fiap.postechfasfood.domain.ports.input.*;
import br.com.fiap.postechfasfood.domain.ports.output.CatalogoServicePort;
import br.com.fiap.postechfasfood.domain.ports.output.PagamentoRepositoryPort;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.infrastructure.external.service.PessoaExternaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UseCaseConfigTest {

    @Mock
    private PedidoRepositoryPort pedidoRepository;

    @Mock
    private CatalogoServicePort catalogoService;

    @Mock
    private PessoaExternaService pessoaExternaService;

    @Mock
    private PagamentoRepositoryPort pagamentoRepository;

    private UseCaseConfig useCaseConfig;

    @BeforeEach
    void setUp() {
        useCaseConfig = new UseCaseConfig();
    }

    @Test
    @DisplayName("Deve criar bean CadastrarPedidoUseCase")
    void deveCriarBeanCadastrarPedidoUseCase() {
        // Act
        CadastrarPedidoUseCase useCase = useCaseConfig.cadastrarPedidoUseCase(
            pedidoRepository, catalogoService, pessoaExternaService);

        // Assert
        assertNotNull(useCase);
    }

    @Test
    @DisplayName("Deve criar bean AtualizarStatusPedidoUseCase")
    void deveCriarBeanAtualizarStatusPedidoUseCase() {
        // Act
        AtualizarStatusPedidoUseCase useCase = useCaseConfig.atualizarStatusPedidoUseCase(pedidoRepository);

        // Assert
        assertNotNull(useCase);
    }

    @Test
    @DisplayName("Deve criar bean ListarPedidosUseCase")
    void deveCriarBeanListarPedidosUseCase() {
        // Act
        ListarPedidosUseCase useCase = useCaseConfig.listarPedidosUseCase(pedidoRepository);

        // Assert
        assertNotNull(useCase);
    }

    @Test
    @DisplayName("Deve criar bean ConsultarStatusPagamentoUseCase")
    void deveCriarBeanConsultarStatusPagamentoUseCase() {
        // Act
        ConsultarStatusPagamentoUseCase useCase = useCaseConfig.consultarStatusPagamentoUseCase(
            pagamentoRepository, pedidoRepository);

        // Assert
        assertNotNull(useCase);
    }

    @Test
    @DisplayName("Deve criar bean ProcessarWebhookPagamentoUseCase")
    void deveCriarBeanProcessarWebhookPagamentoUseCase() {
        // Act
        ProcessarWebhookPagamentoUseCase useCase = useCaseConfig.processarWebhookPagamentoUseCase(
            pedidoRepository, pagamentoRepository);

        // Assert
        assertNotNull(useCase);
    }
}
