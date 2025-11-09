package com.paofresquim.dto;

import java.time.LocalDateTime;

public class ClienteResponseDTO {

    private Long idCliente;
    private String nome;
    private String email;
    private String telefone;
    private LocalDateTime dataCadastro;

    public ClienteResponseDTO(Long idCliente, String nome, String email, String telefone, LocalDateTime dataCadastro) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataCadastro = dataCadastro;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
}