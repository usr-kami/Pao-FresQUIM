package com.paofresquim.controller;

import com.paofresquim.dto.ProdutoRequestDTO;
import com.paofresquim.dto.ProdutoResponseDTO;
import com.paofresquim.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<ProdutoResponseDTO> produto = produtoService.buscarPorId(id);
        return produto.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarPorNome(nome);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/faixa-preco")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorFaixaPreco(
            @RequestParam Double precoMin, 
            @RequestParam Double precoMax) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarPorFaixaPreco(precoMin, precoMax);
        return ResponseEntity.ok(produtos);
    }

    @PostMapping
    public ResponseEntity<?> criarProduto(@Valid @RequestBody ProdutoRequestDTO produtoRequest) {
        try {
            ProdutoResponseDTO produtoCriado = produtoService.criarProduto(produtoRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO produtoRequest) {
        try {
            Optional<ProdutoResponseDTO> produtoAtualizado = produtoService.atualizarProduto(id, produtoRequest);
            return produtoAtualizado.map(ResponseEntity::ok)
                                  .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProduto(@PathVariable Long id) {
        try {
            boolean deletado = produtoService.deletarProduto(id);
            return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}