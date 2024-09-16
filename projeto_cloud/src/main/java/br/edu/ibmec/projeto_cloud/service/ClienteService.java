package br.edu.ibmec.projeto_cloud.service;

import org.springframework.stereotype.Service;
import br.edu.ibmec.projeto_cloud.model.Cliente;
import br.edu.ibmec.projeto_cloud.model.Cartao;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {
    private static List<Cliente> Clientes = new ArrayList<>();

    public List<Cliente> getAllItems() {
        return ClienteService.Clientes;
    }

    public Cliente buscaCliente(UUID id) {
        return findCliente(id);
    }

    public Cliente createCliente(Cliente cliente) throws Exception {
        String cpfFormatado = cliente.getCpf().replaceAll("\\D", "");
        if (!validarCPF(cliente.getCpf())) {
            throw new Exception("CPF inválido.");
        }
        for (Cliente c : Clientes) {
            String cpfExistente = c.getCpf().replaceAll("\\D", "");
            if (cpfExistente.equals(cpfFormatado)) {
                throw new Exception("Já existe um cliente com esse CPF.");
            }
        }
        if (!eMaiorDeIdade(cliente.getDataNascimento())){
            throw new Exception("Você deve ser maior de 18 anos");
        }
        cliente.setId(UUID.randomUUID());
        cliente.setCpf(cpfFormatado);
        ClienteService.Clientes.add(cliente);
        return cliente;
    }

    public Cliente associarCartao(Cartao cartao, UUID id) throws Exception {
        Cliente cliente = this.findCliente(id);
        if (cliente == null) {
            throw new Exception("Cliente não encontrado");
        }
        cliente.associarCartao(cartao);
        return cliente;
    }

    private Cliente findCliente(UUID id) {
        Cliente response = null;

        for (Cliente cliente : Clientes) {
            if (cliente.getId().equals(id)) {
                response = cliente;
                break;
            }
        }
        return response;
    }

    public boolean validarCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11) {
            return false;
        }
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }
        try {
            int peso = 10;
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }

            int primeiroDigitoVerificador = 11 - (soma % 11);
            if (primeiroDigitoVerificador >= 10) {
                primeiroDigitoVerificador = 0;
            }
            peso = 11;
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }

            int segundoDigitoVerificador = 11 - (soma % 11);
            if (segundoDigitoVerificador >= 10) {
                segundoDigitoVerificador = 0;
            }
            return (primeiroDigitoVerificador == Character.getNumericValue(cpf.charAt(9)) &&
                    segundoDigitoVerificador == Character.getNumericValue(cpf.charAt(10)));

        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean eMaiorDeIdade(LocalDate dataNascimento){
        return Period.between(dataNascimento, LocalDate.now()).getYears() >= 18;
    }
}
