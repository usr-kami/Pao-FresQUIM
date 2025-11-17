package com.paofresquim.dto;

import java.util.List;

public record AlertasEstoqueDTO(
    Integer ingredientesParaRepor,
    Integer produtosEstoqueMinimo,
    List<String> alertasCriticos
) {}