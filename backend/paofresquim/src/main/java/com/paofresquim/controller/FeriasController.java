package com.paofresquim.controller;

import com.paofresquim.dto.FeriasRequestDTO;
import com.paofresquim.dto.FeriasResponseDTO;
import com.paofresquim.service.FeriasService;
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
@RequestMapping("/api/ferias")
public class FeriasController {

    private static final Logger logger = LoggerFactory.getLogger(FeriasController.class);

    @Autowired
    private FeriasService feriasService;

    @GetMapping
    public ResponseEntity<List<FeriasResponseDTO>> listarTodas() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Listando todas as férias");
            List<FeriasResponseDTO> ferias = feriasService.listarTodos();
            logger.info("Listagem concluída. Total de férias: {}", ferias.size());
            return ResponseEntity.ok(ferias);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeriasResponseDTO> buscarPorId(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando férias por ID: {}", id);
            Optional<FeriasResponseDTO> ferias = feriasService.buscarPorId(id);
            if (ferias.isPresent()) {
                logger.info("Férias encontradas. ID: {}, Funcionário: {}", 
                           id, ferias.get().nomeFuncionario());
                return ResponseEntity.ok(ferias.get());
            } else {
                logger.warn("Férias não encontradas com ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/funcionario/{idFuncionario}")
    public ResponseEntity<List<FeriasResponseDTO>> buscarPorFuncionario(@PathVariable Long idFuncionario) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando férias por funcionário ID: {}", idFuncionario);
            List<FeriasResponseDTO> ferias = feriasService.buscarPorFuncionario(idFuncionario);
            logger.info("Busca por funcionário ID {} retornou {} férias", idFuncionario, ferias.size());
            return ResponseEntity.ok(ferias);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FeriasResponseDTO>> buscarPorStatus(@PathVariable String status) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando férias por status: {}", status);
            List<FeriasResponseDTO> ferias = feriasService.buscarPorStatus(status);
            logger.info("Busca por status '{}' retornou {} férias", status, ferias.size());
            return ResponseEntity.ok(ferias);
        } finally {
            MDC.clear();
        }
    }

    @PostMapping
    public ResponseEntity<?> solicitarFerias(@Valid @RequestBody FeriasRequestDTO feriasRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Solicitando férias para funcionário ID: {}", feriasRequest.idFuncionario());
            FeriasResponseDTO feriasSolicitada = feriasService.criar(feriasRequest);
            logger.info("Férias solicitadas com sucesso. ID: {}, Período: {} a {}", 
                       feriasSolicitada.idFerias(), feriasSolicitada.dataInicio(), feriasSolicitada.dataFim());
            return ResponseEntity.status(HttpStatus.CREATED).body(feriasSolicitada);
        } catch (Exception e) {
            logger.error("Erro ao solicitar férias: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFerias(@PathVariable Long id, 
                                           @Valid @RequestBody FeriasRequestDTO feriasRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Atualizando férias ID: {}", id);
            Optional<FeriasResponseDTO> feriasAtualizada = feriasService.atualizar(id, feriasRequest);
            if (feriasAtualizada.isPresent()) {
                logger.info("Férias atualizadas com sucesso. ID: {}", id);
                return ResponseEntity.ok(feriasAtualizada.get());
            } else {
                logger.warn("Férias não encontradas para atualização. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar férias ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Atualizando status das férias ID: {} para {}", id, status);
            Optional<FeriasResponseDTO> feriasAtualizada = feriasService.atualizarStatus(id, status);
            if (feriasAtualizada.isPresent()) {
                logger.info("Status das férias atualizado com sucesso. ID: {}", id);
                return ResponseEntity.ok(feriasAtualizada.get());
            } else {
                logger.warn("Férias não encontradas para atualização de status. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar status das férias ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarFerias(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Deletando férias ID: {}", id);
            boolean deletado = feriasService.deletar(id);
            if (deletado) {
                logger.info("Férias deletadas com sucesso. ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Férias não encontradas para deleção. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao deletar férias ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }
}