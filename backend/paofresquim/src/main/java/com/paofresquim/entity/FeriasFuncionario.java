package com.paofresquim.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ferias_funcionarios")
public class FeriasFuncionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ferias")
    private Long idFerias;

    @NotNull(message = "Funcionário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario", nullable = false)
    private Funcionario funcionario;

    @NotNull(message = "Data de início é obrigatória")
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @NotNull(message = "Data de fim é obrigatória")
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @NotNull(message = "Dias solicitados é obrigatório")
    @Column(name = "dias_solicitados", nullable = false)
    private Integer diasSolicitados;

    @Column(name = "status")
    private String status = "solicitado";

    @Column(name = "data_solicitacao")
    private LocalDateTime dataSolicitacao;

    @Column(name = "observacoes")
    private String observacoes;

    public FeriasFuncionario() {
        this.dataSolicitacao = LocalDateTime.now();
    }

    public FeriasFuncionario(Funcionario funcionario, LocalDate dataInicio, LocalDate dataFim, String observacoes) {
        this();
        this.funcionario = funcionario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.observacoes = observacoes;
        calcularDiasSolicitados();
    }

    public void calcularDiasSolicitados() {
        if (this.dataInicio != null && this.dataFim != null) {
            this.diasSolicitados = (int) java.time.temporal.ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        }
    }

    public Long getIdFerias() { return idFerias; }
    public void setIdFerias(Long idFerias) { this.idFerias = idFerias; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { 
        this.dataInicio = dataInicio; 
        calcularDiasSolicitados();
    }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { 
        this.dataFim = dataFim; 
        calcularDiasSolicitados();
    }

    public Integer getDiasSolicitados() { return diasSolicitados; }
    public void setDiasSolicitados(Integer diasSolicitados) { this.diasSolicitados = diasSolicitados; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public String toString() {
        return "FeriasFuncionario{" +
                "idFerias=" + idFerias +
                ", funcionario=" + (funcionario != null ? funcionario.getNome() : "N/A") +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", diasSolicitados=" + diasSolicitados +
                ", status='" + status + '\'' +
                ", dataSolicitacao=" + dataSolicitacao +
                ", observacoes='" + observacoes + '\'' +
                '}';
    }
}