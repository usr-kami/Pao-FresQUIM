package com.paofresquim.controller;

import com.paofresquim.dto.FuncionarioRequestDTO;
import com.paofresquim.dto.FuncionarioResponseDTO;
import com.paofresquim.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDTO>> listarTodos() {
        List<FuncionarioResponseDTO> funcionarios = funcionarioService.listarTodos();
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<FuncionarioResponseDTO> funcionario = funcionarioService.buscarPorId(id);
        return funcionario.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<FuncionarioResponseDTO>> buscarPorNome(@RequestParam String nome) {
        List<FuncionarioResponseDTO> funcionarios = funcionarioService.buscarPorNome(nome);
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/cargo")
    public ResponseEntity<List<FuncionarioResponseDTO>> buscarPorCargo(@RequestParam String cargo) {
        List<FuncionarioResponseDTO> funcionarios = funcionarioService.buscarPorCargo(cargo);
        return ResponseEntity.ok(funcionarios);
    }

    @GetMapping("/status")
    public ResponseEntity<List<FuncionarioResponseDTO>> buscarPorStatus(@RequestParam Boolean ativo) {
        List<FuncionarioResponseDTO> funcionarios = funcionarioService.buscarPorStatus(ativo);
        return ResponseEntity.ok(funcionarios);
    }

    @PostMapping
    public ResponseEntity<?> criarFuncionario(@Valid @RequestBody FuncionarioRequestDTO funcionarioRequest) {
        try {
            FuncionarioResponseDTO funcionarioCriado = funcionarioService.criarFuncionario(funcionarioRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFuncionario(@PathVariable Long id, 
                                                @Valid @RequestBody FuncionarioRequestDTO funcionarioRequest) {
        try {
            Optional<FuncionarioResponseDTO> funcionarioAtualizado = 
                funcionarioService.atualizarFuncionario(id, funcionarioRequest);
            return funcionarioAtualizado.map(ResponseEntity::ok)
                                      .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<?> inativarFuncionario(@PathVariable Long id) {
        try {
            Optional<FuncionarioResponseDTO> funcionarioInativado = funcionarioService.inativarFuncionario(id);
            return funcionarioInativado.map(ResponseEntity::ok)
                                     .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativarFuncionario(@PathVariable Long id) {
        try {
            Optional<FuncionarioResponseDTO> funcionarioAtivado = funcionarioService.ativarFuncionario(id);
            return funcionarioAtivado.map(ResponseEntity::ok)
                                   .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        boolean deletado = funcionarioService.deletarFuncionario(id);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}