package com.paofresquim.dto;

import java.util.List;

public record DashboardResponseDTO(
    MetricasVendasDTO metricasVendas,
    List<ProdutoMaisVendidoDTO> produtosMaisVendidos,
    List<ClienteTopDTO> clientesTop,
    MetricasFuncionariosDTO metricasFuncionarios,
    AlertasEstoqueDTO alertasEstoque
) {}