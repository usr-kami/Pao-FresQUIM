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
    public ResponseEntity<?> obterDashboard() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Solicitando dados do dashboard");
            DashboardResponseDTO dashboard = dashboardService.obterDashboard();
            logger.info("Dashboard gerado com sucesso");
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("Erro ao obter dashboard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro interno ao gerar dashboard: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/vendas-hoje")
    public ResponseEntity<?> obterVendasHoje() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Calculando vendas de hoje");
            DashboardResponseDTO dashboard = dashboardService.obterDashboard();
            Double vendasHoje = dashboard.metricasVendas().vendasHoje();
            logger.info("Vendas de hoje: R$ {}", vendasHoje);
            return ResponseEntity.ok(vendasHoje);
        } catch (Exception e) {
            logger.error("Erro ao obter vendas de hoje: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro ao calcular vendas de hoje: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/vendas-mes")
    public ResponseEntity<?> obterVendasMes() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Calculando vendas do mês");
            DashboardResponseDTO dashboard = dashboardService.obterDashboard();
            Double vendasMes = dashboard.metricasVendas().vendasMes();
            logger.info("Vendas do mês: R$ {}", vendasMes);
            return ResponseEntity.ok(vendasMes);
        } catch (Exception e) {
            logger.error("Erro ao obter vendas do mês: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro ao calcular vendas do mês: " + e.getMessage());
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
        } catch (Exception e) {
            logger.error("Erro ao obter produtos mais vendidos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro ao buscar produtos mais vendidos: " + e.getMessage());
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
        } catch (Exception e) {
            logger.error("Erro ao obter clientes top: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro ao buscar clientes top: " + e.getMessage());
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
        } catch (Exception e) {
            logger.error("Erro ao obter métricas de funcionários: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro ao buscar métricas de funcionários: " + e.getMessage());
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
        } catch (Exception e) {
            logger.error("Erro ao obter alertas de estoque: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Erro ao buscar alertas de estoque: " + e.getMessage());
        } finally {
            MDC.clear();
        }
    }
}