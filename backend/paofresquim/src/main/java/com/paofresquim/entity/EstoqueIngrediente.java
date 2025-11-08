package com.paofresquim.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Table(name = "estoque_ingredientes")
public class EstoqueIngrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ingrediente")
    private Long idIngrediente;

    @NotBlank(message = "Nome do ingrediente é obrigatório")
    @Column(name = "nome_ingrediente", nullable = false)
    private String nomeIngrediente;

    @Positive(message = "Quantidade em estoque deve ser positiva")
    @Column(name = "quantidade_estoque", nullable = false)
    private Double quantidadeEstoque;

    @Column(name = "unidade_medida")
    private String unidadeMedida = "kg";

    @Column(name = "estoque_minimo")
    private Double estoqueMinimo = 0.0;

    @Column(name = "custo_medio")
    private Double custoMedio = 0.0;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    public EstoqueIngrediente() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public EstoqueIngrediente(String nomeIngrediente, Double quantidadeEstoque, String unidadeMedida) {
        this();
        this.nomeIngrediente = nomeIngrediente;
        this.quantidadeEstoque = quantidadeEstoque;
        this.unidadeMedida = unidadeMedida;
    }

    public Long getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(Long idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public String getNomeIngrediente() {
        return nomeIngrediente;
    }

    public void setNomeIngrediente(String nomeIngrediente) {
        this.nomeIngrediente = nomeIngrediente;
    }

    public Double getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Double quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public Double getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(Double estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public Double getCustoMedio() {
        return custoMedio;
    }

    public void setCustoMedio(Double custoMedio) {
        this.custoMedio = custoMedio;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Boolean precisaRepor() {
        return quantidadeEstoque <= estoqueMinimo;
    }

    @Override
    public String toString() {
        return "EstoqueIngrediente{" +
                "idIngrediente=" + idIngrediente +
                ", nomeIngrediente='" + nomeIngrediente + '\'' +
                ", quantidadeEstoque=" + quantidadeEstoque +
                ", unidadeMedida='" + unidadeMedida + '\'' +
                ", estoqueMinimo=" + estoqueMinimo +
                ", custoMedio=" + custoMedio +
                ", dataAtualizacao=" + dataAtualizacao +
                '}';
    }
}