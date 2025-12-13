-- Tabela de Pedidos - Gestão de Pedidos (Hexagonal Architecture)
CREATE TABLE tb_pedidos (
    cd_pedido VARCHAR(36) NOT NULL,
    tx_documento_cliente VARCHAR(50) NULL,
    tx_status VARCHAR(50) NOT NULL, -- AGUARDANDO_PAGAMENTO, RECEBIDO, EM_PREPARACAO, PRONTO, FINALIZADO, CANCELADO
    nr_pedido INT NOT NULL DEFAULT 0,
    vl_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    dh_criacao_pedido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dh_ult_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (cd_pedido),
    UNIQUE KEY UK_PEDIDO_NUMERO (nr_pedido),
    INDEX IDX_PEDIDO_STATUS (tx_status),
    INDEX IDX_PEDIDO_CRIACAO (dh_criacao_pedido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;