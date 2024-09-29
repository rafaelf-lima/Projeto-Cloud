package br.edu.ibmec.projeto_cloud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Field;

@ControllerAdvice
public class ValidationErrorInterceptor {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationMessageError validationMessageError(MethodArgumentNotValidException e) {
        ValidationMessageError response = new ValidationMessageError();

        for (FieldError item : e.getFieldErrors()) {
            ValidationError error = new ValidationError();
            error.setField(item.getField());
            error.setMessage(item.getDefaultMessage());
            response.getErrors().add(error);
        }
        return response;
    }

    @ExceptionHandler(ClienteException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationMessageError validationHandlerCliente(ClienteException e) {
        ValidationMessageError response = new ValidationMessageError();
        ValidationError error = new ValidationError();
        error.setField("exception");
        error.setMessage(e.getMessage());
        response.getErrors().add(error);
        return response;
    }

    @ExceptionHandler(CartaoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationMessageError validationHandlerCartao(CartaoException e) {
        ValidationMessageError response = new ValidationMessageError();
        ValidationError error = new ValidationError();
        error.setField("exception");
        error.setMessage(e.getMessage());
        response.getErrors().add(error);
        return response;
    }

    @ExceptionHandler(TransacaoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationMessageError validationHandlerTransacao(TransacaoException e) {
        ValidationMessageError response = new ValidationMessageError();
        ValidationError error = new ValidationError();
        error.setField("exception");
        error.setMessage(e.getMessage());
        response.getErrors().add(error);
        return response;
    }

}
