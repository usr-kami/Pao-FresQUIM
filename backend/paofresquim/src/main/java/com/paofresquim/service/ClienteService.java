package com.paofresquim.service;

import com.paofresquim.dto.ClienteRequestDTO;
import com.paofresquim.dto.ClienteResponseDTO;
import com.paofresquim.entity.Cliente;
import com.paofresquim.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ClienteResponseDTO> buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClienteResponseDTO criarCliente(ClienteRequestDTO clienteRequest) {

        if (clienteRequest.getEmail() != null && 
            clienteRepository.existsByEmail(clienteRequest.getEmail())) {
            throw new RuntimeException("Email já cadastrado: " + clienteRequest.getEmail());
        }

        Cliente cliente = new Cliente();
        cliente.setNome(clienteRequest.getNome());
        cliente.setEmail(clienteRequest.getEmail());
        cliente.setTelefone(clienteRequest.getTelefone());

        Cliente clienteSalvo = clienteRepository.save(cliente);
        return toResponseDTO(clienteSalvo);
    }

    @Transactional
    public Optional<ClienteResponseDTO> atualizarCliente(Long id, ClienteRequestDTO clienteRequest) {
        return clienteRepository.findById(id)
                .map(cliente -> {

                    if (clienteRequest.getEmail() != null && 
                        !clienteRequest.getEmail().equals(cliente.getEmail()) &&
                        clienteRepository.existsByEmail(clienteRequest.getEmail())) {
                        throw new RuntimeException("Email já cadastrado: " + clienteRequest.getEmail());
                    }

                    cliente.setNome(clienteRequest.getNome());
                    cliente.setEmail(clienteRequest.getEmail());
                    cliente.setTelefone(clienteRequest.getTelefone());
                    
                    Cliente clienteAtualizado = clienteRepository.save(cliente);
                    return toResponseDTO(clienteAtualizado);
                });
    }

    @Transactional
    public boolean deletarCliente(Long id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
            cliente.getIdCliente(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getDataCadastro()
        );
    }
}