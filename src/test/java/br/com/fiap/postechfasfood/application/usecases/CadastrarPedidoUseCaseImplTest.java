package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.ItemPedido;
import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.entities.Produto;
import br.com.fiap.postechfasfood.domain.exception.PessoaNaoEncontradaException;
import br.com.fiap.postechfasfood.domain.ports.input.CadastrarPedidoUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.CatalogoServicePort;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.CategoriaProduto;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import br.com.fiap.postechfasfood.infrastructure.external.service.PessoaExternaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cadastrar Pedido Use Case - Testes")
class CadastrarPedidoUseCaseImplTest {

    @Mock
    private PedidoRepositoryPort pedidoRepository;

    @Mock
    private CatalogoServicePort catalogoService;

    @Mock
    private PessoaExternaService pessoaExternaService;

    private CadastrarPedidoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CadastrarPedidoUseCaseImpl(pedidoRepository, catalogoService, pessoaExternaService);
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void deveCriarPedidoComSucesso() {
        // Arrange
        String cpf = "12345678900";
        String idProduto = "lanche-001";
        Produto produto = new Produto(
            idProduto,
            "X-Burger",
            "Hambúrguer delicioso",
            25.90,
            true,
            CategoriaProduto.LANCHE
        );

        doNothing().when(pessoaExternaService).verificarSeCpfExiste(cpf);

        when(catalogoService.buscarProdutoPorCodigo(idProduto))
            .thenReturn(Optional.of(produto));

        when(pedidoRepository.gerarProximoNumeroPedido()).thenReturn(1);
        when(pedidoRepository.salvar(any(Pedido.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        CadastrarPedidoUseCase.CadastrarPedidoRequest request =
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                cpf,
                List.of(new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest(
                    idProduto, 2
                ))
            );

        // Act
        Pedido pedido = useCase.executar(request);

        // Assert
        assertNotNull(pedido);
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedido.getStatus());
        assertEquals(1, pedido.getNumeroPedido());
        assertEquals(cpf, pedido.getDocumentoCliente());
        assertEquals(1, pedido.getItens().size());

        ItemPedido item = pedido.getItens().getFirst();
        assertEquals(cpf, pedido.getDocumentoCliente());
        assertEquals(2, item.getQuantidade());
        assertEquals(25.90, item.getPrecoUnitario());
        assertEquals("X-Burger", item.getNomeProduto());

        verify(pessoaExternaService, times(1)).verificarSeCpfExiste(cpf);
        verify(catalogoService, times(1)).buscarProdutoPorCodigo(idProduto);
        verify(pedidoRepository, times(1)).gerarProximoNumeroPedido();
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não é encontrado")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        // Arrange
        String idProduto = "produto-inexistente";
        String cpf = "12345678900";

        // Mock da validação de CPF - deve passar
        doNothing().when(pessoaExternaService).verificarSeCpfExiste(cpf);

        // Mock: produto não encontrado
        when(catalogoService.buscarProdutoPorCodigo(idProduto))
            .thenReturn(Optional.empty());

        CadastrarPedidoUseCase.CadastrarPedidoRequest request =
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                cpf,
                List.of(new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest(
                    idProduto, 1
                ))
            );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.executar(request)
        );

        assertTrue(exception.getMessage().contains("Produto não encontrado"));

