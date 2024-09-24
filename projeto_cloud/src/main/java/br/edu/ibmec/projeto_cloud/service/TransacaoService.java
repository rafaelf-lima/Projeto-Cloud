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
        // Busca cartão pelo id
        Optional<Cartao> cartaoExistente = cartaoRepository.findById(id);

        // Verifica se o cartão existe
        if (!cartaoExistente.isPresent())
            throw new Exception("Cartão não encontrado.");
        
        Cartao cartao = cartaoExistente.get();

        // Verifica se o cartão está ativo  
        if (!cartao.getEstaAtivado()) {
            throw new Exception("Cartão inativo.");
        }

        if (cartao.getSaldo() < transacao.getValor()) {
            throw new Exception("Saldo insuficiente para a compra");
        }

        if (cartao.getLimite() < transacao.getValor()) {
            throw new Exception("Limite inferior ao valor de compra.");
        }

        // Busca por transações com o mesmo valor e comerciante
        List<Transacao> transacoesDuplicadas = transacaoRepository.findByValorAndComerciante(
            transacao.getValor(), transacao.getComerciante()
        );

        // Busca por transações feitas no cartão
        List<Transacao> transacoesCartao = cartao.getTransacoes();

        // Contar quantas transações ocorreram nos últimos 2 minutos em relação à nova transação
        int transacoesNosUltimosDoisMinutos = 0;

        for (Transacao transacaoCartao : transacoesCartao) {
            if (isWithinTwoMinutes(transacaoCartao.getDataTransacao(), transacao.getDataTransacao())) {
                transacoesNosUltimosDoisMinutos++;
            }
        }

        // Verifica se já existem 3 ou mais transações nos últimos 2 minutos
        if (transacoesNosUltimosDoisMinutos >= 3) {
            throw new Exception("Limite de 3 transações em 2 minutos excedido.");
        }

        // Verifica se existe alguma transação em comum entre as duas listas
        for (Transacao transacaoDuplicada : transacoesDuplicadas) {
            for (Transacao transacaoCartao : transacoesCartao) {
                if (transacaoCartao.getValor() == transacaoDuplicada.getValor() &&
                    transacaoCartao.getComerciante().equals(transacaoDuplicada.getComerciante())) {
                    // Se as transações forem do mesmo valor e comerciante, compara a data de transação
                    if (isWithinTwoMinutes(transacaoCartao.getDataTransacao(), transacaoDuplicada.getDataTransacao())) {
                        throw new Exception("Transação duplicada encontrada.");
                    }
                }
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

    public List<Transacao> getAllTransacoesByCartao(int id) throws Exception {

        // Busca cliente pelo id
        Optional<Cartao> cartaoExistente = cartaoRepository.findById(id);

        // Verifica se o cartão existe
        if (!cartaoExistente.isPresent())
            throw new Exception("Cartão não encontrado.");
        
        Cartao cartao = cartaoExistente.get();
        
        return cartao.getTransacoes();
    }

    private boolean isWithinTwoMinutes(LocalDateTime dataTransacaoExistente, LocalDateTime dataTransacaoNova) {
        // Calcula a diferença entre as duas datas
        Duration duration = Duration.between(dataTransacaoExistente, dataTransacaoNova);

        // Verifica se a diferença é menor que 2 minutos (em termos absolutos)
        return Math.abs(duration.toMinutes()) < 2;
    }

    // enviarNotificacaoSobreTransacao

}
