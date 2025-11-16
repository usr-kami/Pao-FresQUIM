package com.paofresquim.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequestDTO(
    @NotBlank(message = "Nome é obrigatório") 
    String nome,
    
    @Email(message = "Email deve ser válido") 
    String email,
    
    String telefone
) {}