        // Verificar que CPF foi validado, mas pedido não foi salvo
        verify(pessoaExternaService, times(1)).verificarSeCpfExiste(cpf);
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto está inativo")
    void deveLancarExcecaoQuandoProdutoInativo() {
        // Arrange
        String idProduto = "produto-inativo";
        String cpf = "12345678900";
        Produto produto = new Produto(
            idProduto,
            "Produto Inativo",
            "Produto desativado",
            30.00,
            false,  // INATIVO
            CategoriaProduto.LANCHE
        );

        // Mock da validação de CPF - deve passar
        doNothing().when(pessoaExternaService).verificarSeCpfExiste(cpf);

        when(catalogoService.buscarProdutoPorCodigo(idProduto))
            .thenReturn(Optional.of(produto));

        CadastrarPedidoUseCase.CadastrarPedidoRequest request =
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                cpf,
                List.of(new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest(
                    idProduto, 1
                ))
            );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.executar(request)
        );

        assertTrue(exception.getMessage().contains("não está disponível"));
        verify(pessoaExternaService, times(1)).verificarSeCpfExiste(cpf);
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando pedido não tem itens")
    void deveLancarExcecaoQuandoPedidoSemItens() {
        // Arrange
        CadastrarPedidoUseCase.CadastrarPedidoRequest request =
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                "12345678900",
                List.of()  // Lista vazia
            );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.executar(request)
        );

        assertTrue(exception.getMessage().contains("pelo menos um item"));
        verify(catalogoService, never()).buscarProdutoPorCodigo(anyString());
        verify(pessoaExternaService, never()).verificarSeCpfExiste(anyString());
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve calcular valor total corretamente")
    void deveCalcularValorTotalCorretamente() {
        // Arrange
        String cpf = "12345678900";
        Produto produto1 = new Produto("lanche-001", "X-Burger", "Desc", 25.90, true, CategoriaProduto.LANCHE);
        Produto produto2 = new Produto("bebida-001", "Coca-Cola", "Desc", 5.00, true, CategoriaProduto.BEBIDA);

        // Mock da validação de CPF
        doNothing().when(pessoaExternaService).verificarSeCpfExiste(cpf);

        when(catalogoService.buscarProdutoPorCodigo("lanche-001")).thenReturn(Optional.of(produto1));
        when(catalogoService.buscarProdutoPorCodigo("bebida-001")).thenReturn(Optional.of(produto2));
        when(pedidoRepository.gerarProximoNumeroPedido()).thenReturn(1);
        when(pedidoRepository.salvar(any(Pedido.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        CadastrarPedidoUseCase.CadastrarPedidoRequest request =
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                cpf,
                List.of(
                    new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest("lanche-001", 2),
                    new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest("bebida-001", 3)
                )
            );

        // Act
        Pedido pedido = useCase.executar(request);

        // Assert
        assertEquals(66.80, pedido.calcularValorTotal(), 0.01);
        verify(pessoaExternaService, times(1)).verificarSeCpfExiste(cpf);
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF não está cadastrado")
    void deveLancarExcecaoQuandoCpfNaoCadastrado() {
        // Arrange
        String cpfInexistente = "99999999999";

        // Mock: CPF não encontrado - lança PessoaNaoEncontradaException
        doThrow(new PessoaNaoEncontradaException("CPF " + cpfInexistente + " não encontrado"))
            .when(pessoaExternaService).verificarSeCpfExiste(cpfInexistente);

        CadastrarPedidoUseCase.CadastrarPedidoRequest request =
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                cpfInexistente,
                List.of(new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest(
                    "lanche-001", 1
                ))
            );

        // Act & Assert
        PessoaNaoEncontradaException exception = assertThrows(
            PessoaNaoEncontradaException.class,
            () -> useCase.executar(request)
        );

        assertTrue(exception.getMessage().contains("não encontrado"));
        assertTrue(exception.getMessage().contains(cpfInexistente));

        // Verificar que não tentou buscar produtos nem salvar pedido
        verify(pessoaExternaService, times(1)).verificarSeCpfExiste(cpfInexistente);
        verify(catalogoService, never()).buscarProdutoPorCodigo(anyString());
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve criar pedido anônimo quando CPF não é informado")
    void deveCriarPedidoAnonimoQuandoCpfNaoInformado() {
        // Arrange
        String idProduto = "lanche-001";
        Produto produto = new Produto(
            idProduto,
            "X-Burger",
            "Hambúrguer delicioso",
            25.90,
            true,
            CategoriaProduto.LANCHE
        );

        // Mock da validação de CPF - deve aceitar CPF nulo/vazio
        doNothing().when(pessoaExternaService).verificarSeCpfExiste(null);

        // Mock do catálogo
        when(catalogoService.buscarProdutoPorCodigo(idProduto))
            .thenReturn(Optional.of(produto));

        // Mock do repositório
        when(pedidoRepository.gerarProximoNumeroPedido()).thenReturn(1);
        when(pedidoRepository.salvar(any(Pedido.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Request com CPF null (pedido anônimo)
        CadastrarPedidoUseCase.CadastrarPedidoRequest request =
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                null, // CPF null para pedido anônimo
                List.of(new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest(
                    idProduto, 2
                ))
            );

        // Act
        Pedido pedido = useCase.executar(request);

        // Assert
        assertNotNull(pedido);
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedido.getStatus());
        assertEquals(1, pedido.getNumeroPedido());
        assertNull(pedido.getDocumentoCliente()); // CPF deve ser null para pedido anônimo
        assertEquals(1, pedido.getItens().size());

        ItemPedido item = pedido.getItens().getFirst();
        assertEquals(2, item.getQuantidade());
        assertEquals(25.90, item.getPrecoUnitario());
        assertEquals("X-Burger", item.getNomeProduto());

        verify(pessoaExternaService, times(1)).verificarSeCpfExiste(null);
        verify(catalogoService, times(1)).buscarProdutoPorCodigo(idProduto);
        verify(pedidoRepository, times(1)).gerarProximoNumeroPedido();
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve criar pedido anônimo quando CPF é string vazia")
    void deveCriarPedidoAnonimoQuandoCpfStringVazia() {
        // Arrange
        String cpfVazio = "";
        String idProduto = "lanche-001";
        Produto produto = new Produto(
            idProduto,
            "X-Burger",
            "Hambúrguer delicioso",
            25.90,
            true,
            CategoriaProduto.LANCHE
        );

        // Mock da validação de CPF - deve aceitar CPF vazio
        doNothing().when(pessoaExternaService).verificarSeCpfExiste(cpfVazio);

        when(catalogoService.buscarProdutoPorCodigo(idProduto))
            .thenReturn(Optional.of(produto));

        when(pedidoRepository.gerarProximoNumeroPedido()).thenReturn(1);
        when(pedidoRepository.salvar(any(Pedido.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Request com CPF vazio
        CadastrarPedidoUseCase.CadastrarPedidoRequest request =
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                cpfVazio,
                List.of(new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest(
                    idProduto, 1
                ))
            );

        // Act
        Pedido pedido = useCase.executar(request);

        // Assert
        assertNotNull(pedido);
        assertEquals(cpfVazio, pedido.getDocumentoCliente());
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedido.getStatus());

        verify(pessoaExternaService, times(1)).verificarSeCpfExiste(cpfVazio);
    }
}
