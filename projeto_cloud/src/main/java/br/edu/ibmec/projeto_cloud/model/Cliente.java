package br.edu.ibmec.projeto_cloud.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;
import jakarta.validation.constraints.Pattern;

import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

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
    @CPF
    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}", message = "O CPF deve estar no formato XXX.XXX.XXX-XX")
    private String cpf;

    @Column
    @NotNull(message = "Data de nascimento é obrigatório")
    private LocalDate dataNascimento;

    @Column
    @Email(message = "Email é obrigatório")
    private String email;

    @Column
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\(\\d{2}\\)\\d{5}-\\d{4}", message = "O telefone deve estar no formato (XX)XXXXX-XXXX")
    private String telefone;

    @Column
    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id", name = "cliente_id")
    private List<Cartao> cartoes = new ArrayList<>();

    public void associarCartao(Cartao cartao) {
        this.cartoes.add(cartao);
    }
}
