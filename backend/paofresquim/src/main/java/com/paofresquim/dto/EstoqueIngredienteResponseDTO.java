package com.paofresquim.dto;

import java.time.LocalDateTime;

public class EstoqueIngredienteResponseDTO {

    private Long idIngrediente;
    private String nomeIngrediente;
    private Double quantidadeEstoque;
    private String unidadeMedida;
    private Double estoqueMinimo;
    private Double custoMedio;
    private LocalDateTime dataAtualizacao;
    private Boolean precisaRepor;

    public EstoqueIngredienteResponseDTO(Long idIngrediente, String nomeIngrediente, Double quantidadeEstoque, 
                                       String unidadeMedida, Double estoqueMinimo, Double custoMedio, 
                                       LocalDateTime dataAtualizacao, Boolean precisaRepor) {
        this.idIngrediente = idIngrediente;
        this.nomeIngrediente = nomeIngrediente;
        this.quantidadeEstoque = quantidadeEstoque;
        this.unidadeMedida = unidadeMedida;
        this.estoqueMinimo = estoqueMinimo;
        this.custoMedio = custoMedio;
        this.dataAtualizacao = dataAtualizacao;
        this.precisaRepor = precisaRepor;
    }

    public Long getIdIngrediente() { return idIngrediente; }
    public String getNomeIngrediente() { return nomeIngrediente; }
    public Double getQuantidadeEstoque() { return quantidadeEstoque; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public Double getEstoqueMinimo() { return estoqueMinimo; }
    public Double getCustoMedio() { return custoMedio; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public Boolean getPrecisaRepor() { return precisaRepor; }
}