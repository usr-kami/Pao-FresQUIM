package com.paofresquim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record EstoqueIngredienteRequestDTO(
    @NotBlank(message = "Nome do ingrediente é obrigatório") 
    String nomeIngrediente,
    
    @Positive(message = "Quantidade em estoque deve ser positiva") 
    Double quantidadeEstoque,
    
    String unidadeMedida,
    Double estoqueMinimo,
    Double custoMedio
) {
    public EstoqueIngredienteRequestDTO {
        if (unidadeMedida == null) unidadeMedida = "kg";
        if (estoqueMinimo == null) estoqueMinimo = 0.0;
        if (custoMedio == null) custoMedio = 0.0;
    }
}