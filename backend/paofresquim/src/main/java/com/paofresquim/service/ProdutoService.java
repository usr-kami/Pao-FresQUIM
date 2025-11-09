package com.paofresquim.service;

import com.paofresquim.dto.ProdutoRequestDTO;
import com.paofresquim.dto.ProdutoResponseDTO;
import com.paofresquim.entity.Produto;
import com.paofresquim.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProdutoResponseDTO> buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarPorNome(String nome) {
        return produtoRepository.findByNomeProdutoContainingIgnoreCase(nome)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarPorFaixaPreco(Double precoMin, Double precoMax) {
        return produtoRepository.findByPrecoKgBetween(precoMin, precoMax)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoRequest) {
        
        if (produtoRepository.findByNomeProduto(produtoRequest.getNomeProduto()).isPresent()) {
            throw new RuntimeException("Produto já cadastrado: " + produtoRequest.getNomeProduto());
        }

        Produto produto = new Produto();
        produto.setNomeProduto(produtoRequest.getNomeProduto());
        produto.setPrecoKg(produtoRequest.getPrecoKg());

        Produto produtoSalvo = produtoRepository.save(produto);
        return toResponseDTO(produtoSalvo);
    }

    @Transactional
    public Optional<ProdutoResponseDTO> atualizarProduto(Long id, ProdutoRequestDTO produtoRequest) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    
                    Optional<Produto> produtoComMesmoNome = produtoRepository.findByNomeProduto(produtoRequest.getNomeProduto());
                    if (produtoComMesmoNome.isPresent() && !produtoComMesmoNome.get().getIdProduto().equals(id)) {
                        throw new RuntimeException("Produto já cadastrado: " + produtoRequest.getNomeProduto());
                    }

                    produto.setNomeProduto(produtoRequest.getNomeProduto());
                    produto.setPrecoKg(produtoRequest.getPrecoKg());
                    
                    Produto produtoAtualizado = produtoRepository.save(produto);
                    return toResponseDTO(produtoAtualizado);
                });
    }

    @Transactional
    public boolean deletarProduto(Long id) {
        if (produtoRepository.existsById(id)) {

            Produto produto = produtoRepository.findById(id).orElseThrow();
            if (!produto.getVendas().isEmpty()) {
                throw new RuntimeException("Não é possível deletar produto com vendas associadas");
            }
            
            produtoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
            produto.getIdProduto(),
            produto.getNomeProduto(),
            produto.getPrecoKg()
        );
    }
}