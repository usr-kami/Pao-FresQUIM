package com.paofresquim.dto;

public record MetricasVendasDTO(
    Double vendasHoje,
    Double vendasMes,
    Integer totalVendasHoje,
    Integer totalVendasMes,
    Double ticketMedio,
    Double crescimentoPercentual
) {}