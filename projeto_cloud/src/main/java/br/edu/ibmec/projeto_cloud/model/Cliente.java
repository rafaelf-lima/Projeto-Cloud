package br.edu.ibmec.projeto_cloud.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @NotBlank(message = "O nome do cliente é obrigatório")
    private String nome;

    @Column
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @Column
    @NotNull(message = "Data de nascimento é obrigatório")
    private LocalDate dataNascimento;

    @Column
    @Email(message = "Email é obrigatório")
    private String email;

    @Column
    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @Column
    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "cliente_id")
    private List<Cartao> cartoes = new ArrayList<>();

    public void associarCartao(Cartao cartao) {
        this.cartoes.add(cartao);
    }
}
