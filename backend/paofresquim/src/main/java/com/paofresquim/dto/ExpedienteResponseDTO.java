package com.paofresquim.dto;

public class ExpedienteResponseDTO {

    private Long idExpediente;
    private Long idFuncionario;
    private String nomeFuncionario;
    private String cargoFuncionario;
    private String diaSemana;
    private String horaEntrada;
    private String horaSaida;
    private String turno;

    public ExpedienteResponseDTO(Long idExpediente, Long idFuncionario, String nomeFuncionario, 
                               String cargoFuncionario, String diaSemana, String horaEntrada, 
                               String horaSaida, String turno) {
        this.idExpediente = idExpediente;
        this.idFuncionario = idFuncionario;
        this.nomeFuncionario = nomeFuncionario;
        this.cargoFuncionario = cargoFuncionario;
        this.diaSemana = diaSemana;
        this.horaEntrada = horaEntrada;
        this.horaSaida = horaSaida;
        this.turno = turno;
    }

    public Long getIdExpediente() { return idExpediente; }
    public Long getIdFuncionario() { return idFuncionario; }
    public String getNomeFuncionario() { return nomeFuncionario; }
    public String getCargoFuncionario() { return cargoFuncionario; }
    public String getDiaSemana() { return diaSemana; }
    public String getHoraEntrada() { return horaEntrada; }
    public String getHoraSaida() { return horaSaida; }
    public String getTurno() { return turno; }
}