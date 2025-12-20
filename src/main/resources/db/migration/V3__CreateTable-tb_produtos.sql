-- Tabela de Produtos - Catálogo (Simulação de Microserviço Externo)
CREATE TABLE tb_produtos (
    cd_produto VARCHAR(36) NOT NULL,
    nm_produto VARCHAR(150) NOT NULL,
    ds_descricao TEXT NOT NULL,
    vl_preco DECIMAL(10,2) NOT NULL,
    tp_categoria VARCHAR(50) NOT NULL, -- LANCHE, ACOMPANHAMENTO, BEBIDA, SOBREMESA
    sn_ativo TINYINT(1) NOT NULL DEFAULT 1,
    dh_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dh_ult_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (cd_produto),
    UNIQUE KEY UK_PRODUTO_NOME (nm_produto),
    INDEX IDX_PRODUTO_CATEGORIA (tp_categoria),
    INDEX IDX_PRODUTO_ATIVO (sn_ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Trigger para gerar UUID automaticamente
CREATE TRIGGER before_insert_tb_produtos
BEFORE INSERT ON tb_produtos
FOR EACH ROW
SET NEW.cd_produto = IF(NEW.cd_produto IS NULL OR NEW.cd_produto = '', UUID(), NEW.cd_produto);
