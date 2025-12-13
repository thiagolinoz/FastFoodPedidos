package br.com.fiap.postechfasfood.domain.valueobjects;

public enum StatusPedido {
    AGUARDANDO_PAGAMENTO("Aguardando Pagamento"),
    RECEBIDO("Recebido"),
    EM_PREPARACAO("Em Preparação"),
    PRONTO("Pronto"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");
    
    private final String descricao;
    
    StatusPedido(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }

    public boolean podeTransicionarPara(StatusPedido novoStatus) {
        return switch (this) {
            case AGUARDANDO_PAGAMENTO -> novoStatus == RECEBIDO || novoStatus == CANCELADO;
            case RECEBIDO -> novoStatus == EM_PREPARACAO || novoStatus == CANCELADO;
            case EM_PREPARACAO -> novoStatus == PRONTO || novoStatus == CANCELADO;
            case PRONTO -> novoStatus == FINALIZADO;
            case FINALIZADO, CANCELADO -> false;
        };
    }
    
    public static StatusPedido fromString(String status) {
        for (StatusPedido s : StatusPedido.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + status);
    }
}
