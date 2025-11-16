package com.paofresquim.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record FeriasRequestDTO(
    @NotNull(message = "ID do funcionário é obrigatório") 
    Long idFuncionario,
    
    @NotNull(message = "Data de início é obrigatória") 
    LocalDate dataInicio,
    
    @NotNull(message = "Data de fim é obrigatória") 
    LocalDate dataFim,
    
    String observacoes
) {}