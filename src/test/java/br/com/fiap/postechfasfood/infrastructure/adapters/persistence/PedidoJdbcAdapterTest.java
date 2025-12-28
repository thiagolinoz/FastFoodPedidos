package br.com.fiap.postechfasfood.infrastructure.adapters.persistence;

import br.com.fiap.postechfasfood.domain.entities.ItemPedido;
import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PedidoJdbcAdapter")
class PedidoJdbcAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private PedidoJdbcAdapter adapter;

    private Pedido pedidoValido;

    @BeforeEach
    void setUp() {
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));
        itens.add(new ItemPedido("Refrigerante", "PROD-002", 1, 5.00));

        pedidoValido = new Pedido.Builder()
                .id(UUID.randomUUID().toString())
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(itens)
                .build();
    }

    @Test
    @DisplayName("Deve salvar pedido com sucesso")
    void deveSalvarPedidoComSucesso() {
        // Arrange
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(1);

        // Act
        Pedido resultado = adapter.salvar(pedidoValido);

        // Assert
        assertNotNull(resultado);
        assertEquals(pedidoValido.getId(), resultado.getId());
        verify(jdbcTemplate, times(1)).update(
                contains("INSERT INTO tb_pedidos"),
                eq(pedidoValido.getId()),
                eq(pedidoValido.getDocumentoCliente()),
                eq(pedidoValido.getStatus().name()),
                eq(pedidoValido.getNumeroPedido()),
                any(),
                any(),
                any()
        );
        verify(jdbcTemplate, times(2)).update(
                contains("INSERT INTO tb_itens_pedido"),
                any(), any(), any(), any(), any()
        );
    }

    @Test
    @DisplayName("Deve atualizar pedido com sucesso")
    void deveAtualizarPedidoComSucesso() {
        // Arrange
        pedidoValido.atualizarStatus(StatusPedido.EM_PREPARACAO);
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any()))
                .thenReturn(1);

        // Act
        Pedido resultado = adapter.atualizar(pedidoValido);

        // Assert
        assertNotNull(resultado);
        assertEquals(StatusPedido.EM_PREPARACAO, resultado.getStatus());
        verify(jdbcTemplate).update(
                contains("UPDATE tb_pedidos"),
                eq(StatusPedido.EM_PREPARACAO.name()),
                any(),
                any(),
                eq(pedidoValido.getId())
        );
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void deveBuscarPedidoPorIdComSucesso() {
        // Arrange
        String pedidoId = pedidoValido.getId();
        
        // Criar pedido sem itens para simular o retorno do banco
        Pedido pedidoSemItens = new Pedido.Builder()
                .id(pedidoId)
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>())
                .build();
        
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(pedidoId)))
                .thenReturn(pedidoSemItens);
        
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(pedidoId)))
                .thenReturn(itens);

        // Act
        Optional<Pedido> resultado = adapter.buscarPorId(pedidoId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(pedidoId, resultado.get().getId());
        verify(jdbcTemplate).queryForObject(
                contains("SELECT cd_pedido"),
                any(RowMapper.class),
                eq(pedidoId)
        );
    }

    @Test
    @DisplayName("Deve retornar vazio quando pedido não encontrado por ID")
    void deveRetornarVazioQuandoPedidoNaoEncontradoPorId() {
        // Arrange
        String pedidoId = UUID.randomUUID().toString();
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(pedidoId)))
                .thenThrow(new EmptyResultDataAccessException(1));

        // Act
        Optional<Pedido> resultado = adapter.buscarPorId(pedidoId);

        // Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve buscar pedido por número com sucesso")
    void deveBuscarPedidoPorNumeroComSucesso() {
        // Arrange
        Integer numeroPedido = 1;
        String pedidoId = UUID.randomUUID().toString();
        
        Pedido pedidoSemItens = new Pedido.Builder()
                .id(pedidoId)
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(numeroPedido)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>())
                .build();
        
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(numeroPedido)))
                .thenReturn(pedidoSemItens);
        
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(pedidoId)))
                .thenReturn(itens);

        // Act
        Optional<Pedido> resultado = adapter.buscarPorNumeroPedido(numeroPedido);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(numeroPedido, resultado.get().getNumeroPedido());
    }

    @Test
    @DisplayName("Deve listar todos os pedidos")
    void deveListarTodosPedidos() {
        // Arrange
        String pedidoId = UUID.randomUUID().toString();
        Pedido pedidoSemItens = new Pedido.Builder()
                .id(pedidoId)
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>())
                .build();
        
        List<Pedido> pedidos = new ArrayList<>();
        pedidos.add(pedidoSemItens);
        
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(pedidos);
        
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString()))
                .thenReturn(itens);

        // Act
        List<Pedido> resultado = adapter.listarTodos();

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(jdbcTemplate).query(
                contains("SELECT cd_pedido"),
                any(RowMapper.class)
        );
    }

    @Test
    @DisplayName("Deve listar pedidos por status")
    void deveListarPedidosPorStatus() {
        // Arrange
        StatusPedido status = StatusPedido.RECEBIDO;
        String pedidoId = UUID.randomUUID().toString();
        
        Pedido pedidoSemItens = new Pedido.Builder()
                .id(pedidoId)
                .documentoCliente("12345678900")
                .status(status)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>())
                .build();
        
        List<Pedido> pedidos = new ArrayList<>();
        pedidos.add(pedidoSemItens);
        
        when(jdbcTemplate.query(contains("FROM tb_pedidos"), any(RowMapper.class), eq(status.name())))
                .thenReturn(pedidos);
        
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(new ItemPedido("Hamburguer", "PROD-001", 2, 15.90));
        when(jdbcTemplate.query(contains("FROM tb_itens_pedido"), any(RowMapper.class), eq(pedidoId)))
                .thenReturn(itens);

        // Act
        List<Pedido> resultado = adapter.listarPorStatus(status);

        // Assert
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(status, resultado.get(0).getStatus());
    }

    @Test
    @DisplayName("Deve gerar próximo número de pedido")
    void deveGerarProximoNumeroPedido() {
        // Arrange
        Integer proximoNumero = 42;
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class)))
                .thenReturn(proximoNumero);

        // Act
        Integer resultado = adapter.gerarProximoNumeroPedido();

        // Assert
        assertEquals(proximoNumero, resultado);
        verify(jdbcTemplate).queryForObject(
                contains("SELECT COALESCE(MAX(nr_pedido)"),
                eq(Integer.class)
        );
    }

    @Test
    @DisplayName("Deve mapear itens do pedido via RowMapper da consulta de itens")
    void deveMapearItensDoPedido() throws SQLException {
        // Arrange
        String pedidoId = UUID.randomUUID().toString();
        Pedido pedidoSemItens = new Pedido.Builder()
                .id(pedidoId)
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(1)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>())
                .build();

        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(List.of(pedidoSemItens));

        ArgumentCaptor<RowMapper<ItemPedido>> captor = ArgumentCaptor.forClass(RowMapper.class);
        when(jdbcTemplate.query(contains("FROM tb_itens_pedido"), any(RowMapper.class), eq(pedidoId)))
                .thenReturn(List.of());

        // Act
        adapter.listarTodos();

        // Assert RowMapper mapping
        verify(jdbcTemplate).query(contains("FROM tb_itens_pedido"), captor.capture(), eq(pedidoId));
        RowMapper<ItemPedido> rowMapper = captor.getValue();

        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("nm_produto")).thenReturn("Hamburguer");
        when(rs.getString("cd_produto")).thenReturn("PROD-001");
        when(rs.getInt("vl_quantidade")).thenReturn(2);
        when(rs.getDouble("vl_preco_unitario")).thenReturn(15.90);

        ItemPedido item = rowMapper.mapRow(rs, 0);

        assertEquals("Hamburguer", item.getNomeProduto());
        assertEquals("PROD-001", item.getCodigoProduto());
        assertEquals(2, item.getQuantidade());
        assertEquals(15.90, item.getPrecoUnitario());
    }

    @Test
    @DisplayName("Deve mapear itens com diferentes valores numéricos")
    void deveMapearItensComValoresVariados() throws SQLException {
        // Arrange
        String pedidoId = UUID.randomUUID().toString();
        Pedido pedidoSemItens = new Pedido.Builder()
                .id(pedidoId)
                .documentoCliente("12345678900")
                .status(StatusPedido.RECEBIDO)
                .numeroPedido(99)
                .dataCriacao(LocalDateTime.now())
                .dataUltimaAtualizacao(LocalDateTime.now())
                .itens(new ArrayList<>())
                .build();

        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
                .thenReturn(List.of(pedidoSemItens));

        ArgumentCaptor<RowMapper<ItemPedido>> captor = ArgumentCaptor.forClass(RowMapper.class);
        when(jdbcTemplate.query(contains("FROM tb_itens_pedido"), any(RowMapper.class), eq(pedidoId)))
                .thenReturn(List.of());

        // Act
        adapter.listarTodos();

        verify(jdbcTemplate).query(contains("FROM tb_itens_pedido"), captor.capture(), eq(pedidoId));
        RowMapper<ItemPedido> rowMapper = captor.getValue();

        ResultSet rs = mock(ResultSet.class);

        when(rs.getString("nm_produto")).thenReturn("Combo Grande");
        when(rs.getString("cd_produto")).thenReturn("PROD-XYZ");
        when(rs.getInt("vl_quantidade")).thenReturn(5);
        when(rs.getDouble("vl_preco_unitario")).thenReturn(49.99);
        ItemPedido item1 = rowMapper.mapRow(rs, 0);
        assertEquals(5, item1.getQuantidade());
        assertEquals(49.99, item1.getPrecoUnitario());

        when(rs.getInt("vl_quantidade")).thenReturn(1);
        when(rs.getDouble("vl_preco_unitario")).thenReturn(0.01);
        ItemPedido item2 = rowMapper.mapRow(rs, 1);
        assertEquals(1, item2.getQuantidade());
        assertEquals(0.01, item2.getPrecoUnitario());
    }
}
