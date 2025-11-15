package com.paofresquim.repository;

import com.paofresquim.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    
    Optional<Funcionario> findByEmail(String email);
    
    List<Funcionario> findByNomeContainingIgnoreCase(String nome);
    
    List<Funcionario> findByCargo(String cargo);
    
    List<Funcionario> findByAtivo(Boolean ativo);
    
    boolean existsByEmail(String email);
}