package br.com.fiap.postechfasfood.infrastructure.adapters.persistence;

import br.com.fiap.postechfasfood.domain.entities.Pagamento;
import br.com.fiap.postechfasfood.domain.ports.output.PagamentoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PagamentoJdbcAdapter implements PagamentoRepositoryPort {
    
    private static final Logger logger = LoggerFactory.getLogger(PagamentoJdbcAdapter.class);
    
    private final JdbcTemplate jdbcTemplate;
    
    public PagamentoJdbcAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public Pagamento salvar(Pagamento pagamento) {
        if (pagamento.getId() == null || pagamento.getId().isEmpty()) {
            pagamento.setId(UUID.randomUUID().toString());
            pagamento.setDataCriacao(LocalDateTime.now());
        }
        pagamento.setDataAtualizacao(LocalDateTime.now());
        
        String sql = """
            INSERT INTO tb_pagamentos 
            (cd_pagamento, cd_pedido, nr_pedido, tx_status_pagamento, vl_pagamento, 
             dh_pagamento, tx_transacao_id, tx_origem, dh_criacao, dh_atualizacao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        jdbcTemplate.update(sql,
            pagamento.getId(),
            pagamento.getPedidoId(),
            pagamento.getNumeroPedido(),
            pagamento.getStatusPagamento().name(),
            pagamento.getValor(),
            Timestamp.valueOf(pagamento.getDataHoraPagamento()),
            pagamento.getTransacaoId(),
            pagamento.getOrigem(),
            Timestamp.valueOf(pagamento.getDataCriacao()),
            Timestamp.valueOf(pagamento.getDataAtualizacao())
        );
        
        logger.info("Pagamento salvo: {}", pagamento.getId());
        return pagamento;
    }
    
    @Override
    public Optional<Pagamento> buscarPorId(String id) {
        String sql = """
            SELECT cd_pagamento, cd_pedido, nr_pedido, tx_status_pagamento, vl_pagamento,
                   dh_pagamento, tx_transacao_id, tx_origem, dh_criacao, dh_atualizacao
            FROM tb_pagamentos
            WHERE cd_pagamento = ?
            """;
        
        try {
            Pagamento pagamento = jdbcTemplate.queryForObject(sql, new PagamentoRowMapper(), id);
            return Optional.ofNullable(pagamento);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Pagamento> buscarPorNumeroPedido(Integer numeroPedido) {
        String sql = """
            SELECT cd_pagamento, cd_pedido, nr_pedido, tx_status_pagamento, vl_pagamento,
                   dh_pagamento, tx_transacao_id, tx_origem, dh_criacao, dh_atualizacao
            FROM tb_pagamentos
            WHERE nr_pedido = ?
            ORDER BY dh_criacao DESC
            LIMIT 1
            """;
        
        try {
            Pagamento pagamento = jdbcTemplate.queryForObject(sql, new PagamentoRowMapper(), numeroPedido);
            return Optional.ofNullable(pagamento);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Pagamento> buscarPorPedidoId(String pedidoId) {
        String sql = """
            SELECT cd_pagamento, cd_pedido, nr_pedido, tx_status_pagamento, vl_pagamento,
                   dh_pagamento, tx_transacao_id, tx_origem, dh_criacao, dh_atualizacao
            FROM tb_pagamentos
            WHERE cd_pedido = ?
            ORDER BY dh_criacao DESC
            """;
        
        return jdbcTemplate.query(sql, new PagamentoRowMapper(), pedidoId);
    }
    
    @Override
    public List<Pagamento> listarTodos() {
        String sql = """
            SELECT cd_pagamento, cd_pedido, nr_pedido, tx_status_pagamento, vl_pagamento,
                   dh_pagamento, tx_transacao_id, tx_origem, dh_criacao, dh_atualizacao
            FROM tb_pagamentos
            ORDER BY dh_criacao DESC
            """;
        
        return jdbcTemplate.query(sql, new PagamentoRowMapper());
    }

    private static class PagamentoRowMapper implements RowMapper<Pagamento> {
        @Override
        public Pagamento mapRow(ResultSet rs, int rowNum) throws SQLException {
            Pagamento pagamento = new Pagamento();
            pagamento.setId(rs.getString("cd_pagamento"));
            pagamento.setPedidoId(rs.getString("cd_pedido"));
            pagamento.setNumeroPedido(rs.getInt("nr_pedido"));
            pagamento.setStatusPagamento(StatusPagamento.valueOf(rs.getString("tx_status_pagamento")));
            pagamento.setValor(rs.getBigDecimal("vl_pagamento"));
            pagamento.setDataHoraPagamento(rs.getTimestamp("dh_pagamento").toLocalDateTime());
            pagamento.setTransacaoId(rs.getString("tx_transacao_id"));
            pagamento.setOrigem(rs.getString("tx_origem"));
            pagamento.setDataCriacao(rs.getTimestamp("dh_criacao").toLocalDateTime());
            pagamento.setDataAtualizacao(rs.getTimestamp("dh_atualizacao").toLocalDateTime());
            return pagamento;
        }
    }
}
