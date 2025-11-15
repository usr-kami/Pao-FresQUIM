package com.paofresquim.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "expediente_funcionario")
public class ExpedienteFuncionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_expediente")
    private Long idExpediente;

    @NotNull(message = "Funcionário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario", nullable = false)
    private Funcionario funcionario;

    @Column(name = "dia_semana")
    private String diaSemana;

    @NotNull(message = "Hora de entrada é obrigatória")
    @Column(name = "hora_entrada", nullable = false)
    private String horaEntrada;

    @NotNull(message = "Hora de saída é obrigatória")
    @Column(name = "hora_saida", nullable = false)
    private String horaSaida;

    @Column(name = "turno")
    private String turno = "manha";

    public ExpedienteFuncionario() {}

    public ExpedienteFuncionario(Funcionario funcionario, String diaSemana, String horaEntrada, String horaSaida, String turno) {
        this.funcionario = funcionario;
        this.diaSemana = diaSemana;
        this.horaEntrada = horaEntrada;
        this.horaSaida = horaSaida;
        this.turno = turno;
    }

    public Long getIdExpediente() { return idExpediente; }
    public void setIdExpediente(Long idExpediente) { this.idExpediente = idExpediente; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public String getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }

    public String getHoraSaida() { return horaSaida; }
    public void setHoraSaida(String horaSaida) { this.horaSaida = horaSaida; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    @Override
    public String toString() {
        return "ExpedienteFuncionario{" +
                "idExpediente=" + idExpediente +
                ", funcionario=" + (funcionario != null ? funcionario.getNome() : "N/A") +
                ", diaSemana='" + diaSemana + '\'' +
                ", horaEntrada='" + horaEntrada + '\'' +
                ", horaSaida='" + horaSaida + '\'' +
                ", turno='" + turno + '\'' +
                '}';
    }
}