package br.com.fiap.postechfasfood.infrastructure.adapters.external;

import br.com.fiap.postechfasfood.domain.entities.Produto;
import br.com.fiap.postechfasfood.domain.ports.output.CatalogoServicePort;
import br.com.fiap.postechfasfood.domain.valueobjects.CategoriaProduto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


@Service
public class CatalogoServiceAdapter implements CatalogoServicePort {
    
    private static final Logger logger = LoggerFactory.getLogger(CatalogoServiceAdapter.class);
    
    private final JdbcTemplate jdbcTemplate;
    
    public CatalogoServiceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public Optional<Produto> buscarProdutoPorNome(String nomeProduto) {
        logger.info("Consultando catálogo externo para produto: {}", nomeProduto);
        
        String sql = """
            SELECT cd_produto, nm_produto, ds_descricao, vl_preco, sn_ativo, tp_categoria 
            FROM tb_produtos 
            WHERE LOWER(nm_produto) = LOWER(?)
        """;
        
        try {
            Produto produto = jdbcTemplate.queryForObject(sql, new ProdutoRowMapper(), nomeProduto);
            logger.info("Produto encontrado no catálogo: {} - ID: {}", nomeProduto, 
                produto != null ? produto.getId() : "null");
            return Optional.ofNullable(produto);
        } catch (Exception e) {
            logger.warn("Produto não encontrado no catálogo: {}", nomeProduto);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Produto> buscarProdutoPorCodigo(String codigoProduto) {
        logger.info("Consultando catálogo externo para código: {}", codigoProduto);
        
        String sql = """
            SELECT cd_produto, nm_produto, ds_descricao, vl_preco, sn_ativo, tp_categoria 
            FROM tb_produtos 
            WHERE cd_produto = ?
        """;
        
        try {
            Produto produto = jdbcTemplate.queryForObject(sql, new ProdutoRowMapper(), codigoProduto);
            return Optional.ofNullable(produto);
        } catch (Exception e) {
            logger.warn("Produto não encontrado no catálogo: {}", codigoProduto);
            return Optional.empty();
        }
    }
    
    @Override
    public boolean produtoExisteEEstaAtivo(String nomeProduto) {
        return buscarProdutoPorNome(nomeProduto)
            .map(Produto::isAtivo)
            .orElse(false);
    }

    private static class ProdutoRowMapper implements RowMapper<Produto> {
        @Override
        public Produto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Produto(
                rs.getString("cd_produto"),
                rs.getString("nm_produto"),
                rs.getString("ds_descricao"),
                rs.getDouble("vl_preco"),
                rs.getBoolean("sn_ativo"),
                CategoriaProduto.fromString(rs.getString("tp_categoria"))
            );
        }
    }
}
