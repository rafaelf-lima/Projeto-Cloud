package br.edu.ibmec.projeto_cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
// import jakarta.validation.Valid;

import br.edu.ibmec.projeto_cloud.service.TransacaoService;
import br.edu.ibmec.projeto_cloud.model.Transacao;

// import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transacao")
public class TransacaoController {

    @Autowired
    private TransacaoService service;

    @GetMapping
    public ResponseEntity<List<Transacao>> getTransacao() {
        return new ResponseEntity<>(service.getAllItems(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Transacao> getTransacaoById(@PathVariable("id") UUID id) {
        Transacao transacao = service.buscaTransacao(id);
        if (transacao != null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(transacao, HttpStatus.OK);
    }

    // saveTransacao

    // associarTransacaoCliente

}
