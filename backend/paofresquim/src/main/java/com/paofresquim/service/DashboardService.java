package com.paofresquim.service;

import com.paofresquim.dto.*;
import com.paofresquim.entity.Venda;
import com.paofresquim.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public DashboardResponseDTO obterDashboard() {
        logger.info("Gerando dashboard com métricas consolidadas");
        
        try {
            MetricasVendasDTO metricasVendas = obterMetricasVendas();
            List<ProdutoMaisVendidoDTO> produtosMaisVendidos = obterProdutosMaisVendidos();
            List<ClienteTopDTO> clientesTop = obterClientesTop();
            MetricasFuncionariosDTO metricasFuncionarios = obterMetricasFuncionarios();
            AlertasEstoqueDTO alertasEstoque = obterAlertasEstoque();

            return new DashboardResponseDTO(
                metricasVendas,
                produtosMaisVendidos,
                clientesTop,
                metricasFuncionarios,
                alertasEstoque
            );
        } catch (Exception e) {
            logger.error("Erro ao gerar dashboard completo", e);
            return criarDashboardComValoresPadrao();
        }
    }

    private DashboardResponseDTO criarDashboardComValoresPadrao() {
        return new DashboardResponseDTO(
            new MetricasVendasDTO(0.0, 0.0, 0, 0, 0.0, 0.0),
            new ArrayList<>(),
            new ArrayList<>(),
            new MetricasFuncionariosDTO(0, 0, 0, 0),
            new AlertasEstoqueDTO(0, 0, new ArrayList<>())
        );
    }

    private MetricasVendasDTO obterMetricasVendas() {
        try {
            LocalDateTime inicioHoje = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime fimHoje = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            
            LocalDate primeiroDiaMes = LocalDate.now().withDayOfMonth(1);
            LocalDateTime inicioMes = LocalDateTime.of(primeiroDiaMes, LocalTime.MIN);
            LocalDateTime fimMes = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

            List<Venda> vendasHojeList = vendaRepository.findByDataVendaBetween(inicioHoje, fimHoje);
            Double totalVendasHoje = vendasHojeList.stream()
                    .mapToDouble(venda -> venda.getTotal() != null ? venda.getTotal() : 0.0)
                    .sum();
            Integer countVendasHoje = vendasHojeList.size();

            List<Venda> vendasMesList = vendaRepository.findByDataVendaBetween(inicioMes, fimMes);
            Double totalVendasMes = vendasMesList.stream()
                    .mapToDouble(venda -> venda.getTotal() != null ? venda.getTotal() : 0.0)
                    .sum();
            Integer countVendasMes = vendasMesList.size();

            Double ticketMedio = countVendasMes > 0 ? totalVendasMes / countVendasMes : 0.0;
            Double crescimentoPercentual = 0.0;

            return new MetricasVendasDTO(
                totalVendasHoje != null ? totalVendasHoje : 0.0,
                totalVendasMes != null ? totalVendasMes : 0.0,
                countVendasHoje != null ? countVendasHoje : 0,
                countVendasMes != null ? countVendasMes : 0,
                ticketMedio != null ? ticketMedio : 0.0,
                crescimentoPercentual != null ? crescimentoPercentual : 0.0
            );
        } catch (Exception e) {
            logger.error("Erro ao calcular métricas de vendas", e);
            return new MetricasVendasDTO(0.0, 0.0, 0, 0, 0.0, 0.0);
        }
    }

    private List<ProdutoMaisVendidoDTO> obterProdutosMaisVendidos() {
        try {
            List<Venda> todasVendas = vendaRepository.findAll();
            
            Map<Long, ProdutoMaisVendidoDTO> produtoMap = new HashMap<>();
            
            for (Venda venda : todasVendas) {
                if (venda.getProduto() == null) {
                    logger.warn("Venda {} sem produto associado", venda.getIdVenda());
                    continue;
                }
                
                Long produtoId = venda.getProduto().getIdProduto();
                String produtoNome = venda.getProduto().getNomeProduto() != null ? 
                    venda.getProduto().getNomeProduto() : "Produto Desconhecido";
                
                ProdutoMaisVendidoDTO current = produtoMap.getOrDefault(produtoId, 
                    new ProdutoMaisVendidoDTO(produtoId, produtoNome, 0.0, 0.0, 0));
                
                Double pesoVendido = venda.getPesoVendido() != null ? venda.getPesoVendido() : 0.0;
                Double totalVenda = venda.getTotal() != null ? venda.getTotal() : 0.0;
                
                ProdutoMaisVendidoDTO updated = new ProdutoMaisVendidoDTO(
                    produtoId,
                    produtoNome,
                    current.quantidadeVendida() + pesoVendido,
                    current.totalVendas() + totalVenda,
                    current.numeroVendas() + 1
                );
                
                produtoMap.put(produtoId, updated);
            }
            
            return produtoMap.values().stream()
                    .sorted((a, b) -> Double.compare(b.totalVendas(), a.totalVendas()))
                    .limit(10)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erro ao obter produtos mais vendidos", e);
            return new ArrayList<>();
        }
    }

    private List<ClienteTopDTO> obterClientesTop() {
        try {
            List<Venda> todasVendas = vendaRepository.findAll();
            List<Venda> vendasComCliente = todasVendas.stream()
                    .filter(v -> v.getCliente() != null)
                    .collect(Collectors.toList());
            
            Map<Long, ClienteTopDTO> clienteMap = new HashMap<>();
            
            for (Venda venda : vendasComCliente) {
                Long clienteId = venda.getCliente().getIdCliente();
                String clienteNome = venda.getCliente().getNome() != null ? 
                    venda.getCliente().getNome() : "Cliente Sem Nome";
                
                ClienteTopDTO current = clienteMap.getOrDefault(clienteId, 
                    new ClienteTopDTO(clienteId, clienteNome, 0.0, 0, 0.0));
                
                int newNumeroCompras = current.numeroCompras() + 1;
                double newTotalCompras = current.totalCompras() + (venda.getTotal() != null ? venda.getTotal() : 0.0);
                double newTicketMedio = newNumeroCompras > 0 ? newTotalCompras / newNumeroCompras : 0.0;
                
                ClienteTopDTO updated = new ClienteTopDTO(
                    clienteId,
                    clienteNome,
                    newTotalCompras,
                    newNumeroCompras,
                    newTicketMedio
                );
                
                clienteMap.put(clienteId, updated);
            }
            
            return clienteMap.values().stream()
                    .sorted((a, b) -> Double.compare(b.totalCompras(), a.totalCompras()))
                    .limit(10)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erro ao obter clientes top", e);
            return new ArrayList<>();
        }
    }

    private MetricasFuncionariosDTO obterMetricasFuncionarios() {
        try {
            List<Object> todosFuncionarios = new ArrayList<>(funcionarioRepository.findAll());
            Integer totalFuncionarios = todosFuncionarios.size();
            
            List<Object> funcionariosAtivosList = new ArrayList<>(funcionarioRepository.findByAtivo(true));
            Integer funcionariosAtivos = funcionariosAtivosList.size();
            
            Integer funcionariosFerias = 0;
            try {
                List<Object> feriasAprovadas = new ArrayList<>(feriasRepository.findByStatus("aprovado"));
                List<Object> feriasEmAndamento = new ArrayList<>(feriasRepository.findByStatus("em_andamento"));
                funcionariosFerias = feriasAprovadas.size() + feriasEmAndamento.size();
            } catch (Exception e) {
                logger.warn("Erro ao calcular funcionários de férias: {}", e.getMessage());
            }
            
            Integer expedientesHoje = 0;
            try {
                String diaSemanaHoje = obterDiaSemanaPortugues(LocalDate.now().getDayOfWeek().getValue());
                List<Object> expedientes = new ArrayList<>(expedienteRepository.findByDiaSemana(diaSemanaHoje));
                expedientesHoje = expedientes.size();
            } catch (Exception e) {
                logger.warn("Erro ao calcular expedientes de hoje: {}", e.getMessage());
            }

            return new MetricasFuncionariosDTO(
                totalFuncionarios,
                funcionariosAtivos,
                funcionariosFerias,
                expedientesHoje
            );
        } catch (Exception e) {
            logger.error("Erro ao obter métricas de funcionários: {}", e.getMessage(), e);
            return new MetricasFuncionariosDTO(0, 0, 0, 0);
        }
    }

    private AlertasEstoqueDTO obterAlertasEstoque() {
        try {
            Integer ingredientesParaRepor = 0;
            List<String> alertasCriticos = new ArrayList<>();
            
            try {
                List<Object> ingredientesParaReporList = new ArrayList<>(
                    estoqueIngredienteRepository.findByPrecisaReporTrue());
                ingredientesParaRepor = ingredientesParaReporList.size();
                
                alertasCriticos = estoqueIngredienteRepository.findByPrecisaReporTrue()
                        .stream()
                        .map(e -> {
                            String nome = e.getNomeIngrediente() != null ? e.getNomeIngrediente() : "Ingrediente Desconhecido";
                            Double quantidade = e.getQuantidadeEstoque() != null ? e.getQuantidadeEstoque() : 0.0;
                            String unidade = e.getUnidadeMedida() != null ? e.getUnidadeMedida() : "un";
                            Double minimo = e.getEstoqueMinimo() != null ? e.getEstoqueMinimo() : 0.0;
                            
                            return String.format("%s - Estoque: %.2f %s (Mínimo: %.2f)", 
                                nome, quantidade, unidade, minimo);
                        })
                        .collect(Collectors.toList());
            } catch (Exception e) {
                logger.warn("Erro ao calcular alertas de estoque: {}", e.getMessage());
            }

            return new AlertasEstoqueDTO(
                ingredientesParaRepor,
                ingredientesParaRepor,
                alertasCriticos
            );
        } catch (Exception e) {
            logger.error("Erro ao obter alertas de estoque: {}", e.getMessage(), e);
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