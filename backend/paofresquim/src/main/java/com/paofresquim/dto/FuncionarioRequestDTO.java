package com.paofresquim.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FuncionarioRequestDTO(
    @NotBlank(message = "Nome é obrigatório") 
    String nome,
    
    String telefone,
    
    @Email(message = "Email deve ser válido") 
    String email,
    
    @NotBlank(message = "Cargo é obrigatório") 
    String cargo,
    
    @NotNull(message = "Salário base é obrigatório")
    @Positive(message = "Salário base deve ser positivo") 
    Double salarioBase,
    
    Boolean ativo
) {
    public FuncionarioRequestDTO {
        if (ativo == null) ativo = true;
    }
}