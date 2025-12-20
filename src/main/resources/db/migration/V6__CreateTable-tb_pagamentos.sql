-- Tabela de Pagamentos
CREATE TABLE tb_pagamentos (
    cd_pagamento VARCHAR(36) NOT NULL,
    cd_pedido VARCHAR(36) NOT NULL,
    nr_pedido INT NOT NULL,
    tx_status_pagamento VARCHAR(50) NOT NULL, -- APROVADO, RECUSADO, PENDENTE
    vl_pagamento DECIMAL(10,2) NOT NULL,
    dh_pagamento TIMESTAMP NOT NULL,
    tx_transacao_id VARCHAR(100) NULL,
    tx_origem VARCHAR(50) NULL, -- MERCADO_PAGO, PIX, OUTRO
    dh_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dh_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (cd_pagamento),
    FOREIGN KEY (cd_pedido) REFERENCES tb_pedidos(cd_pedido),
    INDEX IDX_PAGAMENTO_PEDIDO (cd_pedido),
    INDEX IDX_PAGAMENTO_NR_PEDIDO (nr_pedido),
    INDEX IDX_PAGAMENTO_STATUS (tx_status_pagamento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
