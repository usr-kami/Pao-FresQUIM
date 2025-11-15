package com.paofresquim.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "funcionarios")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funcionario")
    private Long idFuncionario;

    @NotBlank(message = "Nome é obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "telefone")
    private String telefone;

    @Email(message = "Email deve ser válido")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "Cargo é obrigatório")
    @Column(name = "cargo", nullable = false)
    private String cargo;

    @NotNull(message = "Salário base é obrigatório")
    @Positive(message = "Salário base deve ser positivo")
    @Column(name = "salario_base", nullable = false)
    private Double salarioBase;

    @Column(name = "data_admissao")
    private LocalDateTime dataAdmissao;

    @Column(name = "ativo")
    private Boolean ativo;

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL)
    private List<FeriasFuncionario> ferias = new ArrayList<>();

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL)
    private List<ExpedienteFuncionario> expedientes = new ArrayList<>();

    public Funcionario() {
        this.dataAdmissao = LocalDateTime.now();
        this.ativo = true;
    }

    public Funcionario(String nome, String telefone, String email, String cargo, Double salarioBase) {
        this();
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
    }

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
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

    public LocalDateTime getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDateTime dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<FeriasFuncionario> getFerias() {
        return ferias;
    }

    public void setFerias(List<FeriasFuncionario> ferias) {
        this.ferias = ferias;
    }

    public List<ExpedienteFuncionario> getExpedientes() {
        return expedientes;
    }

    public void setExpedientes(List<ExpedienteFuncionario> expedientes) {
        this.expedientes = expedientes;
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "idFuncionario=" + idFuncionario +
                ", nome='" + nome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", cargo='" + cargo + '\'' +
                ", salarioBase=" + salarioBase +
                ", dataAdmissao=" + dataAdmissao +
                ", ativo=" + ativo +
                '}';
    }
}