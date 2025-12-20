package br.com.fiap.postechfasfood.domain.entities;

import br.com.fiap.postechfasfood.domain.valueobjects.CategoriaProduto;

import java.util.Objects;

public class Produto {
    
    private String id;
    private String nome;
    private String descricao;
    private double preco;
    private boolean ativo;
    private CategoriaProduto categoria;
    
    public Produto(String id, String nome, String descricao, double preco, 
                   boolean ativo, CategoriaProduto categoria) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        if (preco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }
        
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.ativo = ativo;
        this.categoria = categoria;
    }

    public void ativar() {
        this.ativo = true;
    }
    
    public void desativar() {
        this.ativo = false;
    }
    
    public void atualizarPreco(double novoPreco) {
        if (novoPreco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        this.preco = novoPreco;
    }

    public String getId() {
        return id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public double getPreco() {
        return preco;
    }
    
    public boolean isAtivo() {
        return ativo;
    }
    
    public CategoriaProduto getCategoria() {
        return categoria;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(id, produto.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Produto{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                ", categoria=" + categoria +
                '}';
    }
}
