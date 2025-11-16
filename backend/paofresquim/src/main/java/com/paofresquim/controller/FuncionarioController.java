package com.paofresquim.controller;

import com.paofresquim.dto.FuncionarioRequestDTO;
import com.paofresquim.dto.FuncionarioResponseDTO;
import com.paofresquim.service.FuncionarioService;
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
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private static final Logger logger = LoggerFactory.getLogger(FuncionarioController.class);

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDTO>> listarTodos() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Listando todos os funcionários");
            List<FuncionarioResponseDTO> funcionarios = funcionarioService.listarTodos();
            logger.info("Listagem concluída. Total de funcionários: {}", funcionarios.size());
            return ResponseEntity.ok(funcionarios);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> buscarPorId(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando funcionário por ID: {}", id);
            Optional<FuncionarioResponseDTO> funcionario = funcionarioService.buscarPorId(id);
            if (funcionario.isPresent()) {
                logger.info("Funcionário encontrado: {}", funcionario.get().nome());
                return ResponseEntity.ok(funcionario.get());
            } else {
                logger.warn("Funcionário não encontrado com ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/busca")
    public ResponseEntity<List<FuncionarioResponseDTO>> buscarPorNome(@RequestParam String nome) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando funcionários por nome: {}", nome);
            List<FuncionarioResponseDTO> funcionarios = funcionarioService.buscarPorNome(nome);
            logger.info("Busca por nome '{}' retornou {} funcionários", nome, funcionarios.size());
            return ResponseEntity.ok(funcionarios);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/cargo")
    public ResponseEntity<List<FuncionarioResponseDTO>> buscarPorCargo(@RequestParam String cargo) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando funcionários por cargo: {}", cargo);
            List<FuncionarioResponseDTO> funcionarios = funcionarioService.buscarPorCargo(cargo);
            logger.info("Busca por cargo '{}' retornou {} funcionários", cargo, funcionarios.size());
            return ResponseEntity.ok(funcionarios);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/status")
    public ResponseEntity<List<FuncionarioResponseDTO>> buscarPorStatus(@RequestParam Boolean ativo) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Buscando funcionários por status: {}", ativo);
            List<FuncionarioResponseDTO> funcionarios = funcionarioService.buscarPorStatus(ativo);
            logger.info("Busca por status '{}' retornou {} funcionários", ativo, funcionarios.size());
            return ResponseEntity.ok(funcionarios);
        } finally {
            MDC.clear();
        }
    }

    @PostMapping
    public ResponseEntity<?> criarFuncionario(@Valid @RequestBody FuncionarioRequestDTO funcionarioRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Criando novo funcionário: {}", funcionarioRequest.nome());
            FuncionarioResponseDTO funcionarioCriado = funcionarioService.criar(funcionarioRequest);
            logger.info("Funcionário criado com sucesso. ID: {}, Nome: {}", 
                       funcionarioCriado.idFuncionario(), funcionarioCriado.nome());
            return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioCriado);
        } catch (Exception e) {
            logger.error("Erro ao criar funcionário: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarFuncionario(@PathVariable Long id, 
                                                @Valid @RequestBody FuncionarioRequestDTO funcionarioRequest) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Atualizando funcionário ID: {}", id);
            Optional<FuncionarioResponseDTO> funcionarioAtualizado = 
                funcionarioService.atualizar(id, funcionarioRequest);
            if (funcionarioAtualizado.isPresent()) {
                logger.info("Funcionário atualizado com sucesso. ID: {}", id);
                return ResponseEntity.ok(funcionarioAtualizado.get());
            } else {
                logger.warn("Funcionário não encontrado para atualização. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar funcionário ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<?> inativarFuncionario(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Inativando funcionário ID: {}", id);
            Optional<FuncionarioResponseDTO> funcionarioInativado = funcionarioService.inativarFuncionario(id);
            if (funcionarioInativado.isPresent()) {
                logger.info("Funcionário inativado com sucesso. ID: {}", id);
                return ResponseEntity.ok(funcionarioInativado.get());
            } else {
                logger.warn("Funcionário não encontrado para inativação. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao inativar funcionário ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativarFuncionario(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Ativando funcionário ID: {}", id);
            Optional<FuncionarioResponseDTO> funcionarioAtivado = funcionarioService.ativarFuncionario(id);
            if (funcionarioAtivado.isPresent()) {
                logger.info("Funcionário ativado com sucesso. ID: {}", id);
                return ResponseEntity.ok(funcionarioAtivado.get());
            } else {
                logger.warn("Funcionário não encontrado para ativação. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao ativar funcionário ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFuncionario(@PathVariable Long id) {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        
        try {
            logger.info("Deletando funcionário ID: {}", id);
            boolean deletado = funcionarioService.deletar(id);
            if (deletado) {
                logger.info("Funcionário deletado com sucesso. ID: {}", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Funcionário não encontrado para deleção. ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } finally {
            MDC.clear();
        }
    }
}