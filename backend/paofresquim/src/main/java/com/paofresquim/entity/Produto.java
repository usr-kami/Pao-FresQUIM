package com.paofresquim.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;

    @NotBlank(message = "Nome do produto é obrigatório")
    @Column(name = "nome_produto", nullable = false)
    private String nomeProduto;

    @Positive(message = "Preço por kg deve ser positivo")
    @Column(name = "preco_kg", nullable = false)
    private Double precoKg;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<Venda> vendas = new ArrayList<>();

    public Produto() {}

    public Produto(String nomeProduto, Double precoKg) {
        this.nomeProduto = nomeProduto;
        this.precoKg = precoKg;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Double getPrecoKg() {
        return precoKg;
    }

    public void setPrecoKg(Double precoKg) {
        this.precoKg = precoKg;
    }

    public List<Venda> getVendas() {
        return vendas;
    }

    public void setVendas(List<Venda> vendas) {
        this.vendas = vendas;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "idProduto=" + idProduto +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", precoKg=" + precoKg +
                '}';
    }
}