package com.paofresquim.controller;

import com.paofresquim.dto.DashboardResponseDTO;
import com.paofresquim.service.DashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> obterDashboard() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Solicitando dados do dashboard");
            DashboardResponseDTO dashboard = dashboardService.obterDashboard();
            logger.info("Dashboard gerado com sucesso");
            return ResponseEntity.ok(dashboard);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/vendas-hoje")
    public ResponseEntity<Double> obterVendasHoje() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Calculando vendas de hoje");
            DashboardResponseDTO dashboard = dashboardService.obterDashboard();
            Double vendasHoje = dashboard.metricasVendas().vendasHoje();
            logger.info("Vendas de hoje: R$ {}", vendasHoje);
            return ResponseEntity.ok(vendasHoje);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/vendas-mes")
    public ResponseEntity<Double> obterVendasMes() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Calculando vendas do mês");
            DashboardResponseDTO dashboard = dashboardService.obterDashboard();
            Double vendasMes = dashboard.metricasVendas().vendasMes();
            logger.info("Vendas do mês: R$ {}", vendasMes);
            return ResponseEntity.ok(vendasMes);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<?> obterProdutosMaisVendidos() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando produtos mais vendidos");
            DashboardResponseDTO dashboard = dashboardService.obterDashboard();
            logger.info("Encontrados {} produtos mais vendidos", dashboard.produtosMaisVendidos().size());
            return ResponseEntity.ok(dashboard.produtosMaisVendidos());
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/clientes-top")
    public ResponseEntity<?> obterClientesTop() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando clientes top");
            DashboardResponseDTO dashboard = dashboardService.obterDashboard();
            logger.info("Encontrados {} clientes top", dashboard.clientesTop().size());
            return ResponseEntity.ok(dashboard.clientesTop());
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/metricas-funcionarios")
    public ResponseEntity<?> obterMetricasFuncionarios() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando métricas de funcionários");
            DashboardResponseDTO dashboard = dashboardService.obterDashboard();
            return ResponseEntity.ok(dashboard.metricasFuncionarios());
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/alertas-estoque")
    public ResponseEntity<?> obterAlertasEstoque() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando alertas de estoque");
            DashboardResponseDTO dashboard = dashboardService.obterDashboard();
            logger.info("Encontrados {} alertas de estoque", dashboard.alertasEstoque().ingredientesParaRepor());
            return ResponseEntity.ok(dashboard.alertasEstoque());
        } finally {
            MDC.clear();
        }
    }
}