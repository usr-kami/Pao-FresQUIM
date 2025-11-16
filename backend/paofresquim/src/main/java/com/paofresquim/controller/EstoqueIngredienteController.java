package com.paofresquim.controller;

import com.paofresquim.dto.EstoqueIngredienteRequestDTO;
import com.paofresquim.dto.EstoqueIngredienteResponseDTO;
import com.paofresquim.service.EstoqueIngredienteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/estoque-ingredientes")
public class EstoqueIngredienteController {

    private static final Logger logger = LoggerFactory.getLogger(EstoqueIngredienteController.class);

    @Autowired
    private EstoqueIngredienteService estoqueIngredienteService;

    @GetMapping
    public ResponseEntity<List<EstoqueIngredienteResponseDTO>> listarTodos() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Listando todos os ingredientes em estoque");
            List<EstoqueIngredienteResponseDTO> ingredientes = estoqueIngredienteService.listarTodos();
            logger.info("Listagem concluída. Total de ingredientes: {}", ingredientes.size());
            return ResponseEntity.ok(ingredientes);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueIngredienteResponseDTO> buscarPorId(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando ingrediente por ID: {}", id);
            Optional<EstoqueIngredienteResponseDTO> ingrediente = estoqueIngredienteService.buscarPorId(id);
            if (ingrediente.isPresent()) {
                logger.info("Ingrediente encontrado: {}", ingrediente.get().nomeIngrediente());
                return ResponseEntity.ok(ingrediente.get());
            } else {
                logger.warn("Ingrediente não encontrado com ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/busca")
    public ResponseEntity<List<EstoqueIngredienteResponseDTO>> buscarPorNome(@RequestParam String nome) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando ingredientes por nome: {}", nome);
            List<EstoqueIngredienteResponseDTO> ingredientes = estoqueIngredienteService.buscarPorNome(nome);
            logger.info("Busca por nome '{}' retornou {} ingredientes", nome, ingredientes.size());
            return ResponseEntity.ok(ingredientes);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/estoque-minimo")
    public ResponseEntity<List<EstoqueIngredienteResponseDTO>> buscarPorEstoqueMinimo() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando ingredientes com estoque mínimo");
            List<EstoqueIngredienteResponseDTO> ingredientes = estoqueIngredienteService.buscarPorEstoqueMinimo();
            logger.info("Encontrados {} ingredientes com estoque mínimo", ingredientes.size());
            return ResponseEntity.ok(ingredientes);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/alerta-reposicao")
    public ResponseEntity<List<EstoqueIngredienteResponseDTO>> buscarPrecisaRepor() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando ingredientes que precisam de reposição");
            List<EstoqueIngredienteResponseDTO> ingredientes = estoqueIngredienteService.buscarPrecisaRepor();
            logger.info("Encontrados {} ingredientes que precisam de reposição", ingredientes.size());
            return ResponseEntity.ok(ingredientes);
        } finally {
            MDC.clear();
        }
    }

    @PostMapping
    public ResponseEntity<?> criarIngrediente(@Valid @RequestBody EstoqueIngredienteRequestDTO ingredienteRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Criando novo ingrediente: {}", ingredienteRequest.nomeIngrediente());
            EstoqueIngredienteResponseDTO ingredienteCriado = estoqueIngredienteService.criar(ingredienteRequest);
            logger.info("Ingrediente criado com sucesso. ID: {}, Nome: {}", 
                       ingredienteCriado.idIngrediente(), ingredienteCriado.nomeIngrediente());
            return ResponseEntity.status(HttpStatus.CREATED).body(ingredienteCriado);
        } catch (Exception e) {
            logger.error("Erro ao criar ingrediente: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarIngrediente(@PathVariable Long id, 
                                                @Valid @RequestBody EstoqueIngredienteRequestDTO ingredienteRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Atualizando ingrediente ID: {}", id);
            Optional<EstoqueIngredienteResponseDTO> ingredienteAtualizado = 
                estoqueIngredienteService.atualizar(id, ingredienteRequest);
            if (ingredienteAtualizado.isPresent()) {
                logger.info("Ingrediente atualizado com sucesso. ID: {}", id);
                return ResponseEntity.ok(ingredienteAtualizado.get());
            } else {
                logger.warn("Ingrediente não encontrado para atualização. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar ingrediente ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PatchMapping("/{id}/quantidade")
    public ResponseEntity<?> atualizarQuantidade(@PathVariable Long id, 
                                               @RequestParam Double novaQuantidade) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Atualizando quantidade do ingrediente ID: {} para {}", id, novaQuantidade);
            Optional<EstoqueIngredienteResponseDTO> ingredienteAtualizado = 
                estoqueIngredienteService.atualizarQuantidade(id, novaQuantidade);
            if (ingredienteAtualizado.isPresent()) {
                logger.info("Quantidade atualizada com sucesso para ingrediente ID: {}", id);
                return ResponseEntity.ok(ingredienteAtualizado.get());
            } else {
                logger.warn("Ingrediente não encontrado para atualização de quantidade. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar quantidade do ingrediente ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PatchMapping("/{id}/custo")
    public ResponseEntity<?> atualizarCusto(@PathVariable Long id, 
                                          @RequestParam Double novoCusto) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Atualizando custo do ingrediente ID: {} para {}", id, novoCusto);
            Optional<EstoqueIngredienteResponseDTO> ingredienteAtualizado = 
                estoqueIngredienteService.atualizarCusto(id, novoCusto);
            if (ingredienteAtualizado.isPresent()) {
                logger.info("Custo atualizado com sucesso para ingrediente ID: {}", id);
                return ResponseEntity.ok(ingredienteAtualizado.get());
            } else {
                logger.warn("Ingrediente não encontrado para atualização de custo. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar custo do ingrediente ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarIngrediente(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Deletando ingrediente ID: {}", id);
            boolean deletado = estoqueIngredienteService.deletar(id);
            if (deletado) {
                logger.info("Ingrediente deletado com sucesso. ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Ingrediente não encontrado para deleção. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }
}