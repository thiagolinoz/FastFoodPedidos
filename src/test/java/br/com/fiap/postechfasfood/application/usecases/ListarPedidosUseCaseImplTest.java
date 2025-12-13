package br.com.fiap.postechfasfood.application.usecases;

import br.com.fiap.postechfasfood.domain.entities.ItemPedido;
import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ListarPedidosUseCaseImpl")
class ListarPedidosUseCaseImplTest {

    @Mock
    private PedidoRepositoryPort pedidoRepository;

    @InjectMocks
    private ListarPedidosUseCaseImpl useCase;

    private List<Pedido> pedidosMock;

    @BeforeEach
    void setUp() {
        pedidosMock = new ArrayList<>();

        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));

        Pedido pedidoRecebido1 = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now().minusMinutes(10))
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>(itens))
                .build();

        Pedido pedidoEmPreparacao = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("98765432100")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(2)
                .dataCriacao(LocalDateTime.now().minusMinutes(5))
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>(itens))
                .build();
        pedidoEmPreparacao.atualizarStatus(StatusPedido.EM_PREPARACAO);

        Pedido pedidoPronto = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("11122233344")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(3)
                .dataCriacao(LocalDateTime.now().minusMinutes(15))
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>(itens))
                .build();
        pedidoPronto.atualizarStatus(StatusPedido.EM_PREPARACAO);
        pedidoPronto.atualizarStatus(StatusPedido.PRONTO);

        Pedido pedidoRecebido2 = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("55566677788")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(4)
                .dataCriacao(LocalDateTime.now().minusMinutes(2))
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>(itens))
                .build();

        Pedido pedidoFinalizado = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("99988877766")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(5)
                .dataCriacao(LocalDateTime.now().minusMinutes(20))
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>(itens))
                .build();
        pedidoFinalizado.atualizarStatus(StatusPedido.EM_PREPARACAO);
        pedidoFinalizado.atualizarStatus(StatusPedido.PRONTO);
        pedidoFinalizado.atualizarStatus(StatusPedido.FINALIZADO);

        pedidosMock.add(pedidoRecebido1);
        pedidosMock.add(pedidoEmPreparacao);
        pedidosMock.add(pedidoPronto);
        pedidosMock.add(pedidoRecebido2);
        pedidosMock.add(pedidoFinalizado);
    }

    @Test
    @DisplayName("Deve listar pedidos na ordem correta: PRONTO > EM_PREPARACAO > RECEBIDO")
    void deveListarPedidosNaOrdemCorreta() {
        // Arrange
        when(pedidoRepository.listarTodos()).thenReturn(pedidosMock);

        // Act
        List<Pedido> resultado = useCase.executar();

        // Assert
        assertNotNull(resultado);
        assertEquals(4, resultado.size());

        assertEquals(StatusPedido.PRONTO, resultado.get(0).getStatus());
        assertEquals(StatusPedido.EM_PREPARACAO, resultado.get(1).getStatus());
        assertEquals(StatusPedido.RECEBIDO, resultado.get(2).getStatus());
        assertEquals(StatusPedido.RECEBIDO, resultado.get(3).getStatus());

        assertTrue(resultado.get(2).getDataCriacao().isBefore(resultado.get(3).getDataCriacao()));

        verify(pedidoRepository).listarTodos();
    }

    @Test
    @DisplayName("Deve excluir pedidos FINALIZADOS da listagem")
    void deveExcluirPedidosFinalizados() {
        // Arrange
        when(pedidoRepository.listarTodos()).thenReturn(pedidosMock);

        // Act
        List<Pedido> resultado = useCase.executar();

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.stream().anyMatch(p -> p.getStatus() == StatusPedido.FINALIZADO));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver pedidos")
    void deveRetornarListaVaziaQuandoNaoHouverPedidos() {
        // Arrange
        when(pedidoRepository.listarTodos()).thenReturn(List.of());

        // Act
        List<Pedido> resultado = useCase.executar();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pedidoRepository).listarTodos();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando todos os pedidos estiverem finalizados")
    void deveRetornarListaVaziaQuandoTodosFinalizados() {
        // Arrange
        List<Pedido> pedidosFinalizados = new ArrayList<>();
        
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));

        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(itens)
                .build();
        pedido.atualizarStatus(StatusPedido.EM_PREPARACAO);
        pedido.atualizarStatus(StatusPedido.PRONTO);
        pedido.atualizarStatus(StatusPedido.FINALIZADO);

        pedidosFinalizados.add(pedido);

        when(pedidoRepository.listarTodos()).thenReturn(pedidosFinalizados);

        // Act
        List<Pedido> resultado = useCase.executar();

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve ordenar pedidos do mesmo status por data de criação (mais antigos primeiro)")
    void deveOrdenarPedidosMesmoStatusPorData() {
        // Arrange
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));

        List<Pedido> pedidosRecebidos = new ArrayList<>();

        Pedido pedidoAntigo = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now().minusMinutes(30))
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>(itens))
                .build();

        Pedido pedidoRecente = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("98765432100")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(2)
                .dataCriacao(LocalDateTime.now().minusMinutes(10))
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>(itens))
                .build();

        pedidosRecebidos.add(pedidoRecente);
        pedidosRecebidos.add(pedidoAntigo);

        when(pedidoRepository.listarTodos()).thenReturn(pedidosRecebidos);

        // Act
        List<Pedido> resultado = useCase.executar();

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).getDataCriacao().isBefore(resultado.get(1).getDataCriacao()));
    }

    @Test
    @DisplayName("Deve listar apenas pedidos PRONTOS quando houver apenas esse status")
    void deveListarApenasProntos() {
        // Arrange
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));

        List<Pedido> pedidosProntos = new ArrayList<>();

        Pedido pedido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(itens)
                .build();
        pedido.atualizarStatus(StatusPedido.EM_PREPARACAO);
        pedido.atualizarStatus(StatusPedido.PRONTO);

        pedidosProntos.add(pedido);

        when(pedidoRepository.listarTodos()).thenReturn(pedidosProntos);

        // Act
        List<Pedido> resultado = useCase.executar();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals(StatusPedido.PRONTO, resultado.get(0).getStatus());
    }
}
