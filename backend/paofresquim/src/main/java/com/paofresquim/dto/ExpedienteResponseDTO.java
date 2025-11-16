package com.paofresquim.dto;

public record ExpedienteResponseDTO(
    Long idExpediente,
    Long idFuncionario,
    String nomeFuncionario,
    String cargoFuncionario,
    String diaSemana,
    String horaEntrada,
    String horaSaida,
    String turno
) {}