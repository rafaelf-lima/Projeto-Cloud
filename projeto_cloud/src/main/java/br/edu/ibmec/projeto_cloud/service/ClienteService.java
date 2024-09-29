package br.edu.ibmec.projeto_cloud.service;

import br.edu.ibmec.projeto_cloud.exception.CartaoException;
import br.edu.ibmec.projeto_cloud.exception.ClienteException;
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

        if (clienteRepository.findByCpf(cliente.getCpf()).isPresent())
            throw new ClienteException("Já existe um cliente com esse CPF.");

        if (!verificaIdade(cliente.getDataNascimento())){
            throw new ClienteException("Cliente deve ser maior de 18 anos");
        }

        clienteRepository.save(cliente);

        return cliente;
    }

    public Cliente associarCartao(Cartao cartao, int id) throws Exception {
        Optional<Cliente> clienteExistente = clienteRepository.findById(id);

        if (!clienteExistente.isPresent()) {
            return null;
        }
        Cliente cliente = clienteExistente.get();


        if (cartaoRepository.findByNumeroCartao(cartao.getNumeroCartao()) != null) {
            throw new CartaoException("Número do cartão já associado a outro cliente");
        }

        if (cliente.getCartoes().stream().anyMatch(c -> c.getNumeroCartao().endsWith(cartao.getNumeroCartao().substring(cartao.getNumeroCartao().length() - 4)))) {
            throw new CartaoException("Cliente já possui um cartão com os mesmos 4 últimos dígitos");
        }

        if (cartao.getDataValidade().isBefore(LocalDate.now())) {
            throw new CartaoException("Insira uma data correta, o cartão deve ter data de validade superior a hoje.");
        }

        Notificacao notificacao = new Notificacao();
        
        String ultimosQuatroDigitos = cartao.getNumeroCartao().substring(cartao.getNumeroCartao().length() - 4);

        notificacao.setTipoNotificacao("Associação de cartão");
        notificacao.setMensagem("Cartão com final " + ultimosQuatroDigitos + " associado com sucesso");
        notificacao.setDataNotificacao(LocalDateTime.now());

        cliente.associarCartao(cartao);

        cartaoRepository.save(cartao);

        cliente.associarNotificacao(notificacao);

        notificacaoRepository.save(notificacao);

        clienteRepository.save(cliente);

        return cliente;
    }

    public Cartao cartaoStatus(int id, int idCartao) throws Exception {
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);        
        Optional<Cartao> cartaoOptional = cartaoRepository.findById(idCartao);

        if (!clienteOptional.isPresent() || !cartaoOptional.isPresent())  {
            return null;
        }
        Cliente cliente = clienteOptional.get();
        Cartao cartao = cartaoOptional.get();

        if (!cliente.getCartoes().contains(cartao)) {
            throw new CartaoException("Cartão não está associado a esse cliente");
        }

        cartao.setEstaAtivado(!cartao.getEstaAtivado());

        String status;

        if (cartao.getEstaAtivado()) {
            status = "ativado";
        } else {
            status = "desativado";
        }

        Notificacao notificacao = new Notificacao();
        
        String ultimosQuatroDigitos = cartao.getNumeroCartao().substring(cartao.getNumeroCartao().length() - 4);

        notificacao.setTipoNotificacao("Desbloqueio de cartão");
        notificacao.setMensagem("Cartão com final " + ultimosQuatroDigitos + " está " + status);
        notificacao.setDataNotificacao(LocalDateTime.now());

        cartaoRepository.save(cartao);

        cliente.associarNotificacao(notificacao);

        notificacaoRepository.save(notificacao);

        return cartao;
    }

    public boolean verificaIdade(LocalDate dataNascimento){
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        return idade >= 18;
    }
}
