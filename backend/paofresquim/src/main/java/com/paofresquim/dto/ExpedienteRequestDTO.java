package com.paofresquim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExpedienteRequestDTO(
    @NotNull(message = "ID do funcionário é obrigatório") 
    Long idFuncionario,
    
    @NotBlank(message = "Dia da semana é obrigatório") 
    String diaSemana,
    
    @NotBlank(message = "Hora de entrada é obrigatória") 
    String horaEntrada,
    
    @NotBlank(message = "Hora de saída é obrigatória") 
    String horaSaida,
    
    String turno
) {
    public ExpedienteRequestDTO {
        if (turno == null) turno = "manha";
    }
}