package br.com.fiap.postechfasfood.domain.valueobjects;

public enum CategoriaProduto {
    LANCHE("Lanche"),
    ACOMPANHAMENTO("Acompanhamento"),
    BEBIDA("Bebida"),
    SOBREMESA("Sobremesa");
    
    private final String descricao;
    
    CategoriaProduto(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public static CategoriaProduto fromString(String categoria) {
        for (CategoriaProduto c : CategoriaProduto.values()) {
            if (c.name().equalsIgnoreCase(categoria)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Categoria inválida: " + categoria);
    }
}
