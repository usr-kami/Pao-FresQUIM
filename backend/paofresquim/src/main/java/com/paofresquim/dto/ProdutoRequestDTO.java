package com.paofresquim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ProdutoRequestDTO {

    @NotBlank(message = "Nome do produto é obrigatório")
    private String nomeProduto;

    @Positive(message = "Preço por kg deve ser positivo")
    private Double precoKg;

    public ProdutoRequestDTO() {}

    public ProdutoRequestDTO(String nomeProduto, Double precoKg) {
        this.nomeProduto = nomeProduto;
        this.precoKg = precoKg;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Double getPrecoKg() {
        return precoKg;
    }

    public void setPrecoKg(Double precoKg) {
        this.precoKg = precoKg;
    }
}