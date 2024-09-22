package br.edu.ibmec.projeto_cloud.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
public class Cartao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @NotNull(message = "Número do cartão é obrigatório")
    private long numeroCartao;

    @Column
    @NotNull(message = "Data de validade é obrigatória")
    private LocalDate dataValidade;

    @Column
    @NotNull(message = "CVV obrigatório")
    private int cvv;

    @Column
    @NotNull
    private double limite; // Limite: limite - quantidade de transação

    @Column
    @NotNull(message = "Saldo é obrigatório")
    private double saldo;

    @Column
    @NotNull(message = "Status do cartão é obrigatório")
    private Boolean estaAtivado;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "cartao_id")
    private List<Transacao> transacoes;

    public void adicionarTransacao(Transacao transacao) {
        this.transacoes.add(transacao);
    }
}
