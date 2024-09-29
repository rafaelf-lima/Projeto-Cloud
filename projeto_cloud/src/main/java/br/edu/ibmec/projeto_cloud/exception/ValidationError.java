package br.edu.ibmec.projeto_cloud.exception;

import lombok.Data;

@Data
public class ValidationError {
    private String field;
    private String message;
}
