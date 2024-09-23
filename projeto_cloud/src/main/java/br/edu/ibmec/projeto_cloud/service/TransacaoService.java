package br.edu.ibmec.projeto_cloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ibmec.projeto_cloud.repository.TransacaoRepository;
import br.edu.ibmec.projeto_cloud.repository.CartaoRepository;
import br.edu.ibmec.projeto_cloud.model.Transacao;
import br.edu.ibmec.projeto_cloud.model.Cartao;

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


    public Transacao createTransacao(Transacao transacao, int id) throws Exception {

        Cartao cartao;

        // Verifica se o cartão existe
        if (cartaoRepository.findById(id).isEmpty()) {
            throw new Exception("Cartão não encontrado.");
        } else {
            cartao = cartaoRepository.findById(id).get();
        }

        // Verifica se o cartão está ativo
        if (!cartao.getEstaAtivado()) {
            throw new Exception("Cartão inativo.");
        }

        if (cartao.getSaldo() < transacao.getValor()) {
            throw new Exception("Cartão sem limite para a compra");
        }

        // Busca por transações com o mesmo valor e comerciante
        List<Transacao> transacoesComMesmoValorEComerciante = transacaoRepository.findByValorAndComerciante(
            transacao.getValor(), transacao.getComerciante()
        );

        // Verifica se existe alguma transação com dataTransacao a menos de 2 minutos de diferença
        for (Transacao transacaoExistente : transacoesComMesmoValorEComerciante) {
            if (tempoTransacao(transacaoExistente.getDataTransacao(), transacao.getDataTransacao())) {
                throw new Exception("Transação duplicada encontrada.");
            }
        }
        // Associa a transação ao cartão
        cartao.adicionarTransacao(transacao);

        // Salva a transação no banco de dados
        transacaoRepository.save(transacao);

        cartao.setSaldo(cartao.getSaldo() - transacao.getValor());

        // Salva o cartão no banco de dados
        cartaoRepository.save(cartao);
        
        return transacao;
    }

    private boolean tempoTransacao(LocalDateTime dataTransacaoExistente, LocalDateTime dataTransacaoNova) {
        // Calcula a diferença entre as duas datas
        Duration duration = Duration.between(dataTransacaoExistente, dataTransacaoNova);

        // Verifica se a diferença é menor que 2 minutos (em termos absolutos)
        return Math.abs(duration.toMinutes()) < 2;
    }

    public Transacao buscaTransacao(int id) {
        return findTransacao(id);
    }

    private Transacao findTransacao(int id) {
        Optional<Transacao> transacao = transacaoRepository.findById(id);

        if (transacao.isEmpty())
            return null;

        return transacao.get();
    }

    public List<Transacao> getAllTransacoesByCartao(int id) throws Exception {
        Cartao cartao;

        // Verifica se o cartão existe
        if (cartaoRepository.findById(id).isEmpty()) {
            throw new Exception("Cartão não encontrado.");
        } else {
            cartao = cartaoRepository.findById(id).get();
        }
        
        return cartao.getTransacoes();
    }

    // enviarNotificacaoSobreTransacao

}
