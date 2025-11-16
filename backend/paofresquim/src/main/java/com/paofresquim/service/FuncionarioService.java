package com.paofresquim.service;

import com.paofresquim.dto.FuncionarioRequestDTO;
import com.paofresquim.dto.FuncionarioResponseDTO;
import com.paofresquim.entity.Funcionario;
import com.paofresquim.exception.ConflitoDadosException;
import com.paofresquim.exception.ValidacaoException;
import com.paofresquim.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FuncionarioService extends BaseService<Funcionario, Long, FuncionarioRequestDTO, FuncionarioResponseDTO> {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Override
    protected FuncionarioRepository getRepository() {
        return funcionarioRepository;
    }

    @Override
    protected FuncionarioResponseDTO toResponseDTO(Funcionario funcionario) {
        return new FuncionarioResponseDTO(
            funcionario.getIdFuncionario(),
            funcionario.getNome(),
            funcionario.getTelefone(),
            funcionario.getEmail(),
            funcionario.getCargo(),
            funcionario.getSalarioBase(),
            funcionario.getDataAdmissao(),
            funcionario.getAtivo()
        );
    }

    @Override
    protected Funcionario toEntity(FuncionarioRequestDTO requestDTO) {
        validarEmailUnico(null, requestDTO.email());
        validarCargo(requestDTO.cargo());
        
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(requestDTO.nome());
        funcionario.setTelefone(requestDTO.telefone());
        funcionario.setEmail(requestDTO.email());
        funcionario.setCargo(requestDTO.cargo());
        funcionario.setSalarioBase(requestDTO.salarioBase());
        funcionario.setAtivo(requestDTO.ativo());
        return funcionario;
    }

    @Override
    protected void updateEntityFromRequest(Funcionario funcionario, FuncionarioRequestDTO requestDTO) {
        validarEmailUnico(funcionario.getIdFuncionario(), requestDTO.email());
        validarCargo(requestDTO.cargo());
        
        funcionario.setNome(requestDTO.nome());
        funcionario.setTelefone(requestDTO.telefone());
        funcionario.setEmail(requestDTO.email());
        funcionario.setCargo(requestDTO.cargo());
        funcionario.setSalarioBase(requestDTO.salarioBase());
        funcionario.setAtivo(requestDTO.ativo());
    }

    @Override
    protected Long getIdFromEntity(Funcionario entity) {
        return entity.getIdFuncionario();
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> buscarPorNome(String nome) {
        logger.debug("Buscando funcionários por nome: {}", nome);
        return funcionarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> buscarPorCargo(String cargo) {
        logger.debug("Buscando funcionários por cargo: {}", cargo);
        return funcionarioRepository.findByCargo(cargo)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> buscarPorStatus(Boolean ativo) {
        logger.debug("Buscando funcionários por status: {}", ativo);
        return funcionarioRepository.findByAtivo(ativo)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<FuncionarioResponseDTO> inativarFuncionario(Long id) {
        logger.info("Inativando funcionário ID: {}", id);
        return funcionarioRepository.findById(id)
                .map(funcionario -> {
                    funcionario.setAtivo(false);
                    Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionario);
                    logger.info("Funcionário inativado com ID: {}", id);
                    return toResponseDTO(funcionarioAtualizado);
                });
    }

    @Transactional
    public Optional<FuncionarioResponseDTO> ativarFuncionario(Long id) {
        logger.info("Ativando funcionário ID: {}", id);
        return funcionarioRepository.findById(id)
                .map(funcionario -> {
                    funcionario.setAtivo(true);
                    Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionario);
                    logger.info("Funcionário ativado com ID: {}", id);
                    return toResponseDTO(funcionarioAtualizado);
                });
    }

    private void validarEmailUnico(Long idFuncionario, String email) {
        if (email != null && funcionarioRepository.existsByEmail(email)) {
            Optional<Funcionario> funcionarioExistente = funcionarioRepository.findByEmail(email);
            if (funcionarioExistente.isPresent() && 
                !funcionarioExistente.get().getIdFuncionario().equals(idFuncionario)) {
                throw new ConflitoDadosException("Email já cadastrado: " + email);
            }
        }
    }

    private void validarCargo(String cargo) {
        if (!isCargoValido(cargo)) {
            throw new ValidacaoException("Cargo inválido: " + cargo + 
                    ". Cargos válidos: padeiro, atendente, gerente, auxiliar");
        }
    }

    private boolean isCargoValido(String cargo) {
        return cargo != null && 
               (cargo.equals("padeiro") || cargo.equals("atendente") || 
                cargo.equals("gerente") || cargo.equals("auxiliar"));
    }
}