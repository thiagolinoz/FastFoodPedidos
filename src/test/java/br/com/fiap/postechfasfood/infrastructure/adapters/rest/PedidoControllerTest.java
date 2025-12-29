package br.com.fiap.postechfasfood.infrastructure.adapters.rest;

import br.com.fiap.postechfasfood.domain.entities.ItemPedido;
import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.input.AtualizarStatusPedidoUseCase;
import br.com.fiap.postechfasfood.domain.ports.input.BuscarPedidoPorNumeroUseCase;
import br.com.fiap.postechfasfood.domain.ports.input.BuscarPedidosPorStatusUseCase;
import br.com.fiap.postechfasfood.domain.ports.input.CadastrarPedidoUseCase;
import br.com.fiap.postechfasfood.domain.ports.input.ConsultarStatusPagamentoUseCase;
import br.com.fiap.postechfasfood.domain.ports.input.ListarPedidosUseCase;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.CheckoutPedidoRequest;
import br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto.CheckoutPedidoRequest.ItemPedidoRequest;
import br.com.fiap.postechfasfood.infrastructure.external.service.PessoaExternaService;
import br.com.fiap.postechfasfood.infrastructure.external.service.ProdutoExternoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PedidoController")
class PedidoControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CadastrarPedidoUseCase cadastrarPedidoUseCase;

    @Mock
    private AtualizarStatusPedidoUseCase atualizarStatusPedidoUseCase;

    @Mock
    private ListarPedidosUseCase listarPedidosUseCase;

    @Mock
    private ConsultarStatusPagamentoUseCase consultarStatusPagamentoUseCase;

    @Mock
    private BuscarPedidoPorNumeroUseCase buscarPedidoPorNumeroUseCase;

    @Mock
    private BuscarPedidosPorStatusUseCase buscarPedidosPorStatusUseCase;

    @Mock
    private ProdutoExternoService produtoExternoService;

    @Mock
    private PessoaExternaService pessoaExternaService;

    private Pedido pedidoMock;
    private CheckoutPedidoRequest checkoutRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        PedidoController controller = new PedidoController(
            cadastrarPedidoUseCase,
            atualizarStatusPedidoUseCase,
            listarPedidosUseCase,
            consultarStatusPagamentoUseCase,
            buscarPedidoPorNumeroUseCase,
            buscarPedidosPorStatusUseCase,
            produtoExternoService,
            pessoaExternaService
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));

        pedidoMock = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(itens)
                .build();

        List<ItemPedidoRequest> itensRequest = List.of(
                new ItemPedidoRequest("Hamburguer", 2)
        );
        checkoutRequest = new CheckoutPedidoRequest("12345678900", itensRequest);
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso via checkout")
    void deveCriarPedidoComSucessoViaCheckout() throws Exception {
        // Arrange
        when(cadastrarPedidoUseCase.executar(any(CadastrarPedidoUseCase.CadastrarPedidoRequest.class)))
                .thenReturn(pedidoMock);

        // Act & Assert
        mockMvc.perform(post("/api/v1/pedidos/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkoutRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroPedido", is(1)))
                .andExpect(jsonPath("$.status", is("RECEBIDO")))
                .andExpect(jsonPath("$.documentoCliente", is("12345678900")));

        verify(cadastrarPedidoUseCase, times(1))
                .executar(any(CadastrarPedidoUseCase.CadastrarPedidoRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 400 quando checkout request inválido")
    void deveRetornar400QuandoCheckoutRequestInvalido() throws Exception {
        // Arrange - request sem documento
        CheckoutPedidoRequest requestInvalido = new CheckoutPedidoRequest(null, List.of());

        // Act & Assert
        mockMvc.perform(post("/api/v1/pedidos/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());

        verify(cadastrarPedidoUseCase, never()).executar(any());
    }

    @Test
    @DisplayName("Deve atualizar status do pedido com sucesso")
    void deveAtualizarStatusDoPedidoComSucesso() throws Exception {
        // Arrange
        String pedidoId = pedidoMock.getId();
        pedidoMock.atualizarStatus(StatusPedido.EM_PREPARACAO);
        
        when(atualizarStatusPedidoUseCase.executar(eq(pedidoId), eq(StatusPedido.EM_PREPARACAO)))
                .thenReturn(pedidoMock);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/pedidos/{cdPedido}/status/{txStatus}", 
                        pedidoId, "EM_PREPARACAO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("EM_PREPARACAO")));

        verify(atualizarStatusPedidoUseCase, times(1))
                .executar(pedidoId, StatusPedido.EM_PREPARACAO);
    }

    @Test
    @DisplayName("Deve listar todos os pedidos")
    void deveListarTodosPedidos() throws Exception {
        // Arrange
        List<Pedido> pedidos = List.of(pedidoMock);
        when(listarPedidosUseCase.executar()).thenReturn(pedidos);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numeroPedido", is(1)))
                .andExpect(jsonPath("$[0].status", is("RECEBIDO")));

        verify(listarPedidosUseCase, times(1)).executar();
    }

    @Test
    @DisplayName("Deve consultar status de pagamento")
    void deveConsultarStatusDePagamento() throws Exception {
        // Arrange
        Integer numeroPedido = 1;
        when(consultarStatusPagamentoUseCase.executar(numeroPedido))
                .thenReturn(StatusPagamento.APROVADO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos/{nrPedido}/pagamento/status", numeroPedido)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroPedido", is(numeroPedido)))
                .andExpect(jsonPath("$.status", is("APROVADO")));

        verify(consultarStatusPagamentoUseCase, times(1)).executar(numeroPedido);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há pedidos")
    void deveRetornarListaVaziaQuandoNaoHaPedidos() throws Exception {
        // Arrange
        when(listarPedidosUseCase.executar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Deve aceitar diferentes status válidos")
    void deveAceitarDiferentesStatusValidos() throws Exception {
        // Arrange
        String pedidoId = pedidoMock.getId();
        
        // Criar um novo pedido já com status PRONTO
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));
        Pedido pedidoPronto = new Pedido.Builder()
                .id(pedidoId)
                .documentoCliente("12345678900")
                .status(StatusPedido.PRONTO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(itens)
                .build();
        
        when(atualizarStatusPedidoUseCase.executar(eq(pedidoId), eq(StatusPedido.PRONTO)))
                .thenReturn(pedidoPronto);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/pedidos/{cdPedido}/status/{txStatus}", 
                        pedidoId, "PRONTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PRONTO")));
    }

    @Test
    @DisplayName("Deve buscar pedido por número com sucesso")
    void deveBuscarPedidoPorNumeroComSucesso() throws Exception {
        // Arrange
        Integer numeroPedido = 1;
        when(buscarPedidoPorNumeroUseCase.executar(numeroPedido))
                .thenReturn(pedidoMock);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos/numero/{numeroPedido}", numeroPedido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroPedido", is(1)))
                .andExpect(jsonPath("$.status", is("RECEBIDO")))
                .andExpect(jsonPath("$.documentoCliente", is("12345678900")));
    }

    @Test
    @DisplayName("Deve retornar 404 quando pedido não for encontrado por número")
    void deveRetornar404QuandoPedidoNaoForEncontradoPorNumero() throws Exception {
        // Arrange
        Integer numeroPedido = 999;
        when(buscarPedidoPorNumeroUseCase.executar(numeroPedido))
                .thenThrow(new IllegalArgumentException("Pedido não encontrado com número: " + numeroPedido));

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos/numero/{numeroPedido}", numeroPedido))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar pedidos por status com sucesso")
    void deveBuscarPedidosPorStatusComSucesso() throws Exception {
        // Arrange
        StatusPedido status = StatusPedido.RECEBIDO;
        List<Pedido> pedidos = List.of(pedidoMock);
        when(buscarPedidosPorStatusUseCase.executar(status))
                .thenReturn(pedidos);

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos/status/{status}", "RECEBIDO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("RECEBIDO")))
                .andExpect(jsonPath("$[0].numeroPedido", is(1)));
    }

    @Test
    @DisplayName("Deve retornar 400 para status inválido")
    void deveRetornar400ParaStatusInvalido() throws Exception {
        // Arrange
        String statusInvalido = "STATUS_INEXISTENTE";

        // Act & Assert
        mockMvc.perform(get("/api/v1/pedidos/status/{status}", statusInvalido))
                .andExpect(status().isBadRequest());
    }
}
