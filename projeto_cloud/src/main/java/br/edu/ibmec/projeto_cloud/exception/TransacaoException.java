package br.edu.ibmec.projeto_cloud.exception;

public class TransacaoException extends RuntimeException{
    public TransacaoException(String message){
        super(message);
    }
}
