package br.edu.ibmec.projeto_cloud.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class Cartao {
    private UUID id;
    private int numeroCartao;
    private LocalDate dataValidade;
    private int cvv;
    private double limite; // Limite: limite - quantidade de transação
    private double saldo;
    private Boolean estaAtivado;
    private List<Transacao> transacoes;
}
