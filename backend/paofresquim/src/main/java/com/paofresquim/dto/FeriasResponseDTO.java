package com.paofresquim.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FeriasResponseDTO {

    private Long idFerias;
    private Long idFuncionario;
    private String nomeFuncionario;
    private String cargoFuncionario;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer diasSolicitados;
    private String status;
    private LocalDateTime dataSolicitacao;
    private String observacoes;

    public FeriasResponseDTO(Long idFerias, Long idFuncionario, String nomeFuncionario, String cargoFuncionario,
                           LocalDate dataInicio, LocalDate dataFim, Integer diasSolicitados, String status,
                           LocalDateTime dataSolicitacao, String observacoes) {
        this.idFerias = idFerias;
        this.idFuncionario = idFuncionario;
        this.nomeFuncionario = nomeFuncionario;
        this.cargoFuncionario = cargoFuncionario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.diasSolicitados = diasSolicitados;
        this.status = status;
        this.dataSolicitacao = dataSolicitacao;
        this.observacoes = observacoes;
    }

    public Long getIdFerias() { return idFerias; }
    public Long getIdFuncionario() { return idFuncionario; }
    public String getNomeFuncionario() { return nomeFuncionario; }
    public String getCargoFuncionario() { return cargoFuncionario; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public Integer getDiasSolicitados() { return diasSolicitados; }
    public String getStatus() { return status; }
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public String getObservacoes() { return observacoes; }
}