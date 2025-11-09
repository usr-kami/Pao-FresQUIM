package com.paofresquim.controller;

import com.paofresquim.dto.ClienteRequestDTO;
import com.paofresquim.dto.ClienteResponseDTO;
import com.paofresquim.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        List<ClienteResponseDTO> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<ClienteResponseDTO> cliente = clienteService.buscarPorId(id);
        return cliente.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<ClienteResponseDTO>> buscarPorNome(@RequestParam String nome) {
        List<ClienteResponseDTO> clientes = clienteService.buscarPorNome(nome);
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<?> criarCliente(@Valid @RequestBody ClienteRequestDTO clienteRequest) {
        try {
            ClienteResponseDTO clienteCriado = clienteService.criarCliente(clienteRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, 
                                            @Valid @RequestBody ClienteRequestDTO clienteRequest) {
        try {
            Optional<ClienteResponseDTO> clienteAtualizado = clienteService.atualizarCliente(id, clienteRequest);
            return clienteAtualizado.map(ResponseEntity::ok)
                                  .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        boolean deletado = clienteService.deletarCliente(id);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}