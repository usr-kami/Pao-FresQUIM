package com.paofresquim.controller;

import com.paofresquim.dto.ClienteRequestDTO;
import com.paofresquim.dto.ClienteResponseDTO;
import com.paofresquim.service.ClienteService;
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
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    
    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Iniciando listagem de todos os clientes");
            List<ClienteResponseDTO> clientes = clienteService.listarTodos();
            logger.info("Listagem concluída. Total de clientes: {}", clientes.size());
            return ResponseEntity.ok(clientes);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando cliente por ID: {}", id);
            Optional<ClienteResponseDTO> cliente = clienteService.buscarPorId(id);
            if (cliente.isPresent()) {
                logger.info("Cliente encontrado: {}", cliente.get().nome());
                return ResponseEntity.ok(cliente.get());
            } else {
                logger.warn("Cliente não encontrado com ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/busca")
    public ResponseEntity<List<ClienteResponseDTO>> buscarPorNome(@RequestParam String nome) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando clientes por nome: {}", nome);
            List<ClienteResponseDTO> clientes = clienteService.buscarPorNome(nome);
            logger.info("Busca por nome '{}' retornou {} clientes", nome, clientes.size());
            return ResponseEntity.ok(clientes);
        } finally {
            MDC.clear();
        }
    }

    @PostMapping
    public ResponseEntity<?> criarCliente(@Valid @RequestBody ClienteRequestDTO clienteRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Criando novo cliente: {}", clienteRequest.nome());
            ClienteResponseDTO clienteCriado = clienteService.criar(clienteRequest);
            logger.info("Cliente criado com sucesso. ID: {}, Nome: {}", 
                       clienteCriado.idCliente(), clienteCriado.nome());
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
        } catch (Exception e) {
            logger.error("Erro ao criar cliente: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, 
                                            @Valid @RequestBody ClienteRequestDTO clienteRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Atualizando cliente ID: {}", id);
            Optional<ClienteResponseDTO> clienteAtualizado = clienteService.atualizar(id, clienteRequest);
            if (clienteAtualizado.isPresent()) {
                logger.info("Cliente atualizado com sucesso. ID: {}", id);
                return ResponseEntity.ok(clienteAtualizado.get());
            } else {
                logger.warn("Cliente não encontrado para atualização. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar cliente ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Deletando cliente ID: {}", id);
            boolean deletado = clienteService.deletar(id);
            if (deletado) {
                logger.info("Cliente deletado com sucesso. ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Cliente não encontrado para deleção. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }
}