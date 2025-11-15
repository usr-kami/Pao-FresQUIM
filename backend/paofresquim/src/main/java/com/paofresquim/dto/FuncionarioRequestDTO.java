package com.paofresquim.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FuncionarioRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String telefone;

    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Cargo é obrigatório")
    private String cargo;

    @NotNull(message = "Salário base é obrigatório")
    @Positive(message = "Salário base deve ser positivo")
    private Double salarioBase;

    private Boolean ativo = true;

    public FuncionarioRequestDTO() {}

    public FuncionarioRequestDTO(String nome, String telefone, String email, String cargo, Double salarioBase, Boolean ativo) {
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.ativo = ativo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(Double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}