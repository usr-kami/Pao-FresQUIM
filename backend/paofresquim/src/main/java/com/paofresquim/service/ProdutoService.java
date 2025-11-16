package com.paofresquim.service;

import com.paofresquim.dto.ProdutoRequestDTO;
import com.paofresquim.dto.ProdutoResponseDTO;
import com.paofresquim.entity.Produto;
import com.paofresquim.exception.ConflitoDadosException;
import com.paofresquim.exception.RegraNegocioException;
import com.paofresquim.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService extends BaseService<Produto, Long, ProdutoRequestDTO, ProdutoResponseDTO> {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Override
    protected ProdutoRepository getRepository() {
        return produtoRepository;
    }

    @Override
    protected ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
            produto.getIdProduto(),
            produto.getNomeProduto(),
            produto.getPrecoKg()
        );
    }

    @Override
    protected Produto toEntity(ProdutoRequestDTO requestDTO) {
        validarNomeProdutoUnico(null, requestDTO.nomeProduto());
        
        Produto produto = new Produto();
        produto.setNomeProduto(requestDTO.nomeProduto());
        produto.setPrecoKg(requestDTO.precoKg());
        return produto;
    }

    @Override
    protected void updateEntityFromRequest(Produto produto, ProdutoRequestDTO requestDTO) {
        validarNomeProdutoUnico(produto.getIdProduto(), requestDTO.nomeProduto());
        
        produto.setNomeProduto(requestDTO.nomeProduto());
        produto.setPrecoKg(requestDTO.precoKg());
    }

    @Override
    protected Long getIdFromEntity(Produto entity) {
        return entity.getIdProduto();
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarPorNome(String nome) {
        logger.debug("Buscando produtos por nome: {}", nome);
        return produtoRepository.findByNomeProdutoContainingIgnoreCase(nome)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarPorFaixaPreco(Double precoMin, Double precoMax) {
        logger.debug("Buscando produtos por faixa de preço: {} - {}", precoMin, precoMax);
        return produtoRepository.findByPrecoKgBetween(precoMin, precoMax)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deletar(Long id) {
        logger.info("Tentando deletar produto ID: {}", id);
        if (produtoRepository.existsById(id)) {
            Produto produto = produtoRepository.findById(id).orElseThrow();
            if (!produto.getVendas().isEmpty()) {
                throw new RegraNegocioException("Não é possível deletar produto com vendas associadas");
            }
            produtoRepository.deleteById(id);
            logger.info("Produto deletado com ID: {}", id);
            return true;
        }
        logger.warn("Produto não encontrado para deleção ID: {}", id);
        return false;
    }

    private void validarNomeProdutoUnico(Long idProduto, String nomeProduto) {
        Optional<Produto> produtoExistente = produtoRepository.findByNomeProduto(nomeProduto);
        if (produtoExistente.isPresent() && 
            !produtoExistente.get().getIdProduto().equals(idProduto)) {
            throw new ConflitoDadosException("Produto já cadastrado: " + nomeProduto);
        }
    }
}