-- Tabela de Itens do Pedido - Gestão de Pedidos (Hexagonal Architecture)
CREATE TABLE tb_itens_pedido (
    cd_item_pedido BIGINT NOT NULL AUTO_INCREMENT,
    cd_pedido VARCHAR(36) NOT NULL,
    nm_produto VARCHAR(150) NOT NULL,
    cd_produto VARCHAR(36) NOT NULL,
    vl_quantidade INT NOT NULL DEFAULT 1,
    vl_preco_unitario DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (cd_item_pedido),
    INDEX IDX_ITEM_PEDIDO (cd_pedido),
    CONSTRAINT FK_ITEM_PEDIDO_PEDIDO
        FOREIGN KEY (cd_pedido)
        REFERENCES tb_pedidos(cd_pedido)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;