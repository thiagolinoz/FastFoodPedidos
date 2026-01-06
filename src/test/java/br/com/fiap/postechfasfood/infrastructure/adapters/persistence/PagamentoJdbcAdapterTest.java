package br.com.fiap.postechfasfood.infrastructure.adapters.persistence;

import br.com.fiap.postechfasfood.domain.entities.Pagamento;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
        
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Pagamento>>any(), eq(id)))
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
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Pagamento>>any(), eq(id)))
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
        
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Pagamento>>any(), eq(numeroPedido)))
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
        when(jdbcTemplate.queryForObject(anyString(), ArgumentMatchers.<RowMapper<Pagamento>>any(), eq(numeroPedido)))
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
        
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Pagamento>>any(), eq(pedidoId)))
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
        
        when(jdbcTemplate.query(anyString(), ArgumentMatchers.<RowMapper<Pagamento>>any()))
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

    // ========== Testes para PagamentoRowMapper ==========

    @Test
    @DisplayName("Deve mapear ResultSet para Pagamento com todos os campos preenchidos")
    void deveMapearResultSetParaPagamentoComSucesso() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);
        LocalDateTime dataHora = LocalDateTime.now();
        LocalDateTime dataCriacao = LocalDateTime.now().minusHours(1);
        LocalDateTime dataAtualizacao = LocalDateTime.now();

        when(rs.getString("cd_pagamento")).thenReturn("pag-123");
        when(rs.getString("cd_pedido")).thenReturn("pedido-456");
        when(rs.getInt("nr_pedido")).thenReturn(1);
        when(rs.getString("tx_status_pagamento")).thenReturn("APROVADO");
        when(rs.getBigDecimal("vl_pagamento")).thenReturn(BigDecimal.valueOf(68.00));
        when(rs.getTimestamp("dh_pagamento")).thenReturn(Timestamp.valueOf(dataHora));
        when(rs.getString("tx_transacao_id")).thenReturn("MP-987654321");
        when(rs.getString("tx_origem")).thenReturn("MERCADO_PAGO");
        when(rs.getTimestamp("dh_criacao")).thenReturn(Timestamp.valueOf(dataCriacao));
        when(rs.getTimestamp("dh_atualizacao")).thenReturn(Timestamp.valueOf(dataAtualizacao));

        // Act
        RowMapper<Pagamento> rowMapper = obterPagamentoRowMapper();
        Pagamento resultado = rowMapper.mapRow(rs, 0);

        // Assert
        assertNotNull(resultado);
        assertEquals("pag-123", resultado.getId());
        assertEquals("pedido-456", resultado.getPedidoId());
        assertEquals(1, resultado.getNumeroPedido());
        assertEquals(StatusPagamento.APROVADO, resultado.getStatusPagamento());
        assertEquals(BigDecimal.valueOf(68.00), resultado.getValor());
        assertEquals(dataHora, resultado.getDataHoraPagamento());
        assertEquals("MP-987654321", resultado.getTransacaoId());
        assertEquals("MERCADO_PAGO", resultado.getOrigem());
        assertEquals(dataCriacao, resultado.getDataCriacao());
        assertEquals(dataAtualizacao, resultado.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve mapear todos os status de pagamento possíveis")
    void deveMapearTodosOsStatusDePagamento() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);

        // Configurar campos comuns
        when(rs.getString("cd_pagamento")).thenReturn("pag-123");
        when(rs.getString("cd_pedido")).thenReturn("pedido-456");
        when(rs.getInt("nr_pedido")).thenReturn(1);
        when(rs.getBigDecimal("vl_pagamento")).thenReturn(BigDecimal.valueOf(68.00));
        when(rs.getTimestamp("dh_pagamento")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getString("tx_transacao_id")).thenReturn("TX-123");
        when(rs.getString("tx_origem")).thenReturn("ORIGEM");
        when(rs.getTimestamp("dh_criacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getTimestamp("dh_atualizacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        // Act & Assert - Testar cada status
        RowMapper<Pagamento> rowMapper = obterPagamentoRowMapper();
        for (StatusPagamento status : StatusPagamento.values()) {
            when(rs.getString("tx_status_pagamento")).thenReturn(status.name());
            when(rs.getTimestamp("dh_pagamento")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
            Pagamento resultado = rowMapper.mapRow(rs, 0);
            
            assertNotNull(resultado);
            assertEquals(status, resultado.getStatusPagamento());
        }
    }

    @Test
    @DisplayName("Deve mapear BigDecimal corretamente com diferentes valores")
    void deveMapearBigDecimalCorretamente() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);
        LocalDateTime agora = LocalDateTime.now();
        BigDecimal[] valores = {
            BigDecimal.valueOf(0.01),
            BigDecimal.valueOf(100.50),
            BigDecimal.valueOf(999999.99)
        };

        // Configurar campos comuns
        when(rs.getString("cd_pagamento")).thenReturn("pag-123");
        when(rs.getString("cd_pedido")).thenReturn("pedido-456");
        when(rs.getInt("nr_pedido")).thenReturn(1);
        when(rs.getString("tx_status_pagamento")).thenReturn("APROVADO");
        when(rs.getTimestamp("dh_pagamento")).thenReturn(Timestamp.valueOf(agora));
        when(rs.getString("tx_transacao_id")).thenReturn("TX-123");
        when(rs.getString("tx_origem")).thenReturn("ORIGEM");
        when(rs.getTimestamp("dh_criacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getTimestamp("dh_atualizacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        // Act & Assert
        RowMapper<Pagamento> rowMapper = obterPagamentoRowMapper();
        for (BigDecimal valor : valores) {
            when(rs.getBigDecimal("vl_pagamento")).thenReturn(valor);
            when(rs.getTimestamp("dh_pagamento")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
            Pagamento resultado = rowMapper.mapRow(rs, 0);
            
            assertNotNull(resultado);
            assertEquals(valor, resultado.getValor());
        }
    }

    @Test
    @DisplayName("Deve mapear diferentes números de pedido")
    void deveMapearDiferentesNumeroPedido() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);
        LocalDateTime agora = LocalDateTime.now();
        int[] numeros = { 1, 100, 999, 999999 };

        // Configurar campos comuns
        when(rs.getString("cd_pagamento")).thenReturn("pag-123");
        when(rs.getString("cd_pedido")).thenReturn("pedido-456");
        when(rs.getString("tx_status_pagamento")).thenReturn("APROVADO");
        when(rs.getBigDecimal("vl_pagamento")).thenReturn(BigDecimal.valueOf(68.00));
        when(rs.getTimestamp("dh_pagamento")).thenReturn(Timestamp.valueOf(agora));
        when(rs.getString("tx_transacao_id")).thenReturn("TX-123");
        when(rs.getString("tx_origem")).thenReturn("ORIGEM");
        when(rs.getTimestamp("dh_criacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getTimestamp("dh_atualizacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        // Act & Assert
        RowMapper<Pagamento> rowMapper = obterPagamentoRowMapper();
        for (int numero : numeros) {
            when(rs.getInt("nr_pedido")).thenReturn(numero);
            when(rs.getTimestamp("dh_pagamento")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
            Pagamento resultado = rowMapper.mapRow(rs, 0);
            
            assertNotNull(resultado);
            assertEquals(numero, resultado.getNumeroPedido());
        }
    }

    @Test
    @DisplayName("Deve mapear diferentes origens de pagamento")
    void deveMapearDiferentesOrigens() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);
        LocalDateTime agora = LocalDateTime.now();
        String[] origens = { "MERCADO_PAGO", "STRIPE", "PAYPAL", "CREDITO_DIRETO" };

        // Configurar campos comuns
        when(rs.getString("cd_pagamento")).thenReturn("pag-123");
        when(rs.getString("cd_pedido")).thenReturn("pedido-456");
        when(rs.getInt("nr_pedido")).thenReturn(1);
        when(rs.getString("tx_status_pagamento")).thenReturn("APROVADO");
        when(rs.getBigDecimal("vl_pagamento")).thenReturn(BigDecimal.valueOf(68.00));
        when(rs.getTimestamp("dh_pagamento")).thenReturn(Timestamp.valueOf(agora));
        when(rs.getString("tx_transacao_id")).thenReturn("TX-123");
        when(rs.getTimestamp("dh_criacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getTimestamp("dh_atualizacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        // Act & Assert
        RowMapper<Pagamento> rowMapper = obterPagamentoRowMapper();
        for (String origem : origens) {
            when(rs.getString("tx_origem")).thenReturn(origem);
            when(rs.getTimestamp("dh_pagamento")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
            Pagamento resultado = rowMapper.mapRow(rs, 0);
            
            assertNotNull(resultado);
            assertEquals(origem, resultado.getOrigem());
        }
    }

    @Test
    @DisplayName("Deve converter corretamente Timestamp para LocalDateTime")
    void deveConverterTimestampParaLocalDateTime() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);
        LocalDateTime dataEsperada = LocalDateTime.of(2025, 12, 26, 12, 30, 45);
        Timestamp timestamp = Timestamp.valueOf(dataEsperada);

        // Configurar campos
        when(rs.getString("cd_pagamento")).thenReturn("pag-123");
        when(rs.getString("cd_pedido")).thenReturn("pedido-456");
        when(rs.getInt("nr_pedido")).thenReturn(1);
        when(rs.getString("tx_status_pagamento")).thenReturn("APROVADO");
        when(rs.getBigDecimal("vl_pagamento")).thenReturn(BigDecimal.valueOf(68.00));
        when(rs.getTimestamp("dh_pagamento")).thenReturn(timestamp);
        when(rs.getString("tx_transacao_id")).thenReturn("TX-123");
        when(rs.getString("tx_origem")).thenReturn("ORIGEM");
        when(rs.getTimestamp("dh_criacao")).thenReturn(timestamp);
        when(rs.getTimestamp("dh_atualizacao")).thenReturn(timestamp);

        // Act
        RowMapper<Pagamento> rowMapper = obterPagamentoRowMapper();
        Pagamento resultado = rowMapper.mapRow(rs, 0);

        // Assert
        assertNotNull(resultado);
        assertEquals(dataEsperada, resultado.getDataHoraPagamento());
        assertEquals(dataEsperada, resultado.getDataCriacao());
        assertEquals(dataEsperada, resultado.getDataAtualizacao());
    }

    @Test
    @DisplayName("Deve mapear IDs como string corretamente")
    void deveMapearIdsComoStringCorretamente() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);
        String idPagamento = "550e8400-e29b-41d4-a716-446655440000";
        String idPedido = "f47ac10b-58cc-4372-a567-0e02b2c3d479";

        when(rs.getString("cd_pagamento")).thenReturn(idPagamento);
        when(rs.getString("cd_pedido")).thenReturn(idPedido);
        when(rs.getInt("nr_pedido")).thenReturn(1);
        when(rs.getString("tx_status_pagamento")).thenReturn("APROVADO");
        when(rs.getBigDecimal("vl_pagamento")).thenReturn(BigDecimal.valueOf(68.00));
        when(rs.getTimestamp("dh_pagamento")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getString("tx_transacao_id")).thenReturn("TX-123");
        when(rs.getString("tx_origem")).thenReturn("ORIGEM");
        when(rs.getTimestamp("dh_criacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getTimestamp("dh_atualizacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        // Act
        RowMapper<Pagamento> rowMapper = obterPagamentoRowMapper();
        Pagamento resultado = rowMapper.mapRow(rs, 0);

        // Assert
        assertNotNull(resultado);
        assertEquals(idPagamento, resultado.getId());
        assertEquals(idPedido, resultado.getPedidoId());
    }

    @Test
    @DisplayName("Deve lançar SQLException quando ResultSet contém dados inválidos")
    @SuppressWarnings("resource")
    void deveLancarSQLExceptionComDadosInvalidos() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("cd_pagamento")).thenThrow(new SQLException("Erro ao ler dados"));

        // Act & Assert
        RowMapper<Pagamento> rowMapper = obterPagamentoRowMapper();
        assertThrows(SQLException.class, () -> rowMapper.mapRow(rs, 0));
    }

    @Test
    @DisplayName("Deve lançar exceção quando status de pagamento é inválido")
    @SuppressWarnings("resource")
    void deveLancarExcecaoComStatusInvalido() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);

        when(rs.getString("cd_pagamento")).thenReturn("pag-123");
        when(rs.getString("cd_pedido")).thenReturn("pedido-456");
        when(rs.getInt("nr_pedido")).thenReturn(1);
        when(rs.getString("tx_status_pagamento")).thenReturn("STATUS_INVALIDO");

        // Act & Assert
        RowMapper<Pagamento> rowMapper = obterPagamentoRowMapper();
        assertThrows(IllegalArgumentException.class, () -> rowMapper.mapRow(rs, 0));
    }

    @Test
    @DisplayName("Deve mapear string vazia e nula corretamente para IDs")
    void deveMapearStringVaziaENulaParaIds() throws SQLException {
        // Arrange
        ResultSet rs = mock(ResultSet.class);

        when(rs.getString("cd_pagamento")).thenReturn("");
        when(rs.getString("cd_pedido")).thenReturn(null);
        when(rs.getInt("nr_pedido")).thenReturn(1);
        when(rs.getString("tx_status_pagamento")).thenReturn("APROVADO");
        when(rs.getBigDecimal("vl_pagamento")).thenReturn(BigDecimal.valueOf(68.00));
        when(rs.getTimestamp("dh_pagamento")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getString("tx_transacao_id")).thenReturn("TX-123");
        when(rs.getString("tx_origem")).thenReturn("ORIGEM");
        when(rs.getTimestamp("dh_criacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(rs.getTimestamp("dh_atualizacao")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        // Act
        RowMapper<Pagamento> rowMapper = obterPagamentoRowMapper();
        Pagamento resultado = rowMapper.mapRow(rs, 0);

        // Assert
        assertNotNull(resultado);
        assertEquals("", resultado.getId());
        assertNull(resultado.getPedidoId());
    }

    private RowMapper<Pagamento> obterPagamentoRowMapper() {
        try {
            // Usar uma abordagem mais robusta para encontrar a classe RowMapper
            Class<?> pagamentoRowMapperClass = null;
            Class<?>[] declaredClasses = PagamentoJdbcAdapter.class.getDeclaredClasses();

            for (Class<?> clazz : declaredClasses) {
                // Verificar se a classe implementa RowMapper ao invés de comparar nome
                if (RowMapper.class.isAssignableFrom(clazz)) {
                    pagamentoRowMapperClass = clazz;
                    break;
                }
            }

            assertNotNull(pagamentoRowMapperClass, "PagamentoRowMapper class not found");

            Constructor<?> constructor = pagamentoRowMapperClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();

            // Converter para RowMapper
            @SuppressWarnings("unchecked")
            RowMapper<Pagamento> mapper = (RowMapper<Pagamento>) instance;
            return mapper;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter PagamentoRowMapper via reflexão", e);
        }
    }
}
