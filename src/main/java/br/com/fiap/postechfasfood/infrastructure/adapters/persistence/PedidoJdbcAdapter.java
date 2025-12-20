package br.com.fiap.postechfasfood.infrastructure.adapters.persistence;

import br.com.fiap.postechfasfood.domain.entities.ItemPedido;
import br.com.fiap.postechfasfood.domain.entities.Pedido;
import br.com.fiap.postechfasfood.domain.ports.output.PedidoRepositoryPort;
import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PedidoJdbcAdapter implements PedidoRepositoryPort {
    
    private final JdbcTemplate jdbcTemplate;
    
    public PedidoJdbcAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public Pedido salvar(Pedido pedido) {
        String sqlPedido = """
            INSERT INTO tb_pedidos 
            (cd_pedido, tx_documento_cliente, tx_status, nr_pedido, vl_total, dh_criacao_pedido, dh_ult_atualizacao) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        jdbcTemplate.update(sqlPedido,
            pedido.getId(),
            pedido.getDocumentoCliente(),
            pedido.getStatus().name(),
            pedido.getNumeroPedido(),
            pedido.calcularValorTotal(),
            Timestamp.valueOf(pedido.getDataCriacao()),
            Timestamp.valueOf(pedido.getDataUltimaAtualizacao())
        );

        String sqlItem = """
            INSERT INTO tb_itens_pedido 
            (cd_pedido, nm_produto, cd_produto, vl_quantidade, vl_preco_unitario) 
            VALUES (?, ?, ?, ?, ?)
        """;
        
        for (ItemPedido item : pedido.getItens()) {
            jdbcTemplate.update(sqlItem,
                pedido.getId(),
                item.getNomeProduto(),
                item.getCodigoProduto(),
                item.getQuantidade(),
                item.getPrecoUnitario()
            );
        }
        
        return pedido;
    }
    
    @Override
    public Pedido atualizar(Pedido pedido) {
        String sql = """
            UPDATE tb_pedidos 
            SET tx_status = ?,
                vl_total = ?,
                dh_ult_atualizacao = ? 
            WHERE cd_pedido = ?
        """;
        
        jdbcTemplate.update(sql,
            pedido.getStatus().name(),
            pedido.calcularValorTotal(),
            Timestamp.valueOf(pedido.getDataUltimaAtualizacao()),
            pedido.getId()
        );
        
        return pedido;
    }
    
    @Override
    public Optional<Pedido> buscarPorId(String id) {
        String sql = """
            SELECT cd_pedido, tx_documento_cliente, tx_status, nr_pedido, vl_total,
                   dh_criacao_pedido, dh_ult_atualizacao 
            FROM tb_pedidos 
            WHERE cd_pedido = ?
        """;
        
        try {
            Pedido pedido = jdbcTemplate.queryForObject(sql, new PedidoRowMapper(), id);
            if (pedido != null) {
                carregarItens(pedido);
            }
            return Optional.ofNullable(pedido);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Pedido> buscarPorNumeroPedido(Integer numeroPedido) {
        String sql = """
            SELECT cd_pedido, tx_documento_cliente, tx_status, nr_pedido, vl_total,
                   dh_criacao_pedido, dh_ult_atualizacao 
            FROM tb_pedidos 
            WHERE nr_pedido = ?
        """;
        
        try {
            Pedido pedido = jdbcTemplate.queryForObject(sql, new PedidoRowMapper(), numeroPedido);
            if (pedido != null) {
                carregarItens(pedido);
            }
            return Optional.ofNullable(pedido);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Pedido> listarTodos() {
        String sql = """
            SELECT cd_pedido, tx_documento_cliente, tx_status, nr_pedido, vl_total,
                   dh_criacao_pedido, dh_ult_atualizacao 
            FROM tb_pedidos 
            ORDER BY dh_criacao_pedido DESC
        """;
        
        List<Pedido> pedidos = jdbcTemplate.query(sql, new PedidoRowMapper());
        pedidos.forEach(this::carregarItens);
        return pedidos;
    }
    
    @Override
    public List<Pedido> listarPorStatus(StatusPedido status) {
        String sql = """
            SELECT cd_pedido, tx_documento_cliente, tx_status, nr_pedido, vl_total,
                   dh_criacao_pedido, dh_ult_atualizacao 
            FROM tb_pedidos 
            WHERE tx_status = ?
            ORDER BY dh_criacao_pedido DESC
        """;
        
        List<Pedido> pedidos = jdbcTemplate.query(sql, new PedidoRowMapper(), status.name());
        pedidos.forEach(this::carregarItens);
        return pedidos;
    }
    
    @Override
    public Integer gerarProximoNumeroPedido() {
        String sql = "SELECT COALESCE(MAX(nr_pedido), 0) + 1 FROM tb_pedidos";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
    
    private void carregarItens(Pedido pedido) {
        String sql = """
            SELECT ip.nm_produto, ip.cd_produto, ip.vl_quantidade, ip.vl_preco_unitario
            FROM tb_itens_pedido ip
            WHERE ip.cd_pedido = ?
        """;
        
        List<ItemPedido> itens = jdbcTemplate.query(sql, (rs, rowNum) -> 
            new ItemPedido(
                rs.getString("nm_produto"),
                rs.getString("cd_produto"),
                rs.getInt("vl_quantidade"),
                rs.getDouble("vl_preco_unitario")
            ), pedido.getId()
        );

        itens.forEach(pedido::adicionarItem);
    }

    private static class PedidoRowMapper implements RowMapper<Pedido> {
        @Override
        public Pedido mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Pedido.Builder()
                .id(rs.getString("cd_pedido"))
                .documentoCliente(rs.getString("tx_documento_cliente"))
                .status(StatusPedido.fromString(rs.getString("tx_status")))
                .numeroPedido(rs.getInt("nr_pedido"))
                .dataCriacao(rs.getTimestamp("dh_criacao_pedido").toLocalDateTime())
                .dataUltimaAtualizacao(rs.getTimestamp("dh_ult_atualizacao").toLocalDateTime())
                .itens(new ArrayList<>())
                .build();
        }
    }
}
