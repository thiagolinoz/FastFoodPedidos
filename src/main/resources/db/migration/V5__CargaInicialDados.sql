-- Carga Inicial de Produtos (Catálogo Simulado)
INSERT INTO tb_produtos (nm_produto, ds_descricao, vl_preco, tp_categoria, sn_ativo)
VALUES
-- LANCHES
('Hamburguer Clássico', 'Pão artesanal, hambúrguer 180g, alface, tomate e molho especial', 18.00, 'LANCHE', 1),
('X-Burguer', 'Pão, hambúrguer 180g, queijo cheddar e molho barbecue', 20.00, 'LANCHE', 1),
('X-Salada', 'Pão, hambúrguer 180g, queijo, alface, tomate e maionese', 22.00, 'LANCHE', 1),
('X-Bacon', 'Pão, hambúrguer 180g, queijo, bacon crocante, alface e molho', 25.00, 'LANCHE', 1),
('X-Tudo', 'Pão, 2 hambúrgueres, queijo, bacon, ovo, salada e molhos', 32.00, 'LANCHE', 1),
('Vegetariano', 'Pão integral, hambúrguer de grão-de-bico, queijo, salada', 24.00, 'LANCHE', 1),

-- ACOMPANHAMENTOS
('Batata Frita - Pequena', 'Batatas crocantes 200g com sal especial', 10.00, 'ACOMPANHAMENTO', 1),
('Batata Frita - Média', 'Batatas crocantes 400g com sal especial', 16.00, 'ACOMPANHAMENTO', 1),
('Batata Frita - Grande', 'Batatas crocantes 600g com sal especial', 22.00, 'ACOMPANHAMENTO', 1),
('Onion Rings', 'Anéis de cebola empanados e fritos - 300g', 18.00, 'ACOMPANHAMENTO', 1),
('Nuggets - 6 unidades', 'Nuggets de frango empanados', 14.00, 'ACOMPANHAMENTO', 1),
('Nuggets - 10 unidades', 'Nuggets de frango empanados', 20.00, 'ACOMPANHAMENTO', 1),

-- BEBIDAS
('Coca-Cola 350ml', 'Refrigerante Coca-Cola lata', 6.00, 'BEBIDA', 1),
('Guaraná Antarctica 350ml', 'Refrigerante Guaraná Antarctica lata', 6.00, 'BEBIDA', 1),
('Suco de Laranja 500ml', 'Suco natural de laranja', 8.00, 'BEBIDA', 1),
('Suco de Limão 500ml', 'Suco natural de limão', 8.00, 'BEBIDA', 1),
('Milk Shake - Morango', 'Sorvete de baunilha, leite e calda de morango', 15.00, 'BEBIDA', 1),
('Milk Shake - Chocolate', 'Sorvete de baunilha, leite e calda de chocolate', 15.00, 'BEBIDA', 1),
('Milk Shake - Doce de Leite', 'Sorvete de baunilha, leite e doce de leite', 16.00, 'BEBIDA', 1),
('Água Mineral 500ml', 'Água mineral sem gás', 4.00, 'BEBIDA', 1),

-- SOBREMESAS
('Brownie de Chocolate', 'Brownie artesanal com pedaços de chocolate', 12.00, 'SOBREMESA', 1),
('Torta de Limão', 'Fatia de torta de limão com merengue', 14.00, 'SOBREMESA', 1),
('Petit Gateau', 'Petit gateau de chocolate com sorvete de baunilha', 18.00, 'SOBREMESA', 1),
('Cookie com Gotas de Chocolate', 'Cookie artesanal com gotas de chocolate', 8.00, 'SOBREMESA', 1),
('Sundae de Chocolate', 'Sorvete de baunilha com calda de chocolate', 10.00, 'SOBREMESA', 1),
('Sundae de Morango', 'Sorvete de baunilha com calda de morango', 10.00, 'SOBREMESA', 1);
