package com.paofresquim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class EstoqueIngredienteRequestDTO {

    @NotBlank(message = "Nome do ingrediente é obrigatório")
    private String nomeIngrediente;

    @Positive(message = "Quantidade em estoque deve ser positiva")
    private Double quantidadeEstoque;

    private String unidadeMedida = "kg";

    private Double estoqueMinimo = 0.0;

    private Double custoMedio = 0.0;

    public EstoqueIngredienteRequestDTO() {}

    public EstoqueIngredienteRequestDTO(String nomeIngrediente, Double quantidadeEstoque, String unidadeMedida, Double estoqueMinimo, Double custoMedio) {
        this.nomeIngrediente = nomeIngrediente;
        this.quantidadeEstoque = quantidadeEstoque;
        this.unidadeMedida = unidadeMedida;
        this.estoqueMinimo = estoqueMinimo;
        this.custoMedio = custoMedio;
    }

    public String getNomeIngrediente() { return nomeIngrediente; }
    public void setNomeIngrediente(String nomeIngrediente) { this.nomeIngrediente = nomeIngrediente; }

    public Double getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(Double quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public Double getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Double estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public Double getCustoMedio() { return custoMedio; }
    public void setCustoMedio(Double custoMedio) { this.custoMedio = custoMedio; }
}