package com.paofresquim.repository;

import com.paofresquim.entity.FeriasFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeriasRepository extends JpaRepository<FeriasFuncionario, Long> {
    
    List<FeriasFuncionario> findByFuncionarioIdFuncionario(Long idFuncionario);
    
    List<FeriasFuncionario> findByStatus(String status);
    
    List<FeriasFuncionario> findByDataInicioBetween(LocalDate inicio, LocalDate fim);
    
    List<FeriasFuncionario> findByDataFimBetween(LocalDate inicio, LocalDate fim);
    
    List<FeriasFuncionario> findByFuncionarioIdFuncionarioAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            Long idFuncionario, LocalDate dataFim, LocalDate dataInicio);
}