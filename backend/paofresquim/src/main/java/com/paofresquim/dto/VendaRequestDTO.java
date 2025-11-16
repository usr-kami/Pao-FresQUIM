package com.paofresquim.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VendaRequestDTO(
    @NotNull(message = "ID do produto é obrigatório") 
    Long idProduto,
    
    Long idCliente,
    
    @NotNull(message = "Peso vendido é obrigatório")
    @Positive(message = "Peso vendido deve ser positivo") 
    Double pesoVendido,
    
    Double precoKg,
    String formaPagamento,
    String statusPagamento
) {
    public VendaRequestDTO {
        if (formaPagamento == null) formaPagamento = "dinheiro";
        if (statusPagamento == null) statusPagamento = "pago";
    }
}