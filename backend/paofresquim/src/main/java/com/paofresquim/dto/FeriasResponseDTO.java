package com.paofresquim.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FeriasResponseDTO(
    Long idFerias,
    Long idFuncionario,
    String nomeFuncionario,
    String cargoFuncionario,
    LocalDate dataInicio,
    LocalDate dataFim,
    Integer diasSolicitados,
    String status,
    LocalDateTime dataSolicitacao,
    String observacoes
) {}