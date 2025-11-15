package com.paofresquim.dto;

import java.time.LocalDateTime;

public class FuncionarioResponseDTO {

    private Long idFuncionario;
    private String nome;
    private String telefone;
    private String email;
    private String cargo;
    private Double salarioBase;
    private LocalDateTime dataAdmissao;
    private Boolean ativo;

    public FuncionarioResponseDTO(Long idFuncionario, String nome, String telefone, String email, 
                                 String cargo, Double salarioBase, LocalDateTime dataAdmissao, Boolean ativo) {
        this.idFuncionario = idFuncionario;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.dataAdmissao = dataAdmissao;
        this.ativo = ativo;
    }

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getCargo() {
        return cargo;
    }

    public Double getSalarioBase() {
        return salarioBase;
    }

    public LocalDateTime getDataAdmissao() {
        return dataAdmissao;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}