package com.projetocloudibmec.projetocloudibmec.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ClienteRequest {
    private UUID id;
    @NotBlank(message = "Campo nome é obrigatório")
    private String nome;
    @NotBlank(message = "Campo sobrenome é obrigatório")
    private String sobrenome;
    @NotBlank(message = "Campo CPF é obrigatório")
    private String cpf;
    @NotBlank(message = "Campo e-mail é obrigatório")
    @Email
    private String email;
    @NotBlank(message = "Campo telefone é obrigatório")
    private String telefone;
    @NotNull(message = "Campo data de nascimento é obrigatório")
    private LocalDate dataNascimento;
    @NotBlank(message = "Campo endereço é obrigatório")
    private String endereco;
}
