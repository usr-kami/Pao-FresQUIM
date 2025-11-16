package com.paofresquim.dto;

import java.time.LocalDateTime;

public record FuncionarioResponseDTO(
    Long idFuncionario,
    String nome,
    String telefone,
    String email,
    String cargo,
    Double salarioBase,
    LocalDateTime dataAdmissao,
    Boolean ativo
) {}