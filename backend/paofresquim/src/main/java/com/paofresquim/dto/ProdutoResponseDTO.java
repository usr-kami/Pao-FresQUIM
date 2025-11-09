package com.paofresquim.dto;

public class ProdutoResponseDTO {

    private Long idProduto;
    private String nomeProduto;
    private Double precoKg;

    public ProdutoResponseDTO(Long idProduto, String nomeProduto, Double precoKg) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.precoKg = precoKg;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Double getPrecoKg() {
        return precoKg;
    }
}