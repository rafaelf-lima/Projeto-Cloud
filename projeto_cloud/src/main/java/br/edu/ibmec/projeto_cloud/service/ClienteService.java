package br.edu.ibmec.projeto_cloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ibmec.projeto_cloud.repository.ClienteRepository;
import br.edu.ibmec.projeto_cloud.repository.CartaoRepository;
import br.edu.ibmec.projeto_cloud.model.Cliente;
import br.edu.ibmec.projeto_cloud.model.Cartao;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CartaoRepository cartaoRepository;


    public Cliente createCliente(Cliente cliente) throws Exception {

        // Verifica se o CPF já está cadastrado
        if (clienteRepository.findByCpf(cliente.getCpf()).isPresent())
            throw new Exception("Já existe um cliente com esse CPF.");

        // Verifica se o cliente é maior de idade
        if (!verificaIdade(cliente.getDataNascimento())){
            throw new Exception("Cliente deve ser maior de 18 anos");
        }

        // Formata CPF do cliente
        cliente.setCpf(cpfFormatado);
        
        // Salva cliente na Base de dados
        clienteRepository.save(cliente);

        return cliente;
    }

    public Cliente associarCartao(Cartao cartao, int id) throws Exception {
        // Busca o cliente
        Optional<Cliente> clienteExistente = clienteRepository.findById(id);

        // Verifica se o cliente existe
        if (!clienteExistente.isPresent()) {
            throw new Exception("Cliente não encontrado");
        }

        Cliente cliente = clienteExistente.get();

        // Verifica se o cartão está ativado
        if (cartao.getEstaAtivado() == false) {
            throw new Exception("Cartão não está ativado");
        }

        if (cartao.getDataValidade().isBefore(LocalDate.now())) {
            throw new Exception("Insira uma data correta, o cartão deve ter data de validade superior a hoje.");
        }

        
        // Associa o cartão ao cliente
        cliente.associarCartao(cartao);

        // Salvar cartão no repositório
        cartaoRepository.save(cartao);

        // Atualiza o cliente com novo cartão no repositório
        clienteRepository.save(cliente);

        return cliente;
    }

    // public boolean validarCPF(String cpf) {
    //     cpf = cpf.replaceAll("\\D", "");
    //     if (cpf.length() != 11) {
    //         return false;
    //     }
    //     if (cpf.chars().distinct().count() == 1) {
    //         return false;
    //     }
    //     try {
    //         int peso = 10;
    //         int soma = 0;
    //         for (int i = 0; i < 9; i++) {
    //             soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
    //         }

    //         int primeiroDigitoVerificador = 11 - (soma % 11);
    //         if (primeiroDigitoVerificador >= 10) {
    //             primeiroDigitoVerificador = 0;
    //         }
    //         peso = 11;
    //         soma = 0;
    //         for (int i = 0; i < 10; i++) {
    //             soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
    //         }

    //         int segundoDigitoVerificador = 11 - (soma % 11);
    //         if (segundoDigitoVerificador >= 10) {
    //             segundoDigitoVerificador = 0;
    //         }
    //         return (primeiroDigitoVerificador == Character.getNumericValue(cpf.charAt(9)) &&
    //                 segundoDigitoVerificador == Character.getNumericValue(cpf.charAt(10)));

    //     } catch (NumberFormatException e) {
    //         return false;
    //     }
    // }

    public boolean verificaIdade(LocalDate dataNascimento){
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        return idade >= 18;
    }

    // enviarNotificacaoSobreAssociacaoDeCartao
}
