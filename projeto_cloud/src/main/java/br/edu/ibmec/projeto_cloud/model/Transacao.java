package br.edu.ibmec.projeto_cloud.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
@Entity
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @NotNull(message = "Data e hora da transação são obrigatórios")
    private LocalDateTime dataTransacao;

    @Column
    @NotNull(message = "Valor da transação é obrigatório")
    private Double valor;

    @Column
    @NotBlank(message = "Informação sobre o comerciante é obrigatório")
    private String comerciante;
}
