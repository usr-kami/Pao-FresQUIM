package com.paofresquim.dto;

public record ClienteTopDTO(
    Long idCliente,
    String nomeCliente,
    Double totalCompras,
    Integer numeroCompras,
    Double ticketMedio
) {}