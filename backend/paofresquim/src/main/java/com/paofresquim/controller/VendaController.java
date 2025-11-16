package com.paofresquim.controller;

import com.paofresquim.dto.VendaRequestDTO;
import com.paofresquim.dto.VendaResponseDTO;
import com.paofresquim.service.VendaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private static final Logger logger = LoggerFactory.getLogger(VendaController.class);

    @Autowired
    private VendaService vendaService;

    @GetMapping
    public ResponseEntity<List<VendaResponseDTO>> listarTodas() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Listando todas as vendas");
            List<VendaResponseDTO> vendas = vendaService.listarTodos();
            logger.info("Listagem concluída. Total de vendas: {}", vendas.size());
            return ResponseEntity.ok(vendas);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando venda por ID: {}", id);
            Optional<VendaResponseDTO> venda = vendaService.buscarPorId(id);
            if (venda.isPresent()) {
                logger.info("Venda encontrada. ID: {}, Produto: {}", id, venda.get().nomeProduto());
                return ResponseEntity.ok(venda.get());
            } else {
                logger.warn("Venda não encontrada com ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VendaResponseDTO>> buscarPorCliente(@PathVariable Long idCliente) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando vendas por cliente ID: {}", idCliente);
            List<VendaResponseDTO> vendas = vendaService.buscarPorCliente(idCliente);
            logger.info("Busca por cliente ID {} retornou {} vendas", idCliente, vendas.size());
            return ResponseEntity.ok(vendas);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<List<VendaResponseDTO>> buscarPorProduto(@PathVariable Long idProduto) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando vendas por produto ID: {}", idProduto);
            List<VendaResponseDTO> vendas = vendaService.buscarPorProduto(idProduto);
            logger.info("Busca por produto ID {} retornou {} vendas", idProduto, vendas.size());
            return ResponseEntity.ok(vendas);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<VendaResponseDTO>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando vendas por período: {} - {}", inicio, fim);
            List<VendaResponseDTO> vendas = vendaService.buscarPorPeriodo(inicio, fim);
            logger.info("Busca por período retornou {} vendas", vendas.size());
            return ResponseEntity.ok(vendas);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/status-pagamento")
    public ResponseEntity<List<VendaResponseDTO>> buscarPorStatusPagamento(@RequestParam String status) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando vendas por status de pagamento: {}", status);
            List<VendaResponseDTO> vendas = vendaService.buscarPorStatusPagamento(status);
            logger.info("Busca por status '{}' retornou {} vendas", status, vendas.size());
            return ResponseEntity.ok(vendas);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/forma-pagamento")
    public ResponseEntity<List<VendaResponseDTO>> buscarPorFormaPagamento(@RequestParam String forma) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando vendas por forma de pagamento: {}", forma);
            List<VendaResponseDTO> vendas = vendaService.buscarPorFormaPagamento(forma);
            logger.info("Busca por forma '{}' retornou {} vendas", forma, vendas.size());
            return ResponseEntity.ok(vendas);
        } finally {
            MDC.clear();
        }
    }

    @PostMapping
    public ResponseEntity<?> criarVenda(@Valid @RequestBody VendaRequestDTO vendaRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Criando nova venda para produto ID: {}", vendaRequest.idProduto());
            VendaResponseDTO vendaCriada = vendaService.criar(vendaRequest);
            logger.info("Venda criada com sucesso. ID: {}, Total: R$ {}", 
                       vendaCriada.idVenda(), vendaCriada.total());
            return ResponseEntity.status(HttpStatus.CREATED).body(vendaCriada);
        } catch (Exception e) {
            logger.error("Erro ao criar venda: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarVenda(@PathVariable Long id, 
                                          @Valid @RequestBody VendaRequestDTO vendaRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Atualizando venda ID: {}", id);
            Optional<VendaResponseDTO> vendaAtualizada = vendaService.atualizar(id, vendaRequest);
            if (vendaAtualizada.isPresent()) {
                logger.info("Venda atualizada com sucesso. ID: {}", id);
                return ResponseEntity.ok(vendaAtualizada.get());
            } else {
                logger.warn("Venda não encontrada para atualização. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar venda ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PatchMapping("/{id}/status-pagamento")
    public ResponseEntity<?> atualizarStatusPagamento(@PathVariable Long id, @RequestParam String status) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Atualizando status de pagamento da venda ID: {} para {}", id, status);
            Optional<VendaResponseDTO> vendaAtualizada = vendaService.atualizarStatusPagamento(id, status);
            if (vendaAtualizada.isPresent()) {
                logger.info("Status de pagamento atualizado com sucesso para venda ID: {}", id);
                return ResponseEntity.ok(vendaAtualizada.get());
            } else {
                logger.warn("Venda não encontrada para atualização de status. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar status de pagamento da venda ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Deletando venda ID: {}", id);
            boolean deletado = vendaService.deletar(id);
            if (deletado) {
                logger.info("Venda deletada com sucesso. ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Venda não encontrada para deleção. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }
}