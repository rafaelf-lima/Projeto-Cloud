package br.edu.ibmec.projeto_cloud.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import lombok.Data;

@Data
@Entity
public class Notificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @NotNull(message = "Favor inserir o tipo da notificação")
    private String tipoNotificacao; // 'Cartão associado ao cliente', 'Transação aprovada', 'Falha na transação', 'Cartão ativado' etc

    @Column
    @NotNull(message = "Favor inserir a mensagem da notificação")
    private String mensagem;

    @Column
    @NotNull(message = "Favor inserir a data da notificação")
    private LocalDateTime dataNotificacao;
}
