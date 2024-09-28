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
import java.util.Optional;

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
        Optional<Transacao> tryResponse = transacaoRepository.findById(id);

        if (!tryResponse.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Transacao response = tryResponse.get();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("cartao/{id}")
    public ResponseEntity<List<Transacao>> getTransacoesByCartao(@PathVariable("id") int id) throws Exception {
        List<Transacao> transacoes = service.getAllTransacoesByCartao(id);

        if (transacoes == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(transacoes, HttpStatus.OK);
    }

    @PostMapping("cartao/{id}")
    public ResponseEntity<Transacao> saveTransacao(@PathVariable("id") int id, @Valid @RequestBody Transacao transacao) throws Exception {
        Transacao response = service.createTransacao(transacao, id);

        if (response == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
