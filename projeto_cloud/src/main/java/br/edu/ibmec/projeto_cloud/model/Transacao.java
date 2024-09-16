package br.edu.ibmec.projeto_cloud.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class Transacao {
    private UUID id;
    private LocalDateTime dataTransacao;
    private double valor;
    private String comerciante;
}
