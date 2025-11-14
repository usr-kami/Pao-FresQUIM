package com.paofresquim.controller;

import com.paofresquim.dto.EstoqueIngredienteRequestDTO;
import com.paofresquim.dto.EstoqueIngredienteResponseDTO;
import com.paofresquim.service.EstoqueIngredienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/estoque-ingredientes")
public class EstoqueIngredienteController {

    @Autowired
    private EstoqueIngredienteService estoqueIngredienteService;

    @GetMapping
    public ResponseEntity<List<EstoqueIngredienteResponseDTO>> listarTodos() {
        List<EstoqueIngredienteResponseDTO> ingredientes = estoqueIngredienteService.listarTodos();
        return ResponseEntity.ok(ingredientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueIngredienteResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<EstoqueIngredienteResponseDTO> ingrediente = estoqueIngredienteService.buscarPorId(id);
        return ingrediente.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<EstoqueIngredienteResponseDTO>> buscarPorNome(@RequestParam String nome) {
        List<EstoqueIngredienteResponseDTO> ingredientes = estoqueIngredienteService.buscarPorNome(nome);
        return ResponseEntity.ok(ingredientes);
    }

    @GetMapping("/estoque-minimo")
    public ResponseEntity<List<EstoqueIngredienteResponseDTO>> buscarPorEstoqueMinimo() {
        List<EstoqueIngredienteResponseDTO> ingredientes = estoqueIngredienteService.buscarPorEstoqueMinimo();
        return ResponseEntity.ok(ingredientes);
    }

    @GetMapping("/alerta-reposicao")
    public ResponseEntity<List<EstoqueIngredienteResponseDTO>> buscarPrecisaRepor() {
        List<EstoqueIngredienteResponseDTO> ingredientes = estoqueIngredienteService.buscarPrecisaRepor();
        return ResponseEntity.ok(ingredientes);
    }

    @PostMapping
    public ResponseEntity<?> criarIngrediente(@Valid @RequestBody EstoqueIngredienteRequestDTO ingredienteRequest) {
        try {
            EstoqueIngredienteResponseDTO ingredienteCriado = estoqueIngredienteService.criarIngrediente(ingredienteRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(ingredienteCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarIngrediente(@PathVariable Long id, 
                                                @Valid @RequestBody EstoqueIngredienteRequestDTO ingredienteRequest) {
        try {
            Optional<EstoqueIngredienteResponseDTO> ingredienteAtualizado = 
                estoqueIngredienteService.atualizarIngrediente(id, ingredienteRequest);
            return ingredienteAtualizado.map(ResponseEntity::ok)
                                      .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/quantidade")
    public ResponseEntity<?> atualizarQuantidade(@PathVariable Long id, 
                                               @RequestParam Double novaQuantidade) {
        try {
            Optional<EstoqueIngredienteResponseDTO> ingredienteAtualizado = 
                estoqueIngredienteService.atualizarQuantidade(id, novaQuantidade);
            return ingredienteAtualizado.map(ResponseEntity::ok)
                                      .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/custo")
    public ResponseEntity<?> atualizarCusto(@PathVariable Long id, 
                                          @RequestParam Double novoCusto) {
        try {
            Optional<EstoqueIngredienteResponseDTO> ingredienteAtualizado = 
                estoqueIngredienteService.atualizarCusto(id, novoCusto);
            return ingredienteAtualizado.map(ResponseEntity::ok)
                                      .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarIngrediente(@PathVariable Long id) {
        boolean deletado = estoqueIngredienteService.deletarIngrediente(id);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}