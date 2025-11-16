package com.paofresquim.dto;

public record ProdutoResponseDTO(
    Long idProduto,
    String nomeProduto,
    Double precoKg
) {}