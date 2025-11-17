package com.paofresquim.service;

import com.paofresquim.dto.*;
import com.paofresquim.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private EstoqueIngredienteRepository estoqueIngredienteRepository;

    @Autowired
    private ExpedienteRepository expedienteRepository;

    @Autowired
    private FeriasRepository feriasRepository;

    @Transactional(readOnly = true)
    public DashboardResponseDTO obterDashboard() {
        logger.info("Gerando dashboard com métricas consolidadas");
        
        return new DashboardResponseDTO(
            obterMetricasVendas(),
            obterProdutosMaisVendidos(),
            obterClientesTop(),
            obterMetricasFuncionarios(),
            obterAlertasEstoque()
        );
    }

    private MetricasVendasDTO obterMetricasVendas() {
        try {
            LocalDateTime inicioHoje = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime fimHoje = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            LocalDate primeiroDiaMes = LocalDate.now().withDayOfMonth(1);
            LocalDateTime inicioMes = LocalDateTime.of(primeiroDiaMes, LocalTime.MIN);
            LocalDateTime fimMes = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

            List<Object[]> vendasHoje = vendaRepository.findVendasResumidasPorPeriodo(inicioHoje, fimHoje);
            Double totalVendasHoje = vendasHoje.stream()
                    .mapToDouble(v -> ((Number) v[1]).doubleValue())
                    .sum();
            Integer countVendasHoje = vendasHoje.stream()
                    .mapToInt(v -> ((Number) v[2]).intValue())
                    .sum();

            List<Object[]> vendasMes = vendaRepository.findVendasResumidasPorPeriodo(inicioMes, fimMes);
            Double totalVendasMes = vendasMes.stream()
                    .mapToDouble(v -> ((Number) v[1]).doubleValue())
                    .sum();
            Integer countVendasMes = vendasMes.stream()
                    .mapToInt(v -> ((Number) v[2]).intValue())
                    .sum();

            Double ticketMedio = countVendasMes > 0 ? totalVendasMes / countVendasMes : 0.0;

            Double crescimentoPercentual = 0.0;

            return new MetricasVendasDTO(
                totalVendasHoje,
                totalVendasMes,
                countVendasHoje,
                countVendasMes,
                ticketMedio,
                crescimentoPercentual
            );
        } catch (Exception e) {
            logger.error("Erro ao calcular métricas de vendas", e);
            return new MetricasVendasDTO(0.0, 0.0, 0, 0, 0.0, 0.0);
        }
    }

    private List<ProdutoMaisVendidoDTO> obterProdutosMaisVendidos() {
        try {
            List<Object[]> produtosMaisVendidos = vendaRepository.findProdutosMaisVendidos(10);
            
            return produtosMaisVendidos.stream()
                    .map(p -> new ProdutoMaisVendidoDTO(
                        ((Number) p[0]).longValue(),
                        (String) p[1],
                        ((Number) p[2]).doubleValue(),
                        ((Number) p[3]).doubleValue(),
                        ((Number) p[4]).intValue()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erro ao obter produtos mais vendidos", e);
            return new ArrayList<>();
        }
    }

    private List<ClienteTopDTO> obterClientesTop() {
        try {
            List<Object[]> clientesTop = vendaRepository.findClientesTop(10);
            
            return clientesTop.stream()
                    .map(c -> new ClienteTopDTO(
                        ((Number) c[0]).longValue(),
                        (String) c[1],
                        ((Number) c[2]).doubleValue(),
                        ((Number) c[3]).intValue(),
                        ((Number) c[4]).doubleValue()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erro ao obter clientes top", e);
            return new ArrayList<>();
        }
    }

    private MetricasFuncionariosDTO obterMetricasFuncionarios() {
        try {
            Integer totalFuncionarios = funcionarioRepository.findAll().size();
            Integer funcionariosAtivos = funcionarioRepository.findByAtivo(true).size();
            
            Integer funcionariosFerias = feriasRepository.findByStatus("aprovado").size() + 
                                       feriasRepository.findByStatus("em_andamento").size();
            
            String diaSemanaHoje = obterDiaSemanaPortugues(LocalDate.now().getDayOfWeek().getValue());
            Integer expedientesHoje = expedienteRepository.findByDiaSemana(diaSemanaHoje).size();

            return new MetricasFuncionariosDTO(
                totalFuncionarios,
                funcionariosAtivos,
                funcionariosFerias,
                expedientesHoje
            );
        } catch (Exception e) {
            logger.error("Erro ao obter métricas de funcionários", e);
            return new MetricasFuncionariosDTO(0, 0, 0, 0);
        }
    }

    private AlertasEstoqueDTO obterAlertasEstoque() {
        try {
            Integer ingredientesParaRepor = estoqueIngredienteRepository.findByPrecisaReporTrue().size();
            
            Integer produtosEstoqueMinimo = ingredientesParaRepor;
            
            List<String> alertasCriticos = estoqueIngredienteRepository.findByPrecisaReporTrue()
                    .stream()
                    .map(e -> String.format("%s - Estoque: %.2f %s (Mínimo: %.2f)", 
                        e.getNomeIngrediente(), e.getQuantidadeEstoque(), e.getUnidadeMedida(), e.getEstoqueMinimo()))
                    .collect(Collectors.toList());

            return new AlertasEstoqueDTO(
                ingredientesParaRepor,
                produtosEstoqueMinimo,
                alertasCriticos
            );
        } catch (Exception e) {
            logger.error("Erro ao obter alertas de estoque", e);
            return new AlertasEstoqueDTO(0, 0, new ArrayList<>());
        }
    }

    private String obterDiaSemanaPortugues(int dia) {
        return switch (dia) {
            case 1 -> "segunda";
            case 2 -> "terca";
            case 3 -> "quarta";
            case 4 -> "quinta";
            case 5 -> "sexta";
            case 6 -> "sabado";
            case 7 -> "domingo";
            default -> "desconhecido";
        };
    }
}