package com.projetocloudibmec.projetocloudibmec.controller;

import com.projetocloudibmec.projetocloudibmec.model.Cartao;
import com.projetocloudibmec.projetocloudibmec.model.Cliente;
import com.projetocloudibmec.projetocloudibmec.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/cliente")
public class ClienteController {
    @Autowired
    private ClienteService service;

    @GetMapping("{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable("id") UUID id) {
        Cliente response = service.buscaCliente(id);
        if (response == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping()
    public ResponseEntity<Cliente> saveCliente(@Valid @RequestBody ClienteRequest request) {
        Cliente response = service.criarCliente(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("{id}/associar-cartao")
    public ResponseEntity<Cliente> getClienteById(@PathVariable("id") UUID id, @Valid @RequestBody Cartao cartao) throws Exception {
        Cliente response = service.associarCartao(cartao, id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}