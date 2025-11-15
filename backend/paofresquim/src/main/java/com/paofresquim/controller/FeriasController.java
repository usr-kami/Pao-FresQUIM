package com.paofresquim.controller;

import com.paofresquim.dto.FeriasRequestDTO;
import com.paofresquim.dto.FeriasResponseDTO;
import com.paofresquim.service.FeriasService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ferias")
public class FeriasController {

    @Autowired
    private FeriasService feriasService;

    @GetMapping
    public ResponseEntity<List<FeriasResponseDTO>> listarTodas() {
        List<FeriasResponseDTO> ferias = feriasService.listarTodas();
        return ResponseEntity.ok(ferias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeriasResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<FeriasResponseDTO> ferias = feriasService.buscarPorId(id);
        return ferias.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/funcionario/{idFuncionario}")
    public ResponseEntity<List<FeriasResponseDTO>> buscarPorFuncionario(@PathVariable Long idFuncionario) {
        List<FeriasResponseDTO> ferias = feriasService.buscarPorFuncionario(idFuncionario);
        return ResponseEntity.ok(ferias);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FeriasResponseDTO>> buscarPorStatus(@PathVariable String status) {
        List<FeriasResponseDTO> ferias = feriasService.buscarPorStatus(status);
        return ResponseEntity.ok(ferias);
    }

    @PostMapping
    public ResponseEntity<?> solicitarFerias(@Valid @RequestBody FeriasRequestDTO feriasRequest) {
        try {
            FeriasResponseDTO feriasSolicitada = feriasService.solicitarFerias(feriasRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(feriasSolicitada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFerias(@PathVariable Long id, @Valid @RequestBody FeriasRequestDTO feriasRequest) {
        try {
            Optional<FeriasResponseDTO> feriasAtualizada = feriasService.atualizarFerias(id, feriasRequest);
            return feriasAtualizada.map(ResponseEntity::ok)
                                 .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Optional<FeriasResponseDTO> feriasAtualizada = feriasService.atualizarStatus(id, status);
            return feriasAtualizada.map(ResponseEntity::ok)
                                 .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarFerias(@PathVariable Long id) {
        try {
            boolean deletado = feriasService.deletarFerias(id);
            return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}