package br.edu.ibmec.projeto_cloud.model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
public class Cartao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    @Size(min = 16, max = 16, message = "Insira o número de cartão corretamente")
    @NotNull(message = "Número do cartão é obrigatório")
    @Pattern(regexp = "^[0-9]+$", message = "Número de cartão deve conter apenas números")
    private String numeroCartao;

    @Column
    @Size(min = 3, max = 3, message = "Insira o CVV corretamente")
    @NotNull(message = "CVV obrigatório")
    @Pattern(regexp = "^[0-9]+$", message = "CVV deve conter apenas números")
    private String cvv;

    @Column
    @NotNull(message = "Data de validade é obrigatória")
    @Future(message = "Data de validade inválida")
    private LocalDate dataValidade;

    @Column
    @NotNull(message = "Limite é obrigatório")
    private double limite; // Limite no início do mês (pós pagamento da boleto)

    @Column
    @NotNull(message = "Saldo é obrigatório")
    private double saldo; // Saldo: saldo - valor por transações

    @Column
    @JsonProperty(defaultValue = "false")  // Informa ao Jackson que o padrão é false
    private Boolean estaAtivado = false;   // Definindo false como valor padrão

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id", name = "cartao_id")
    private List<Transacao> transacoes = new ArrayList<>();

    public void adicionarTransacao(Transacao transacao) {
        this.transacoes.add(transacao);
    }
}
