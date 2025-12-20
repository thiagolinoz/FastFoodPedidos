package br.com.fiap.postechfasfood.domain.entities;

import br.com.fiap.postechfasfood.domain.valueobjects.StatusPedido;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Pedido {
    
    private String id;
    private String documentoCliente;
    private StatusPedido status;
    private Integer numeroPedido;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataUltimaAtualizacao;
    private List<ItemPedido> itens;

    private Pedido() {
        this.itens = new ArrayList<>();
    }

    public Pedido(String id, 
                  String documentoCliente, 
                  StatusPedido status, 
                  Integer numeroPedido,
                  LocalDateTime dataCriacao, 
                  LocalDateTime dataUltimaAtualizacao,
                  List<ItemPedido> itens) {
        this.id = id;
        this.documentoCliente = documentoCliente;
        this.status = status;
        this.numeroPedido = numeroPedido;
        this.dataCriacao = dataCriacao;
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
        this.itens = itens != null ? new ArrayList<>(itens) : new ArrayList<>();
    }

    public void atualizarStatus(StatusPedido novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("Status não pode ser nulo");
        }

        if (!this.status.podeTransicionarPara(novoStatus)) {
            throw new IllegalStateException(
                String.format("Não é possível transicionar de %s para %s", 
                    this.status, novoStatus)
            );
        }
        
        this.status = novoStatus;
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
    
    public void adicionarItem(ItemPedido item) {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }
        this.itens.add(item);
    }
    
    public double calcularValorTotal() {
        return itens.stream()
            .mapToDouble(ItemPedido::calcularSubtotal)
            .sum();
    }
    
    public boolean isPago() {
        return status == StatusPedido.RECEBIDO || 
               status == StatusPedido.EM_PREPARACAO || 
               status == StatusPedido.PRONTO ||
               status == StatusPedido.FINALIZADO;
    }
    
    public boolean isAguardandoPagamento() {
        return status == StatusPedido.AGUARDANDO_PAGAMENTO;
    }

    public String getId() {
        return id;
    }
    
    public String getDocumentoCliente() {
        return documentoCliente;
    }
    
    public StatusPedido getStatus() {
        return status;
    }
    
    public Integer getNumeroPedido() {
        return numeroPedido;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }
    
    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public static class Builder {
        private String id;
        private String documentoCliente;
        private StatusPedido status;
        private Integer numeroPedido;
        private LocalDateTime dataCriacao;
        private LocalDateTime dataUltimaAtualizacao;
        private List<ItemPedido> itens;
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder documentoCliente(String documentoCliente) {
            this.documentoCliente = documentoCliente;
            return this;
        }
        
        public Builder status(StatusPedido status) {
            this.status = status;
            return this;
        }
        
        public Builder numeroPedido(Integer numeroPedido) {
            this.numeroPedido = numeroPedido;
            return this;
        }
        
        public Builder dataCriacao(LocalDateTime dataCriacao) {
            this.dataCriacao = dataCriacao;
            return this;
        }
        
        public Builder dataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
            this.dataUltimaAtualizacao = dataUltimaAtualizacao;
            return this;
        }
        
        public Builder itens(List<ItemPedido> itens) {
            this.itens = itens;
            return this;
        }
        
        public Pedido build() {
            Objects.requireNonNull(id, "ID é obrigatório");
            Objects.requireNonNull(status, "Status é obrigatório");
            Objects.requireNonNull(numeroPedido, "Número do pedido é obrigatório");
            
            return new Pedido(id, documentoCliente, status, numeroPedido, 
                            dataCriacao, dataUltimaAtualizacao, itens);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Pedido{" +
                "id='" + id + '\'' +
                ", numeroPedido=" + numeroPedido +
                ", status=" + status +
                ", dataCriacao=" + dataCriacao +
                '}';
    }
}
