package br.com.fiap.postechfasfood.infrastructure.adapters.persistence;

import br.com.fiap.postechfasfood.domain.entities.Pagamento;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pagamento JDBC Adapter - Testes")
class PagamentoJdbcAdapterTest {
    
    @Mock
    private JdbcTemplate jdbcTemplate;
    
    private PagamentoJdbcAdapter adapter;
    
    @BeforeEach
    void setUp() {
        adapter = new PagamentoJdbcAdapter(jdbcTemplate);
    }
    
    @Test
    @DisplayName("Deve salvar pagamento com sucesso gerando ID automático")
    void deveSalvarPagamentoComSucesso() {
        // Arrange
        Pagamento pagamento = new Pagamento();
        pagamento.setPedidoId("pedido-123");
        pagamento.setNumeroPedido(1);
        pagamento.setStatusPagamento(StatusPagamento.APROVADO);
        pagamento.setValor(BigDecimal.valueOf(68.00));
        pagamento.setDataHoraPagamento(LocalDateTime.now());
        pagamento.setTransacaoId("MP-123456789");
        pagamento.setOrigem("MERCADO_PAGO");
        
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(1);
        
        // Act
        Pagamento resultado = adapter.salvar(pagamento);
        
        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertNotNull(resultado.getDataCriacao());
        assertNotNull(resultado.getDataAtualizacao());
        assertEquals(StatusPagamento.APROVADO, resultado.getStatusPagamento());
        
        verify(jdbcTemplate, times(1)).update(
            anyString(), 
            anyString(),
            eq("pedido-123"),
            eq(1),
            eq("APROVADO"),
            eq(BigDecimal.valueOf(68.00)),
            any(),
            eq("MP-123456789"),
            eq("MERCADO_PAGO"),
            any(),
            any()
        );
    }
    
    @Test
    @DisplayName("Deve buscar pagamento por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        // Arrange
        String id = "pag-123";
        Pagamento pagamentoEsperado = criarPagamentoMock(id, 1);
        
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(id)))
            .thenReturn(pagamentoEsperado);
        
        // Act
        Optional<Pagamento> resultado = adapter.buscarPorId(id);
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getId());
        assertEquals(StatusPagamento.APROVADO, resultado.get().getStatusPagamento());
    }
    
    @Test
    @DisplayName("Deve retornar Optional vazio quando pagamento não encontrado por ID")
    void deveRetornarVazioQuandoNaoEncontradoPorId() {
        // Arrange
        String id = "pag-inexistente";
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(id)))
            .thenThrow(new EmptyResultDataAccessException(1));
        
        // Act
        Optional<Pagamento> resultado = adapter.buscarPorId(id);
        
        // Assert
        assertFalse(resultado.isPresent());
    }
    
    @Test
    @DisplayName("Deve buscar pagamento por número do pedido com sucesso")
    void deveBuscarPorNumeroPedidoComSucesso() {
        // Arrange
        Integer numeroPedido = 1;
        Pagamento pagamentoEsperado = criarPagamentoMock("pag-123", numeroPedido);
        
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(numeroPedido)))
            .thenReturn(pagamentoEsperado);
        
        // Act
        Optional<Pagamento> resultado = adapter.buscarPorNumeroPedido(numeroPedido);
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(numeroPedido, resultado.get().getNumeroPedido());
    }
    
    @Test
    @DisplayName("Deve retornar Optional vazio quando não há pagamento para o pedido")
    void deveRetornarVazioQuandoNaoHaPagamentoParaPedido() {
        // Arrange
        Integer numeroPedido = 999;
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(numeroPedido)))
            .thenThrow(new EmptyResultDataAccessException(1));
        
        // Act
        Optional<Pagamento> resultado = adapter.buscarPorNumeroPedido(numeroPedido);
        
        // Assert
        assertFalse(resultado.isPresent());
    }
    
    @Test
    @DisplayName("Deve buscar todos os pagamentos de um pedido")
    void deveBuscarPorPedidoId() {
        // Arrange
        String pedidoId = "pedido-123";
        List<Pagamento> pagamentosEsperados = List.of(
            criarPagamentoMock("pag-1", 1),
            criarPagamentoMock("pag-2", 1)
        );
        
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(pedidoId)))
            .thenReturn(pagamentosEsperados);
        
        // Act
        List<Pagamento> resultado = adapter.buscarPorPedidoId(pedidoId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }
    
    @Test
    @DisplayName("Deve listar todos os pagamentos")
    void deveListarTodosPagamentos() {
        // Arrange
        List<Pagamento> pagamentosEsperados = List.of(
            criarPagamentoMock("pag-1", 1),
            criarPagamentoMock("pag-2", 2),
            criarPagamentoMock("pag-3", 3)
        );
        
        when(jdbcTemplate.query(anyString(), any(RowMapper.class)))
            .thenReturn(pagamentosEsperados);
        
        // Act
        List<Pagamento> resultado = adapter.listarTodos();
        
        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
    }
    
    private Pagamento criarPagamentoMock(String id, Integer numeroPedido) {
        return new Pagamento(
            id,
            "pedido-" + numeroPedido,
            numeroPedido,
            StatusPagamento.APROVADO,
            BigDecimal.valueOf(68.00),
            LocalDateTime.now(),
            "MP-123456789",
            "MERCADO_PAGO",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
