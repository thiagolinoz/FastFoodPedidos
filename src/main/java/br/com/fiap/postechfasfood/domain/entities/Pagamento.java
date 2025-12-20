package br.com.fiap.postechfasfood.domain.entities;

import br.com.fiap.postechfasfood.domain.valueobjects.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Pagamento {
    
    private String id;
    private String pedidoId;
    private Integer numeroPedido;
    private StatusPagamento statusPagamento;
    private BigDecimal valor;
    private LocalDateTime dataHoraPagamento;
    private String transacaoId;
    private String origem;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    
    public Pagamento() {
    }
    
    public Pagamento(String id, String pedidoId, Integer numeroPedido, 
                     StatusPagamento statusPagamento, BigDecimal valor, 
                     LocalDateTime dataHoraPagamento, String transacaoId, 
                     String origem, LocalDateTime dataCriacao, 
                     LocalDateTime dataAtualizacao) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.numeroPedido = numeroPedido;
        this.statusPagamento = statusPagamento;
        this.valor = valor;
        this.dataHoraPagamento = dataHoraPagamento;
        this.transacaoId = transacaoId;
        this.origem = origem;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getId() {
        return id;
    }
    
    public String getPedidoId() {
        return pedidoId;
    }
    
    public Integer getNumeroPedido() {
        return numeroPedido;
    }
    
    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public LocalDateTime getDataHoraPagamento() {
        return dataHoraPagamento;
    }
    
    public String getTransacaoId() {
        return transacaoId;
    }
    
    public String getOrigem() {
        return origem;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void setPedidoId(String pedidoId) {
        this.pedidoId = pedidoId;
    }
    
    public void setNumeroPedido(Integer numeroPedido) {
        this.numeroPedido = numeroPedido;
    }
    
    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    public void setDataHoraPagamento(LocalDateTime dataHoraPagamento) {
        this.dataHoraPagamento = dataHoraPagamento;
    }
    
    public void setTransacaoId(String transacaoId) {
        this.transacaoId = transacaoId;
    }
    
    public void setOrigem(String origem) {
        this.origem = origem;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagamento pagamento = (Pagamento) o;
        return Objects.equals(id, pagamento.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Pagamento{" +
                "id='" + id + '\'' +
                ", numeroPedido=" + numeroPedido +
                ", statusPagamento=" + statusPagamento +
                ", valor=" + valor +
                ", origem='" + origem + '\'' +
                '}';
    }
}
