package com.paofresquim.service;

import com.paofresquim.dto.EstoqueIngredienteRequestDTO;
import com.paofresquim.dto.EstoqueIngredienteResponseDTO;
import com.paofresquim.entity.EstoqueIngrediente;
import com.paofresquim.exception.ConflitoDadosException;
import com.paofresquim.exception.ValidacaoException;
import com.paofresquim.repository.EstoqueIngredienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstoqueIngredienteService extends BaseService<EstoqueIngrediente, Long, EstoqueIngredienteRequestDTO, EstoqueIngredienteResponseDTO> {

    @Autowired
    private EstoqueIngredienteRepository estoqueIngredienteRepository;

    @Override
    protected EstoqueIngredienteRepository getRepository() {
        return estoqueIngredienteRepository;
    }

    @Override
    protected EstoqueIngredienteResponseDTO toResponseDTO(EstoqueIngrediente ingrediente) {
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

    @Override
    protected EstoqueIngrediente toEntity(EstoqueIngredienteRequestDTO requestDTO) {
        validarNomeIngredienteUnico(null, requestDTO.nomeIngrediente());
        
        EstoqueIngrediente ingrediente = new EstoqueIngrediente();
        ingrediente.setNomeIngrediente(requestDTO.nomeIngrediente());
        ingrediente.setQuantidadeEstoque(requestDTO.quantidadeEstoque());
        ingrediente.setUnidadeMedida(requestDTO.unidadeMedida());
        ingrediente.setEstoqueMinimo(requestDTO.estoqueMinimo());
        ingrediente.setCustoMedio(requestDTO.custoMedio());
        return ingrediente;
    }

    @Override
    protected void updateEntityFromRequest(EstoqueIngrediente ingrediente, EstoqueIngredienteRequestDTO requestDTO) {
        validarNomeIngredienteUnico(ingrediente.getIdIngrediente(), requestDTO.nomeIngrediente());
        
        ingrediente.setNomeIngrediente(requestDTO.nomeIngrediente());
        ingrediente.setQuantidadeEstoque(requestDTO.quantidadeEstoque());
        ingrediente.setUnidadeMedida(requestDTO.unidadeMedida());
        ingrediente.setEstoqueMinimo(requestDTO.estoqueMinimo());
        ingrediente.setCustoMedio(requestDTO.custoMedio());
    }

    @Override
    protected Long getIdFromEntity(EstoqueIngrediente entity) {
        return entity.getIdIngrediente();
    }

    @Transactional(readOnly = true)
    public List<EstoqueIngredienteResponseDTO> buscarPorNome(String nome) {
        logger.debug("Buscando ingredientes por nome: {}", nome);
        return estoqueIngredienteRepository.findByNomeIngredienteContainingIgnoreCase(nome)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EstoqueIngredienteResponseDTO> buscarPorEstoqueMinimo() {
        logger.debug("Buscando ingredientes com estoque mínimo");
        return estoqueIngredienteRepository.findByQuantidadeEstoqueLessThanEqual(0.0)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EstoqueIngredienteResponseDTO> buscarPrecisaRepor() {
        logger.debug("Buscando ingredientes que precisam de reposição");
        return estoqueIngredienteRepository.findByPrecisaReporTrue()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<EstoqueIngredienteResponseDTO> atualizarQuantidade(Long id, Double novaQuantidade) {
        logger.info("Atualizando quantidade do ingrediente ID: {} para {}", id, novaQuantidade);
        return estoqueIngredienteRepository.findById(id)
                .map(ingrediente -> {
                    if (novaQuantidade < 0) {
                        throw new ValidacaoException("Quantidade não pode ser negativa");
                    }
                    ingrediente.setQuantidadeEstoque(novaQuantidade);
                    EstoqueIngrediente ingredienteAtualizado = estoqueIngredienteRepository.save(ingrediente);
                    logger.info("Quantidade atualizada para ingrediente ID: {}", id);
                    return toResponseDTO(ingredienteAtualizado);
                });
    }

    @Transactional
    public Optional<EstoqueIngredienteResponseDTO> atualizarCusto(Long id, Double novoCusto) {
        logger.info("Atualizando custo do ingrediente ID: {} para {}", id, novoCusto);
        return estoqueIngredienteRepository.findById(id)
                .map(ingrediente -> {
                    if (novoCusto < 0) {
                        throw new ValidacaoException("Custo não pode ser negativo");
                    }
                    ingrediente.setCustoMedio(novoCusto);
                    EstoqueIngrediente ingredienteAtualizado = estoqueIngredienteRepository.save(ingrediente);
                    logger.info("Custo atualizado para ingrediente ID: {}", id);
                    return toResponseDTO(ingredienteAtualizado);
                });
    }

    private void validarNomeIngredienteUnico(Long idIngrediente, String nomeIngrediente) {
        Optional<EstoqueIngrediente> ingredienteExistente = estoqueIngredienteRepository.findByNomeIngrediente(nomeIngrediente);
        if (ingredienteExistente.isPresent() && 
            !ingredienteExistente.get().getIdIngrediente().equals(idIngrediente)) {
            throw new ConflitoDadosException("Ingrediente já cadastrado: " + nomeIngrediente);
        }
    }
}