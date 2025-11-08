package com.paofresquim.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venda")
    private Long idVenda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @NotNull(message = "Produto é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Positive(message = "Peso vendido deve ser positivo")
    @Column(name = "peso_vendido", nullable = false)
    private Double pesoVendido;

    @Positive(message = "Preço por kg deve ser positivo")
    @Column(name = "preco_kg", nullable = false)
    private Double precoKg;

    @Positive(message = "Total deve ser positivo")
    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "forma_pagamento")
    private String formaPagamento = "dinheiro";

    @Column(name = "status_pagamento")
    private String statusPagamento = "pago";

    @Column(name = "data_venda")
    private LocalDateTime dataVenda;

    @Column(name = "data_vencimento")
    private LocalDateTime dataVencimento;

    public Venda() {
        this.dataVenda = LocalDateTime.now();
    }

    public Venda(Produto produto, Double pesoVendido, Cliente cliente) {
        this();
        this.produto = produto;
        this.pesoVendido = pesoVendido;
        this.cliente = cliente;
        this.precoKg = produto.getPrecoKg();
        calcularTotal();
    }

    public void calcularTotal() {
        if (this.pesoVendido != null && this.precoKg != null) {
            this.total = this.pesoVendido * this.precoKg;
        }
    }

    public Long getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(Long idVenda) {
        this.idVenda = idVenda;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        if (produto != null && this.precoKg == null) {
            this.precoKg = produto.getPrecoKg();
            calcularTotal();
        }
    }

    public Double getPesoVendido() {
        return pesoVendido;
    }

    public void setPesoVendido(Double pesoVendido) {
        this.pesoVendido = pesoVendido;
        calcularTotal();
    }

    public Double getPrecoKg() {
        return precoKg;
    }

    public void setPrecoKg(Double precoKg) {
        this.precoKg = precoKg;
        calcularTotal();
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public LocalDateTime getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDateTime dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    @Override
    public String toString() {
        return "Venda{" +
                "idVenda=" + idVenda +
                ", cliente=" + (cliente != null ? cliente.getNome() : "N/A") +
                ", produto=" + (produto != null ? produto.getNomeProduto() : "N/A") +
                ", pesoVendido=" + pesoVendido +
                ", precoKg=" + precoKg +
                ", total=" + total +
                ", formaPagamento='" + formaPagamento + '\'' +
                ", statusPagamento='" + statusPagamento + '\'' +
                ", dataVenda=" + dataVenda +
                '}';
    }
}