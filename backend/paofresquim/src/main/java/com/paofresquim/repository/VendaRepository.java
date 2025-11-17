package com.paofresquim.repository;

import com.paofresquim.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    
    List<Venda> findByClienteIdCliente(Long idCliente);
    
    List<Venda> findByProdutoIdProduto(Long idProduto);
    
    List<Venda> findByDataVendaBetween(LocalDateTime inicio, LocalDateTime fim);
    
    List<Venda> findByStatusPagamento(String statusPagamento);
    
    List<Venda> findByFormaPagamento(String formaPagamento);

    @Query("SELECT v.produto.idProduto, v.produto.nomeProduto, " +
           "SUM(v.pesoVendido) as quantidadeTotal, " +
           "SUM(v.total) as totalVendas, " +
           "COUNT(v) as numeroVendas " +
           "FROM Venda v " +
           "WHERE v.dataVenda BETWEEN :inicio AND :fim " +
           "GROUP BY v.produto.idProduto, v.produto.nomeProduto " +
           "ORDER BY totalVendas DESC")
    List<Object[]> findVendasResumidasPorPeriodo(@Param("inicio") LocalDateTime inicio, 
                                               @Param("fim") LocalDateTime fim);

    @Query("SELECT v.produto.idProduto, v.produto.nomeProduto, " +
           "SUM(v.pesoVendido) as quantidadeTotal, " +
           "SUM(v.total) as totalVendas, " +
           "COUNT(v) as numeroVendas " +
           "FROM Venda v " +
           "GROUP BY v.produto.idProduto, v.produto.nomeProduto " +
           "ORDER BY totalVendas DESC")
    List<Object[]> findProdutosMaisVendidos();

    default List<Object[]> findProdutosMaisVendidos(int limit) {
        List<Object[]> todos = findProdutosMaisVendidos();
        return todos.stream().limit(limit).collect(Collectors.toList());
    }

    @Query("SELECT v.cliente.idCliente, v.cliente.nome, " +
           "SUM(v.total) as totalCompras, " +
           "COUNT(v) as numeroCompras, " +
           "AVG(v.total) as ticketMedio " +
           "FROM Venda v " +
           "WHERE v.cliente IS NOT NULL " +
           "GROUP BY v.cliente.idCliente, v.cliente.nome " +
           "ORDER BY totalCompras DESC")
    List<Object[]> findClientesTop();

    default List<Object[]> findClientesTop(int limit) {
        List<Object[]> todos = findClientesTop();
        return todos.stream().limit(limit).collect(Collectors.toList());
    }
}