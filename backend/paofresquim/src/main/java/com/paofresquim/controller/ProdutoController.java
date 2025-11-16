package com.paofresquim.controller;

import com.paofresquim.dto.ProdutoRequestDTO;
import com.paofresquim.dto.ProdutoResponseDTO;
import com.paofresquim.service.ProdutoService;
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
@RequestMapping("/api/produtos")
public class ProdutoController {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Listando todos os produtos");
            List<ProdutoResponseDTO> produtos = produtoService.listarTodos();
            logger.info("Listagem concluída. Total de produtos: {}", produtos.size());
            return ResponseEntity.ok(produtos);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando produto por ID: {}", id);
            Optional<ProdutoResponseDTO> produto = produtoService.buscarPorId(id);
            if (produto.isPresent()) {
                logger.info("Produto encontrado: {}", produto.get().nomeProduto());
                return ResponseEntity.ok(produto.get());
            } else {
                logger.warn("Produto não encontrado com ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/busca")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando produtos por nome: {}", nome);
            List<ProdutoResponseDTO> produtos = produtoService.buscarPorNome(nome);
            logger.info("Busca por nome '{}' retornou {} produtos", nome, produtos.size());
            return ResponseEntity.ok(produtos);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/faixa-preco")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorFaixaPreco(
            @RequestParam Double precoMin, 
            @RequestParam Double precoMax) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando produtos por faixa de preço: {} - {}", precoMin, precoMax);
            List<ProdutoResponseDTO> produtos = produtoService.buscarPorFaixaPreco(precoMin, precoMax);
            logger.info("Busca por faixa de preço retornou {} produtos", produtos.size());
            return ResponseEntity.ok(produtos);
        } finally {
            MDC.clear();
        }
    }

    @PostMapping
    public ResponseEntity<?> criarProduto(@Valid @RequestBody ProdutoRequestDTO produtoRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Criando novo produto: {}", produtoRequest.nomeProduto());
            ProdutoResponseDTO produtoCriado = produtoService.criar(produtoRequest);
            logger.info("Produto criado com sucesso. ID: {}, Nome: {}", 
                       produtoCriado.idProduto(), produtoCriado.nomeProduto());
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
        } catch (Exception e) {
            logger.error("Erro ao criar produto: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, 
                                            @Valid @RequestBody ProdutoRequestDTO produtoRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Atualizando produto ID: {}", id);
            Optional<ProdutoResponseDTO> produtoAtualizado = produtoService.atualizar(id, produtoRequest);
            if (produtoAtualizado.isPresent()) {
                logger.info("Produto atualizado com sucesso. ID: {}", id);
                return ResponseEntity.ok(produtoAtualizado.get());
            } else {
                logger.warn("Produto não encontrado para atualização. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar produto ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProduto(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Deletando produto ID: {}", id);
            boolean deletado = produtoService.deletar(id);
            if (deletado) {
                logger.info("Produto deletado com sucesso. ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Produto não encontrado para deleção. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao deletar produto ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }
}