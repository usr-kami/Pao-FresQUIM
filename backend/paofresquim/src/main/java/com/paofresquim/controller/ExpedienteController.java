package com.paofresquim.controller;

import com.paofresquim.dto.ExpedienteRequestDTO;
import com.paofresquim.dto.ExpedienteResponseDTO;
import com.paofresquim.service.ExpedienteService;
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
@RequestMapping("/api/expediente")
public class ExpedienteController {

    private static final Logger logger = LoggerFactory.getLogger(ExpedienteController.class);

    @Autowired
    private ExpedienteService expedienteService;

    @GetMapping
    public ResponseEntity<List<ExpedienteResponseDTO>> listarTodos() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Listando todos os expedientes");
            List<ExpedienteResponseDTO> expedientes = expedienteService.listarTodos();
            logger.info("Listagem concluída. Total de expedientes: {}", expedientes.size());
            return ResponseEntity.ok(expedientes);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpedienteResponseDTO> buscarPorId(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando expediente por ID: {}", id);
            Optional<ExpedienteResponseDTO> expediente = expedienteService.buscarPorId(id);
            if (expediente.isPresent()) {
                logger.info("Expediente encontrado. ID: {}, Funcionário: {}", 
                           id, expediente.get().nomeFuncionario());
                return ResponseEntity.ok(expediente.get());
            } else {
                logger.warn("Expediente não encontrado com ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/funcionario/{idFuncionario}")
    public ResponseEntity<List<ExpedienteResponseDTO>> buscarPorFuncionario(@PathVariable Long idFuncionario) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando expedientes por funcionário ID: {}", idFuncionario);
            List<ExpedienteResponseDTO> expedientes = expedienteService.buscarPorFuncionario(idFuncionario);
            logger.info("Busca por funcionário ID {} retornou {} expedientes", idFuncionario, expedientes.size());
            return ResponseEntity.ok(expedientes);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/dia/{diaSemana}")
    public ResponseEntity<List<ExpedienteResponseDTO>> buscarPorDiaSemana(@PathVariable String diaSemana) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando expedientes por dia da semana: {}", diaSemana);
            List<ExpedienteResponseDTO> expedientes = expedienteService.buscarPorDiaSemana(diaSemana);
            logger.info("Busca por dia '{}' retornou {} expedientes", diaSemana, expedientes.size());
            return ResponseEntity.ok(expedientes);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/turno/{turno}")
    public ResponseEntity<List<ExpedienteResponseDTO>> buscarPorTurno(@PathVariable String turno) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando expedientes por turno: {}", turno);
            List<ExpedienteResponseDTO> expedientes = expedienteService.buscarPorTurno(turno);
            logger.info("Busca por turno '{}' retornou {} expedientes", turno, expedientes.size());
            return ResponseEntity.ok(expedientes);
        } finally {
            MDC.clear();
        }
    }

    @PostMapping
    public ResponseEntity<?> criarExpediente(@Valid @RequestBody ExpedienteRequestDTO expedienteRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Criando novo expediente para funcionário ID: {}", expedienteRequest.idFuncionario());
            ExpedienteResponseDTO expedienteCriado = expedienteService.criar(expedienteRequest);
            logger.info("Expediente criado com sucesso. ID: {}, Dia: {}", 
                       expedienteCriado.idExpediente(), expedienteCriado.diaSemana());
            return ResponseEntity.status(HttpStatus.CREATED).body(expedienteCriado);
        } catch (Exception e) {
            logger.error("Erro ao criar expediente: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarExpediente(@PathVariable Long id, 
                                               @Valid @RequestBody ExpedienteRequestDTO expedienteRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Atualizando expediente ID: {}", id);
            Optional<ExpedienteResponseDTO> expedienteAtualizado = expedienteService.atualizar(id, expedienteRequest);
            if (expedienteAtualizado.isPresent()) {
                logger.info("Expediente atualizado com sucesso. ID: {}", id);
                return ResponseEntity.ok(expedienteAtualizado.get());
            } else {
                logger.warn("Expediente não encontrado para atualização. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar expediente ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExpediente(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Deletando expediente ID: {}", id);
            boolean deletado = expedienteService.deletar(id);
            if (deletado) {
                logger.info("Expediente deletado com sucesso. ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Expediente não encontrado para deleção. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }
}