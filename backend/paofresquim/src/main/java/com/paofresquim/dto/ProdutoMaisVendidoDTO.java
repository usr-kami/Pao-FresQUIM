package com.paofresquim.dto;

public record ProdutoMaisVendidoDTO(
    Long idProduto,
    String nomeProduto,
    Double quantidadeVendida,
    Double totalVendas,
    Integer numeroVendas
) {}