package br.com.fiap.postechfasfood.domain.valueobjects;

public enum StatusPagamento {
    PENDENTE("Pendente"),
    APROVADO("Aprovado"),
    RECUSADO("Recusado"),
    CANCELADO("Cancelado");
    
    private final String descricao;
    
    StatusPagamento(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public boolean isAprovado() {
        return this == APROVADO;
    }
    
    public boolean isRecusado() {
        return this == RECUSADO || this == CANCELADO;
    }
    
    public static StatusPagamento fromString(String status) {
        for (StatusPagamento s : StatusPagamento.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de pagamento inválido: " + status);
    }
}
