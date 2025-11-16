package com.paofresquim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProdutoRequestDTO(
    @NotBlank(message = "Nome do produto é obrigatório") 
    String nomeProduto,
    
    @Positive(message = "Preço por kg deve ser positivo") 
    Double precoKg
) {}