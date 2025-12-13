package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.ItemPedido;
import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.entities.Produto;
import br.com.fiap.postechfasfood.domain.ports.input.CadastrarPedidoUseCase;
import br.com.fiap.postechfasfood.domain.ports.output.CatalogoServicePort;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.CategoriaProduto;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
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
    
    private CadastrarPedidoUseCase useCase;
    
    @BeforeEach
    void setUp() {
        useCase = new CadastrarPedidoUseCaseImpl(pedidoRepository, catalogoService);
    }
    
    @Test
    @DisplayName("Deve criar pedido com sucesso usando nome do produto")
    void deveCriarPedidoComSucesso() {
        // Arrange
        String nomeProduto = "X-Burger";
        Produto produto = new Produto(
            "prod-123",
            nomeProduto,
            "Hambúrguer delicioso",
            25.90,
            true,
            CategoriaProduto.LANCHE
        );
        
        // Mock do catálogo: simula busca por NOME
        when(catalogoService.buscarProdutoPorNome(nomeProduto))
            .thenReturn(Optional.of(produto));
        
        // Mock do repositório
        when(pedidoRepository.gerarProximoNumeroPedido()).thenReturn(1);
        when(pedidoRepository.salvar(any(Pedido.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Request
        CadastrarPedidoUseCase.CadastrarPedidoRequest request = 
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                "12345678900",
                List.of(new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest(
                    nomeProduto, 2
                ))
            );
        
        // Act
        Pedido pedido = useCase.executar(request);
        
        // Assert
        assertNotNull(pedido);
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedido.getStatus());
        assertEquals(1, pedido.getNumeroPedido());
        assertEquals("12345678900", pedido.getDocumentoCliente());
        assertEquals(1, pedido.getItens().size());
        
        ItemPedido item = pedido.getItens().get(0);
        assertEquals(nomeProduto, item.getNomeProduto());
        assertEquals(2, item.getQuantidade());
        assertEquals(25.90, item.getPrecoUnitario());
        
        // Verificar interações
        verify(catalogoService, times(1)).buscarProdutoPorNome(nomeProduto);
        verify(pedidoRepository, times(1)).gerarProximoNumeroPedido();
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando produto não é encontrado")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        // Arrange
        String nomeProduto = "Produto Inexistente";
        
        // Mock: produto não encontrado
        when(catalogoService.buscarProdutoPorNome(nomeProduto))
            .thenReturn(Optional.empty());
        
        CadastrarPedidoUseCase.CadastrarPedidoRequest request = 
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                "12345678900",
                List.of(new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest(
                    nomeProduto, 1
                ))
            );
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.executar(request)
        );
        
        assertTrue(exception.getMessage().contains("Produto não encontrado"));
        
        // Não deve chamar o repositório
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando produto está inativo")
    void deveLancarExcecaoQuandoProdutoInativo() {
        // Arrange
        String nomeProduto = "Produto Inativo";
        Produto produto = new Produto(
            "prod-456",
            nomeProduto,
            "Produto desativado",
            30.00,
            false,  // INATIVO
            CategoriaProduto.LANCHE
        );
        
        when(catalogoService.buscarProdutoPorNome(nomeProduto))
            .thenReturn(Optional.of(produto));
        
        CadastrarPedidoUseCase.CadastrarPedidoRequest request = 
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                "12345678900",
                List.of(new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest(
                    nomeProduto, 1
                ))
            );
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.executar(request)
        );
        
        assertTrue(exception.getMessage().contains("não está disponível"));
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
        verify(catalogoService, never()).buscarProdutoPorNome(anyString());
        verify(pedidoRepository, never()).salvar(any(Pedido.class));
    }
    
    @Test
    @DisplayName("Deve calcular valor total corretamente")
    void deveCalcularValorTotalCorretamente() {
        // Arrange
        Produto produto1 = new Produto("p1", "X-Burger", "Desc", 25.90, true, CategoriaProduto.LANCHE);
        Produto produto2 = new Produto("p2", "Coca-Cola", "Desc", 5.00, true, CategoriaProduto.BEBIDA);
        
        when(catalogoService.buscarProdutoPorNome("X-Burger")).thenReturn(Optional.of(produto1));
        when(catalogoService.buscarProdutoPorNome("Coca-Cola")).thenReturn(Optional.of(produto2));
        when(pedidoRepository.gerarProximoNumeroPedido()).thenReturn(1);
        when(pedidoRepository.salvar(any(Pedido.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        CadastrarPedidoUseCase.CadastrarPedidoRequest request = 
            new CadastrarPedidoUseCase.CadastrarPedidoRequest(
                "12345678900",
                List.of(
                    new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest("X-Burger", 2),
                    new CadastrarPedidoUseCase.CadastrarPedidoRequest.ItemPedidoRequest("Coca-Cola", 3)
                )
            );
        
        // Act
        Pedido pedido = useCase.executar(request);
        
        // Assert
        assertEquals(66.80, pedido.calcularValorTotal(), 0.01);
    }
}
