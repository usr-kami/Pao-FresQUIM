package com.paofresquim.controller;

import com.paofresquim.dto.ExpedienteRequestDTO;
import com.paofresquim.dto.ExpedienteResponseDTO;
import com.paofresquim.service.ExpedienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expediente")
public class ExpedienteController {

    @Autowired
    private ExpedienteService expedienteService;

    @GetMapping
    public ResponseEntity<List<ExpedienteResponseDTO>> listarTodos() {
        List<ExpedienteResponseDTO> expedientes = expedienteService.listarTodos();
        return ResponseEntity.ok(expedientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpedienteResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<ExpedienteResponseDTO> expediente = expedienteService.buscarPorId(id);
        return expediente.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/funcionario/{idFuncionario}")
    public ResponseEntity<List<ExpedienteResponseDTO>> buscarPorFuncionario(@PathVariable Long idFuncionario) {
        List<ExpedienteResponseDTO> expedientes = expedienteService.buscarPorFuncionario(idFuncionario);
        return ResponseEntity.ok(expedientes);
    }

    @GetMapping("/dia/{diaSemana}")
    public ResponseEntity<List<ExpedienteResponseDTO>> buscarPorDiaSemana(@PathVariable String diaSemana) {
        List<ExpedienteResponseDTO> expedientes = expedienteService.buscarPorDiaSemana(diaSemana);
        return ResponseEntity.ok(expedientes);
    }

    @GetMapping("/turno/{turno}")
    public ResponseEntity<List<ExpedienteResponseDTO>> buscarPorTurno(@PathVariable String turno) {
        List<ExpedienteResponseDTO> expedientes = expedienteService.buscarPorTurno(turno);
        return ResponseEntity.ok(expedientes);
    }

    @PostMapping
    public ResponseEntity<?> criarExpediente(@Valid @RequestBody ExpedienteRequestDTO expedienteRequest) {
        try {
            ExpedienteResponseDTO expedienteCriado = expedienteService.criarExpediente(expedienteRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(expedienteCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarExpediente(@PathVariable Long id, 
                                               @Valid @RequestBody ExpedienteRequestDTO expedienteRequest) {
        try {
            Optional<ExpedienteResponseDTO> expedienteAtualizado = expedienteService.atualizarExpediente(id, expedienteRequest);
            return expedienteAtualizado.map(ResponseEntity::ok)
                                     .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExpediente(@PathVariable Long id) {
        boolean deletado = expedienteService.deletarExpediente(id);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}