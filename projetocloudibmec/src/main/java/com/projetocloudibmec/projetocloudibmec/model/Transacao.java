package com.projetocloudibmec.projetocloudibmec.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@Data
public class Transacao {
    private UUID id;
    private LocalDateTime dataTransacao;
    private double valor;
    private String comerciante;
}
