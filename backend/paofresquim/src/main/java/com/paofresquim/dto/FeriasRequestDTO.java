package com.paofresquim.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class FeriasRequestDTO {

    @NotNull(message = "ID do funcionário é obrigatório")
    private Long idFuncionario;

    @NotNull(message = "Data de início é obrigatória")
    private LocalDate dataInicio;

    @NotNull(message = "Data de fim é obrigatória")
    private LocalDate dataFim;

    private String observacoes;

    public FeriasRequestDTO() {}

    public FeriasRequestDTO(Long idFuncionario, LocalDate dataInicio, LocalDate dataFim, String observacoes) {
        this.idFuncionario = idFuncionario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.observacoes = observacoes;
    }

    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long idFuncionario) { this.idFuncionario = idFuncionario; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}