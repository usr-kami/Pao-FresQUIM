package com.paofresquim.repository;

import com.paofresquim.entity.EstoqueIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueIngredienteRepository extends JpaRepository<EstoqueIngrediente, Long> {
    
    Optional<EstoqueIngrediente> findByNomeIngrediente(String nomeIngrediente);
    
    List<EstoqueIngrediente> findByNomeIngredienteContainingIgnoreCase(String nome);
    
    List<EstoqueIngrediente> findByQuantidadeEstoqueLessThanEqual(Double quantidade);
    
    @Query("SELECT e FROM EstoqueIngrediente e WHERE e.quantidadeEstoque <= e.estoqueMinimo")
    List<EstoqueIngrediente> findByPrecisaReporTrue();
}