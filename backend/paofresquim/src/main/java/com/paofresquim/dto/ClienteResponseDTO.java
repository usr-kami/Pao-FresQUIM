package com.paofresquim.dto;

import java.time.LocalDateTime;

public record ClienteResponseDTO(
    Long idCliente,
    String nome,
    String email,
    String telefone,
    LocalDateTime dataCadastro
) {}