package com.paofresquim.repository;

import com.paofresquim.entity.ExpedienteFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpedienteRepository extends JpaRepository<ExpedienteFuncionario, Long> {
    
    List<ExpedienteFuncionario> findByFuncionarioIdFuncionario(Long idFuncionario);
    
    List<ExpedienteFuncionario> findByDiaSemana(String diaSemana);
    
    List<ExpedienteFuncionario> findByTurno(String turno);
    
    List<ExpedienteFuncionario> findByFuncionarioIdFuncionarioAndDiaSemana(Long idFuncionario, String diaSemana);
}