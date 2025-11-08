package com.paofresquim.repository;

import com.paofresquim.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    Optional<Produto> findByNomeProduto(String nomeProduto);
    
    List<Produto> findByNomeProdutoContainingIgnoreCase(String nome);
    
    List<Produto> findByPrecoKgBetween(Double precoMin, Double precoMax);
}