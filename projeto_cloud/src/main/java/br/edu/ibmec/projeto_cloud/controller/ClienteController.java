package br.edu.ibmec.projeto_cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import br.edu.ibmec.projeto_cloud.repository.ClienteRepository;
import br.edu.ibmec.projeto_cloud.service.ClienteService;
import br.edu.ibmec.projeto_cloud.model.Cliente;
import br.edu.ibmec.projeto_cloud.model.Cartao;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService service;

    @GetMapping
    public ResponseEntity<List<Cliente>> getCliente() {
        List<Cliente> Clientes = clienteRepository.findAll();
        return new ResponseEntity<>(Clientes, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable("id") int id) {
        Optional<Cliente> tryResponse = clienteRepository.findById(id);

        if (!tryResponse.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Cliente response = tryResponse.get();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<Cliente> saveCliente(@Valid @RequestBody Cliente cliente) throws Exception {
        Cliente response = service.createCliente(cliente);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("{id}/associar-cartao")
    public ResponseEntity<Cliente> getClienteById(@PathVariable("id") int id, @Valid @RequestBody Cartao cartao) throws Exception {
        Optional<Cliente> tryResponse = clienteRepository.findById(id);

        if (!tryResponse.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Cliente response = service.associarCartao(cartao, id);
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // enviarNotificacaoSobreAssociacaoDeCartao
}
