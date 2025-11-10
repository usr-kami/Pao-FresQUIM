package com.paofresquim.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class VendaRequestDTO {

    @NotNull(message = "ID do produto é obrigatório")
    private Long idProduto;

    private Long idCliente;

    @NotNull(message = "Peso vendido é obrigatório")
    @Positive(message = "Peso vendido deve ser positivo")
    private Double pesoVendido;

    private Double precoKg;
    
    private String formaPagamento = "dinheiro";
    
    private String statusPagamento = "pago";

    public VendaRequestDTO() {}

    public VendaRequestDTO(Long idProduto, Long idCliente, Double pesoVendido, Double precoKg, String formaPagamento, String statusPagamento) {
        this.idProduto = idProduto;
        this.idCliente = idCliente;
        this.pesoVendido = pesoVendido;
        this.precoKg = precoKg;
        this.formaPagamento = formaPagamento;
        this.statusPagamento = statusPagamento;
    }

    public Long getIdProduto() { return idProduto; }
    public void setIdProduto(Long idProduto) { this.idProduto = idProduto; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    public Double getPesoVendido() { return pesoVendido; }
    public void setPesoVendido(Double pesoVendido) { this.pesoVendido = pesoVendido; }

    public Double getPrecoKg() { return precoKg; }
    public void setPrecoKg(Double precoKg) { this.precoKg = precoKg; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(String statusPagamento) { this.statusPagamento = statusPagamento; }
}