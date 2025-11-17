package com.paofresquim.dto;

public record MetricasFuncionariosDTO(
    Integer totalFuncionarios,
    Integer funcionariosAtivos,
    Integer funcionariosFerias,
    Integer expedientesHoje
) {}