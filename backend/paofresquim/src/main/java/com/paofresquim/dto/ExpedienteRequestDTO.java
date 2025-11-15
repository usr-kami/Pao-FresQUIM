package com.paofresquim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExpedienteRequestDTO {

    @NotNull(message = "ID do funcionário é obrigatório")
    private Long idFuncionario;

    @NotBlank(message = "Dia da semana é obrigatório")
    private String diaSemana;

    @NotBlank(message = "Hora de entrada é obrigatória")
    private String horaEntrada;

    @NotBlank(message = "Hora de saída é obrigatória")
    private String horaSaida;

    private String turno = "manha";

    public ExpedienteRequestDTO() {}

    public ExpedienteRequestDTO(Long idFuncionario, String diaSemana, String horaEntrada, String horaSaida, String turno) {
        this.idFuncionario = idFuncionario;
        this.diaSemana = diaSemana;
        this.horaEntrada = horaEntrada;
        this.horaSaida = horaSaida;
        this.turno = turno;
    }

    public Long getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Long idFuncionario) { this.idFuncionario = idFuncionario; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public String getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }

    public String getHoraSaida() { return horaSaida; }
    public void setHoraSaida(String horaSaida) { this.horaSaida = horaSaida; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
}