package com.paofresquim.service;

import com.paofresquim.dto.ClienteRequestDTO;
import com.paofresquim.dto.ClienteResponseDTO;
import com.paofresquim.entity.Cliente;
import com.paofresquim.exception.ConflitoDadosException;
import com.paofresquim.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService extends BaseService<Cliente, Long, ClienteRequestDTO, ClienteResponseDTO> {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    protected ClienteRepository getRepository() {
        return clienteRepository;
    }

    @Override
    protected ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
            cliente.getIdCliente(),
            cliente.getNome(),
            cliente.getEmail(),
            cliente.getTelefone(),
            cliente.getDataCadastro()
        );
    }

    @Override
    protected Cliente toEntity(ClienteRequestDTO requestDTO) {
        validarEmailUnico(null, requestDTO.email());
        
        Cliente cliente = new Cliente();
        cliente.setNome(requestDTO.nome());
        cliente.setEmail(requestDTO.email());
        cliente.setTelefone(requestDTO.telefone());
        return cliente;
    }

    @Override
    protected void updateEntityFromRequest(Cliente cliente, ClienteRequestDTO requestDTO) {
        validarEmailUnico(cliente.getIdCliente(), requestDTO.email());
        
        cliente.setNome(requestDTO.nome());
        cliente.setEmail(requestDTO.email());
        cliente.setTelefone(requestDTO.telefone());
    }

    @Override
    protected Long getIdFromEntity(Cliente entity) {
        return entity.getIdCliente();
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> buscarPorNome(String nome) {
        logger.debug("Buscando clientes por nome: {}", nome);
        return clienteRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void validarEmailUnico(Long idCliente, String email) {
        if (email != null && clienteRepository.existsByEmail(email)) {
            Optional<Cliente> clienteExistente = clienteRepository.findByEmail(email);
            if (clienteExistente.isPresent() && 
                !clienteExistente.get().getIdCliente().equals(idCliente)) {
                throw new ConflitoDadosException("Email já cadastrado: " + email);
            }
        }
    }
}