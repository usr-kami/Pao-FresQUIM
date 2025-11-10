package com.paofresquim.controller;

import com.paofresquim.dto.VendaRequestDTO;
import com.paofresquim.dto.VendaResponseDTO;
import com.paofresquim.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @GetMapping
    public ResponseEntity<List<VendaResponseDTO>> listarTodas() {
        List<VendaResponseDTO> vendas = vendaService.listarTodas();
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<VendaResponseDTO> venda = vendaService.buscarPorId(id);
        return venda.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VendaResponseDTO>> buscarPorCliente(@PathVariable Long idCliente) {
        List<VendaResponseDTO> vendas = vendaService.buscarPorCliente(idCliente);
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<List<VendaResponseDTO>> buscarPorProduto(@PathVariable Long idProduto) {
        List<VendaResponseDTO> vendas = vendaService.buscarPorProduto(idProduto);
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<VendaResponseDTO>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        List<VendaResponseDTO> vendas = vendaService.buscarPorPeriodo(inicio, fim);
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/status-pagamento")
    public ResponseEntity<List<VendaResponseDTO>> buscarPorStatusPagamento(@RequestParam String status) {
        List<VendaResponseDTO> vendas = vendaService.buscarPorStatusPagamento(status);
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/forma-pagamento")
    public ResponseEntity<List<VendaResponseDTO>> buscarPorFormaPagamento(@RequestParam String forma) {
        List<VendaResponseDTO> vendas = vendaService.buscarPorFormaPagamento(forma);
        return ResponseEntity.ok(vendas);
    }

    @PostMapping
    public ResponseEntity<?> criarVenda(@Valid @RequestBody VendaRequestDTO vendaRequest) {
        try {
            VendaResponseDTO vendaCriada = vendaService.criarVenda(vendaRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(vendaCriada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarVenda(@PathVariable Long id, @Valid @RequestBody VendaRequestDTO vendaRequest) {
        try {
            Optional<VendaResponseDTO> vendaAtualizada = vendaService.atualizarVenda(id, vendaRequest);
            return vendaAtualizada.map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/status-pagamento")
    public ResponseEntity<?> atualizarStatusPagamento(@PathVariable Long id, @RequestParam String status) {
        try {
            Optional<VendaResponseDTO> vendaAtualizada = vendaService.atualizarStatusPagamento(id, status);
            return vendaAtualizada.map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
        boolean deletado = vendaService.deletarVenda(id);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}