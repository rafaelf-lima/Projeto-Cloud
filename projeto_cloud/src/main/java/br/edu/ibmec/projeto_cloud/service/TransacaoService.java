package br.edu.ibmec.projeto_cloud.service;

import br.edu.ibmec.projeto_cloud.exception.CartaoException;
import br.edu.ibmec.projeto_cloud.exception.ClienteException;
import br.edu.ibmec.projeto_cloud.exception.TransacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ibmec.projeto_cloud.repository.TransacaoRepository;
import br.edu.ibmec.projeto_cloud.repository.CartaoRepository;
import br.edu.ibmec.projeto_cloud.repository.NotificacaoRepository;
import br.edu.ibmec.projeto_cloud.repository.ClienteRepository;
import br.edu.ibmec.projeto_cloud.model.Transacao;
import br.edu.ibmec.projeto_cloud.model.Cartao;
import br.edu.ibmec.projeto_cloud.model.Notificacao;
import br.edu.ibmec.projeto_cloud.model.Cliente;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {
    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public Transacao createTransacao(Transacao transacao, int id) throws Exception {
        Optional<Cartao> cartaoOptional = cartaoRepository.findById(id);

        if (!cartaoOptional.isPresent())
            return null;
        
        Cartao cartao = cartaoOptional.get();

        Optional<Cliente> clienteOptional = clienteRepository.findByCartoes(cartao);

        if (!clienteOptional.isPresent())
            throw new ClienteException("Erro ao achar o cliente");

        Cliente cliente = clienteOptional.get();

        Notificacao notificacao = new Notificacao();
        
        String ultimosQuatroDigitos = cartao.getNumeroCartao().substring(cartao.getNumeroCartao().length() - 4);

        notificacao.setTipoNotificacao("Tentativa de transação");
        notificacao.setDataNotificacao(transacao.getDataTransacao());

        String mensagemBase = "Tentativa de transação no cartão com final " + ultimosQuatroDigitos + ". Motivo da recusa: ";

        if (!cartao.getEstaAtivado()) {
            notificacao.setMensagem(mensagemBase + "Cartão desativado");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new CartaoException("Cartão desativado.");
        }

        if (cartao.getSaldo() < transacao.getValor()) {
            // Envia notificação
            notificacao.setMensagem(mensagemBase + "Saldo insuficiente");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new TransacaoException("Saldo insuficiente para a compra");
        }

        if (cartao.getLimite() < transacao.getValor()) {
            notificacao.setMensagem(mensagemBase + "Limite insuficiente");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new TransacaoException("Limite insuficiente para a compra");
        }

        List<Transacao> transacoesDuplicadas = transacaoRepository.findByValorAndComerciante(
            transacao.getValor(), transacao.getComerciante()
        );

        List<Transacao> transacoesCartao = cartao.getTransacoes();

        int transacoesNosUltimosDoisMinutos = 0;

        for (Transacao transacaoCartao : transacoesCartao) {
            if (verificaMinutagem(transacaoCartao.getDataTransacao(), transacao.getDataTransacao())) {
                transacoesNosUltimosDoisMinutos++;
            }
        }

        if (transacoesNosUltimosDoisMinutos >= 3) {
            notificacao.setMensagem(mensagemBase + "Alta frequência de transações");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new TransacaoException("Limite de 3 transações em 2 minutos excedido.");
        }

        for (Transacao transacaoDuplicada : transacoesDuplicadas) {
            for (Transacao transacaoCartao : transacoesCartao) {
                if (transacaoCartao.getValor() == transacaoDuplicada.getValor() &&
                    transacaoCartao.getComerciante().equals(transacaoDuplicada.getComerciante())) {
                    if (verificaMinutagem(transacaoCartao.getDataTransacao(), transacaoDuplicada.getDataTransacao())) {
                        notificacao.setMensagem(mensagemBase + "Transação duplicada");
                        cliente.associarNotificacao(notificacao);
                        notificacaoRepository.save(notificacao);
            
                        throw new TransacaoException("Transação duplicada encontrada.");
                    }
                }
            }
        }
        notificacao.setTipoNotificacao("Transação aprovada");
        notificacao.setMensagem("Transação aprovada no cartão com final " + ultimosQuatroDigitos + ". Valor de R$" + transacao.getValor() + " em " + transacao.getComerciante());

        cliente.associarNotificacao(notificacao);

        notificacaoRepository.save(notificacao);

        cartao.adicionarTransacao(transacao);

        transacaoRepository.save(transacao);

        cartao.setSaldo(cartao.getSaldo() - transacao.getValor());

        cartaoRepository.save(cartao);
        
        return transacao;
    }

    public List<Transacao> getAllTransacoesByCartao(int id) throws Exception {
        Optional<Cartao> cartaoExistente = cartaoRepository.findById(id);

        if (!cartaoExistente.isPresent())
            return null;
        
        Cartao cartao = cartaoExistente.get();
        
        return cartao.getTransacoes();
    }

    private boolean verificaMinutagem(LocalDateTime dataTransacaoExistente, LocalDateTime dataTransacaoNova) {
        Duration duration = Duration.between(dataTransacaoExistente, dataTransacaoNova);

        return Math.abs(duration.toMinutes()) < 2;
    }

}
