package br.edu.ibmec.projeto_cloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ibmec.projeto_cloud.repository.ClienteRepository;
import br.edu.ibmec.projeto_cloud.repository.CartaoRepository;
import br.edu.ibmec.projeto_cloud.repository.NotificacaoRepository;
import br.edu.ibmec.projeto_cloud.model.Cliente;
import br.edu.ibmec.projeto_cloud.model.Notificacao;
import br.edu.ibmec.projeto_cloud.model.Cartao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;


    public Cliente createCliente(Cliente cliente) throws Exception {

        // Verifica se o CPF já está cadastrado
        if (clienteRepository.findByCpf(cliente.getCpf()).isPresent())
            throw new Exception("Já existe um cliente com esse CPF.");

        // Verifica se o cliente é maior de idade
        if (!verificaIdade(cliente.getDataNascimento())){
            throw new Exception("Cliente deve ser maior de 18 anos");
        }
        
        // Salva cliente na Base de dados
        clienteRepository.save(cliente);

        return cliente;
    }

    public Cliente associarCartao(Cartao cartao, int id) throws Exception {
        // Busca o cliente
        Optional<Cliente> clienteExistente = clienteRepository.findById(id);

        // Verifica se o cliente existe
        if (!clienteExistente.isPresent()) {
            return null;
        }
        Cliente cliente = clienteExistente.get();

        // Verifica se o número do cartão já está registrado
        if (cartaoRepository.findByNumeroCartao(cartao.getNumeroCartao()) != null) {
            throw new Exception("Número do cartão já associado a outro cliente");
        }

        // Verifica se o cliente tem um cartão com o mesmo final
        if (cliente.getCartoes().stream().anyMatch(c -> c.getNumeroCartao().endsWith(cartao.getNumeroCartao().substring(cartao.getNumeroCartao().length() - 4)))) {
            throw new Exception("Cliente já possui um cartão com os mesmos 4 últimos dígitos");
        }

        // Verifica se a data de validade é válida
        if (cartao.getDataValidade().isBefore(LocalDate.now())) {
            throw new Exception("Insira uma data correta, o cartão deve ter data de validade superior a hoje.");
        }

        // Cria a notificação
        Notificacao notificacao = new Notificacao();
        
        String ultimosQuatroDigitos = cartao.getNumeroCartao().substring(cartao.getNumeroCartao().length() - 4);

        notificacao.setTipoNotificacao("Associação de cartão");
        notificacao.setMensagem("Cartão com final " + ultimosQuatroDigitos + " associado com sucesso");
        notificacao.setDataNotificacao(LocalDateTime.now());

        // Associa o cartão ao cliente
        cliente.associarCartao(cartao);

        // Salvar cartão no repositório
        cartaoRepository.save(cartao);

        // Associa a notificação ao cliente
        cliente.associarNotificacao(notificacao);

        // Salvar notificação no repositório
        notificacaoRepository.save(notificacao);

        // Atualiza o cliente com novo cartão no repositório
        clienteRepository.save(cliente);

        return cliente;
    }

    public Cartao cartaoStatus(int id, int idCartao) throws Exception {
        // Busca cliente e cartão
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);        
        Optional<Cartao> cartaoOptional = cartaoRepository.findById(idCartao);

        // Confirma a existência do cartão e do cliente
        if (!clienteOptional.isPresent() || !cartaoOptional.isPresent())  {
            return null;
        }
        Cliente cliente = clienteOptional.get();
        Cartao cartao = cartaoOptional.get();

        // Confirma associação entre cartão e cliente
        if (!cliente.getCartoes().contains(cartao)) {
            throw new Exception("Cartão não está associado a esse cliente");
        }

        // Muda status do cartão
        cartao.setEstaAtivado(!cartao.getEstaAtivado());

        // Configura String 'status' para notificação 
        String status;

        if (cartao.getEstaAtivado()) {
            status = "ativado";
        } else {
            status = "desativado";
        }

        // Cria a notificação
        Notificacao notificacao = new Notificacao();
        
        String ultimosQuatroDigitos = cartao.getNumeroCartao().substring(cartao.getNumeroCartao().length() - 4);

        notificacao.setTipoNotificacao("Desbloqueio de cartão");
        notificacao.setMensagem("Cartão com final " + ultimosQuatroDigitos + " está " + status);
        notificacao.setDataNotificacao(LocalDateTime.now());

        // Salvar cartão com novo status
        cartaoRepository.save(cartao);

        // Associa a notificação ao cliente
        cliente.associarNotificacao(notificacao);

        // Salvar notificação no repositório
        notificacaoRepository.save(notificacao);

        return cartao;
    }

    public boolean verificaIdade(LocalDate dataNascimento){
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        return idade >= 18;
    }
}
