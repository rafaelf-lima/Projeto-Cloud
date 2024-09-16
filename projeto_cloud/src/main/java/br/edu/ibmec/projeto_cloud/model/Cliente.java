package br.edu.ibmec.projeto_cloud.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class Cliente {
    private UUID id;

    @NotBlank(message = "O nome do cliente é obrigatório")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotNull(message = "Data de nascimento é obrigatório")
    private LocalDate dataNascimento;

    @Email(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    private List<Cartao> cartoes = new ArrayList<>();

    public void associarCartao(Cartao cartao) {
        this.cartoes.add(cartao);
    }
}
