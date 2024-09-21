package br.edu.ibmec.projeto_cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import br.edu.ibmec.projeto_cloud.service.TransacaoService;
import br.edu.ibmec.projeto_cloud.model.Transacao;
import br.edu.ibmec.projeto_cloud.repository.TransacaoRepository;

import java.util.List;

@RestController
@RequestMapping("/transacao")
public class TransacaoController {
    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private TransacaoService service;


    @GetMapping
    public ResponseEntity<List<Transacao>> getTransacao() {
        List<Transacao> Transacoes = transacaoRepository.findAll();
        return new ResponseEntity<>(Transacoes, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Transacao> getTransacaoById(@PathVariable("id") int id) {
        Transacao response = service.buscaTransacao(id);
        if (response == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("cartao/{id}")
    public ResponseEntity<List<Transacao>> getTransacoesByCartao(@PathVariable("id") int id) throws Exception {
        List<Transacao> response = service.getAllTransacoesByCartao(id);
        if (response.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("cartao/{id}")
    public ResponseEntity<Transacao> saveTransacao(@Valid @RequestBody Transacao transacao, @PathVariable("id") int id) throws Exception {
        Transacao response = service.createTransacao(transacao, id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // enviarNotifacacaoSobreTransacao

}
