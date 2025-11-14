package com.paofresquim.service;

import com.paofresquim.dto.EstoqueIngredienteRequestDTO;
import com.paofresquim.dto.EstoqueIngredienteResponseDTO;
import com.paofresquim.entity.EstoqueIngrediente;
import com.paofresquim.repository.EstoqueIngredienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstoqueIngredienteService {

    @Autowired
    private EstoqueIngredienteRepository estoqueIngredienteRepository;

    @Transactional(readOnly = true)
    public List<EstoqueIngredienteResponseDTO> listarTodos() {
        return estoqueIngredienteRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EstoqueIngredienteResponseDTO> buscarPorId(Long id) {
        return estoqueIngredienteRepository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<EstoqueIngredienteResponseDTO> buscarPorNome(String nome) {
        return estoqueIngredienteRepository.findByNomeIngredienteContainingIgnoreCase(nome)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EstoqueIngredienteResponseDTO> buscarPorEstoqueMinimo() {
        return estoqueIngredienteRepository.findByQuantidadeEstoqueLessThanEqual(0.0)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EstoqueIngredienteResponseDTO> buscarPrecisaRepor() {
        return estoqueIngredienteRepository.findByPrecisaReporTrue()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EstoqueIngredienteResponseDTO criarIngrediente(EstoqueIngredienteRequestDTO ingredienteRequest) {
        
        if (estoqueIngredienteRepository.findByNomeIngrediente(ingredienteRequest.getNomeIngrediente()).isPresent()) {
            throw new RuntimeException("Ingrediente já cadastrado: " + ingredienteRequest.getNomeIngrediente());
        }

        EstoqueIngrediente ingrediente = new EstoqueIngrediente();
        ingrediente.setNomeIngrediente(ingredienteRequest.getNomeIngrediente());
        ingrediente.setQuantidadeEstoque(ingredienteRequest.getQuantidadeEstoque());
        ingrediente.setUnidadeMedida(ingredienteRequest.getUnidadeMedida());
        ingrediente.setEstoqueMinimo(ingredienteRequest.getEstoqueMinimo());
        ingrediente.setCustoMedio(ingredienteRequest.getCustoMedio());

        EstoqueIngrediente ingredienteSalvo = estoqueIngredienteRepository.save(ingrediente);
        return toResponseDTO(ingredienteSalvo);
    }

    @Transactional
    public Optional<EstoqueIngredienteResponseDTO> atualizarIngrediente(Long id, EstoqueIngredienteRequestDTO ingredienteRequest) {
        return estoqueIngredienteRepository.findById(id)
                .map(ingrediente -> {
                    
                    Optional<EstoqueIngrediente> ingredienteComMesmoNome = 
                        estoqueIngredienteRepository.findByNomeIngrediente(ingredienteRequest.getNomeIngrediente());
                    if (ingredienteComMesmoNome.isPresent() && !ingredienteComMesmoNome.get().getIdIngrediente().equals(id)) {
                        throw new RuntimeException("Ingrediente já cadastrado: " + ingredienteRequest.getNomeIngrediente());
                    }

                    ingrediente.setNomeIngrediente(ingredienteRequest.getNomeIngrediente());
                    ingrediente.setQuantidadeEstoque(ingredienteRequest.getQuantidadeEstoque());
                    ingrediente.setUnidadeMedida(ingredienteRequest.getUnidadeMedida());
                    ingrediente.setEstoqueMinimo(ingredienteRequest.getEstoqueMinimo());
                    ingrediente.setCustoMedio(ingredienteRequest.getCustoMedio());
                    
                    EstoqueIngrediente ingredienteAtualizado = estoqueIngredienteRepository.save(ingrediente);
                    return toResponseDTO(ingredienteAtualizado);
                });
    }

    @Transactional
    public Optional<EstoqueIngredienteResponseDTO> atualizarQuantidade(Long id, Double novaQuantidade) {
        return estoqueIngredienteRepository.findById(id)
                .map(ingrediente -> {
                    if (novaQuantidade < 0) {
                        throw new RuntimeException("Quantidade não pode ser negativa");
                    }
                    ingrediente.setQuantidadeEstoque(novaQuantidade);
                    EstoqueIngrediente ingredienteAtualizado = estoqueIngredienteRepository.save(ingrediente);
                    return toResponseDTO(ingredienteAtualizado);
                });
    }

    @Transactional
    public Optional<EstoqueIngredienteResponseDTO> atualizarCusto(Long id, Double novoCusto) {
        return estoqueIngredienteRepository.findById(id)
                .map(ingrediente -> {
                    if (novoCusto < 0) {
                        throw new RuntimeException("Custo não pode ser negativo");
                    }
                    ingrediente.setCustoMedio(novoCusto);
                    EstoqueIngrediente ingredienteAtualizado = estoqueIngredienteRepository.save(ingrediente);
                    return toResponseDTO(ingredienteAtualizado);
                });
    }

    @Transactional
    public boolean deletarIngrediente(Long id) {
        if (estoqueIngredienteRepository.existsById(id)) {
            estoqueIngredienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private EstoqueIngredienteResponseDTO toResponseDTO(EstoqueIngrediente ingrediente) {
        return new EstoqueIngredienteResponseDTO(
            ingrediente.getIdIngrediente(),
            ingrediente.getNomeIngrediente(),
            ingrediente.getQuantidadeEstoque(),
            ingrediente.getUnidadeMedida(),
            ingrediente.getEstoqueMinimo(),
            ingrediente.getCustoMedio(),
            ingrediente.getDataAtualizacao(),
            ingrediente.precisaRepor()
        );
    }
}