package com.paofresquim.repository;

import com.paofresquim.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    
    List<Venda> findByClienteIdCliente(Long idCliente);
    
    List<Venda> findByProdutoIdProduto(Long idProduto);
    
    List<Venda> findByDataVendaBetween(LocalDateTime inicio, LocalDateTime fim);
    
    List<Venda> findByStatusPagamento(String statusPagamento);
    
    List<Venda> findByFormaPagamento(String formaPagamento);
}