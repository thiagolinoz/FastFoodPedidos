package br.com.fiap.postechfasfood.domain.entities;

import java.util.Objects;


public class ItemPedido {
    
    private String nomeProduto;
    private String codigoProduto;
    private int quantidade;
    private double precoUnitario;
    
    public ItemPedido(String nomeProduto, String codigoProduto, int quantidade, double precoUnitario) {
        if (nomeProduto == null || nomeProduto.isBlank()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (precoUnitario < 0) {
            throw new IllegalArgumentException("Preço unitário não pode ser negativo");
        }
        
        this.nomeProduto = nomeProduto;
        this.codigoProduto = codigoProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public double calcularSubtotal() {
        return quantidade * precoUnitario;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }
    
    public String getCodigoProduto() {
        return codigoProduto;
    }
    
    public int getQuantidade() {
        return quantidade;
    }
    
    public double getPrecoUnitario() {
        return precoUnitario;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemPedido that = (ItemPedido) o;
        return Objects.equals(codigoProduto, that.codigoProduto);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(codigoProduto);
    }
    
    @Override
    public String toString() {
        return "ItemPedido{" +
                "nomeProduto='" + nomeProduto + '\'' +
                ", quantidade=" + quantidade +
                ", precoUnitario=" + precoUnitario +
                '}';
    }
}
