package com.projetocloudibmec.projetocloudibmec.service;
import com.projetocloudibmec.projetocloudibmec.controller.ClienteRequest;
import com.projetocloudibmec.projetocloudibmec.model.Cartao;
import com.projetocloudibmec.projetocloudibmec.model.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Service
public class ClienteService {
    private static List<Cliente> Clientes = new ArrayList<>();

    public Cliente criarCliente(ClienteRequest request) {
        Cliente cliente = new Cliente();

        cliente.setNome(request.getNome());
        cliente.setSobrenome(request.getNome());
        cliente.setCpf(request.getCpf()); // Validar CPF
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());
        cliente.setDataNascimento(request.getDataNascimento());
        cliente.setEndereco(request.getEndereco());
        cliente.setId(UUID.randomUUID());

        Clientes.add(cliente);

        return cliente;
    }

    public Cliente buscaCliente(UUID id) {
        return this.buscaCliente(id);
    }


    public Cliente associarCartao(Cartao cartao, UUID id) throws Exception {
        Cliente cliente = this.findCliente(id);
    if (cliente == null) {
        throw new Exception("Não encontrou");
    }
        if (cartao.getEstaAtivado() == false) {
            throw new Exception("Não é possível associar um cartão inativo ao usuário");
        }
        cartao.setId(UUID.randomUUID());
        cliente.associarCartao(cartao);
        return cliente;

    }
    private Cliente findCliente(UUID id) {
        for (Cliente cliente : Clientes) {
            if (cliente.getId().equals(id)) {
                return cliente;
            }
        }
        return null;
    }
}
