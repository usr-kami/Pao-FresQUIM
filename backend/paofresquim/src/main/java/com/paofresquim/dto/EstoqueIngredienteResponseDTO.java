package com.paofresquim.dto;

import java.time.LocalDateTime;

public record EstoqueIngredienteResponseDTO(
    Long idIngrediente,
    String nomeIngrediente,
    Double quantidadeEstoque,
    String unidadeMedida,
    Double estoqueMinimo,
    Double custoMedio,
    LocalDateTime dataAtualizacao,
    Boolean precisaRepor
) {}