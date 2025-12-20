package br.com.fiap.postechfasfood.infrastructure.adapters.rest.dto;

public record StatusPagamentoResponse(
    Integer numeroPedido,
    String status,
    String descricao
) {}
