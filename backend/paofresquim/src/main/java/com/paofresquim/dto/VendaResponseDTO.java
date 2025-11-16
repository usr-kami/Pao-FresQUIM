package com.paofresquim.dto;

import java.time.LocalDateTime;

public record VendaResponseDTO(
    Long idVenda,
    Long idCliente,
    String nomeCliente,
    Long idProduto,
    String nomeProduto,
    Double pesoVendido,
    Double precoKg,
    Double total,
    String formaPagamento,
    String statusPagamento,
    LocalDateTime dataVenda,
    LocalDateTime dataVencimento
) {}