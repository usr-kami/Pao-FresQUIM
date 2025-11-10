package com.paofresquim.dto;

import java.time.LocalDateTime;

public class VendaResponseDTO {

    private Long idVenda;
    private Long idCliente;
    private String nomeCliente;
    private Long idProduto;
    private String nomeProduto;
    private Double pesoVendido;
    private Double precoKg;
    private Double total;
    private String formaPagamento;
    private String statusPagamento;
    private LocalDateTime dataVenda;
    private LocalDateTime dataVencimento;

    public VendaResponseDTO(Long idVenda, Long idCliente, String nomeCliente, Long idProduto, String nomeProduto, Double pesoVendido,
                           Double precoKg, Double total, String formaPagamento, String statusPagamento, LocalDateTime dataVenda,
                           LocalDateTime dataVencimento) {
        this.idVenda = idVenda;
        this.idCliente = idCliente;
        this.nomeCliente = nomeCliente;
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.pesoVendido = pesoVendido;
        this.precoKg = precoKg;
        this.total = total;
        this.formaPagamento = formaPagamento;
        this.statusPagamento = statusPagamento;
        this.dataVenda = dataVenda;
        this.dataVencimento = dataVencimento;
    }

    public Long getIdVenda() { return idVenda; }
    public Long getIdCliente() { return idCliente; }
    public String getNomeCliente() { return nomeCliente; }
    public Long getIdProduto() { return idProduto; }
    public String getNomeProduto() { return nomeProduto; }
    public Double getPesoVendido() { return pesoVendido; }
    public Double getPrecoKg() { return precoKg; }
    public Double getTotal() { return total; }
    public String getFormaPagamento() { return formaPagamento; }
    public String getStatusPagamento() { return statusPagamento; }
    public LocalDateTime getDataVenda() { return dataVenda; }
    public LocalDateTime getDataVencimento() { return dataVencimento; }
}