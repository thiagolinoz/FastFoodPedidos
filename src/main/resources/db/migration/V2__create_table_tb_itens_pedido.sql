CREATE TABLE tb_itens_pedido (
    id BIGINT AUTO_INCREMENT,
    cd_pedido VARCHAR(36) NOT NULL,
    nm_produto VARCHAR(255) NOT NULL,
    cd_produto VARCHAR(50) NOT NULL,
    vl_quantidade INT NOT NULL,
    vl_preco_unitario DECIMAL(10,2) NOT NULL,
    dh_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT FK_ITEM_PEDIDO
        FOREIGN KEY (cd_pedido)
        REFERENCES tb_pedidos(cd_pedido)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    INDEX IDX_ITEM_PEDIDO (cd_pedido),
    INDEX IDX_ITEM_PRODUTO (cd_produto)
) ENGINE=InnoDB;

