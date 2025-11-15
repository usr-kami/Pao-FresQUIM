package com.paofresquim.service;

import com.paofresquim.dto.FuncionarioRequestDTO;
import com.paofresquim.dto.FuncionarioResponseDTO;
import com.paofresquim.entity.Funcionario;
import com.paofresquim.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> listarTodos() {
        return funcionarioRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<FuncionarioResponseDTO> buscarPorId(Long id) {
        return funcionarioRepository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> buscarPorNome(String nome) {
        return funcionarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> buscarPorCargo(String cargo) {
        return funcionarioRepository.findByCargo(cargo)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponseDTO> buscarPorStatus(Boolean ativo) {
        return funcionarioRepository.findByAtivo(ativo)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FuncionarioResponseDTO criarFuncionario(FuncionarioRequestDTO funcionarioRequest) {

        if (funcionarioRequest.getEmail() != null && 
            funcionarioRepository.existsByEmail(funcionarioRequest.getEmail())) {
            throw new RuntimeException("Email já cadastrado: " + funcionarioRequest.getEmail());
        }


        if (!isCargoValido(funcionarioRequest.getCargo())) {
            throw new RuntimeException("Cargo inválido: " + funcionarioRequest.getCargo() + 
                                    ". Cargos válidos: padeiro, atendente, gerente, auxiliar");
        }

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(funcionarioRequest.getNome());
        funcionario.setTelefone(funcionarioRequest.getTelefone());
        funcionario.setEmail(funcionarioRequest.getEmail());
        funcionario.setCargo(funcionarioRequest.getCargo());
        funcionario.setSalarioBase(funcionarioRequest.getSalarioBase());
        funcionario.setAtivo(funcionarioRequest.getAtivo());

        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);
        return toResponseDTO(funcionarioSalvo);
    }

    @Transactional
    public Optional<FuncionarioResponseDTO> atualizarFuncionario(Long id, FuncionarioRequestDTO funcionarioRequest) {
        return funcionarioRepository.findById(id)
                .map(funcionario -> {

                    if (funcionarioRequest.getEmail() != null && 
                        !funcionarioRequest.getEmail().equals(funcionario.getEmail()) &&
                        funcionarioRepository.existsByEmail(funcionarioRequest.getEmail())) {
                        throw new RuntimeException("Email já cadastrado: " + funcionarioRequest.getEmail());
                    }


                    if (!isCargoValido(funcionarioRequest.getCargo())) {
                        throw new RuntimeException("Cargo inválido: " + funcionarioRequest.getCargo() + 
                                                ". Cargos válidos: padeiro, atendente, gerente, auxiliar");
                    }

                    funcionario.setNome(funcionarioRequest.getNome());
                    funcionario.setTelefone(funcionarioRequest.getTelefone());
                    funcionario.setEmail(funcionarioRequest.getEmail());
                    funcionario.setCargo(funcionarioRequest.getCargo());
                    funcionario.setSalarioBase(funcionarioRequest.getSalarioBase());
                    funcionario.setAtivo(funcionarioRequest.getAtivo());
                    
                    Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionario);
                    return toResponseDTO(funcionarioAtualizado);
                });
    }

    @Transactional
    public boolean deletarFuncionario(Long id) {
        if (funcionarioRepository.existsById(id)) {
            funcionarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<FuncionarioResponseDTO> inativarFuncionario(Long id) {
        return funcionarioRepository.findById(id)
                .map(funcionario -> {
                    funcionario.setAtivo(false);
                    Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionario);
                    return toResponseDTO(funcionarioAtualizado);
                });
    }

    @Transactional
    public Optional<FuncionarioResponseDTO> ativarFuncionario(Long id) {
        return funcionarioRepository.findById(id)
                .map(funcionario -> {
                    funcionario.setAtivo(true);
                    Funcionario funcionarioAtualizado = funcionarioRepository.save(funcionario);
                    return toResponseDTO(funcionarioAtualizado);
                });
    }

    private boolean isCargoValido(String cargo) {
        return cargo != null && 
               (cargo.equals("padeiro") || cargo.equals("atendente") || 
                cargo.equals("gerente") || cargo.equals("auxiliar"));
    }

    private FuncionarioResponseDTO toResponseDTO(Funcionario funcionario) {
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
}