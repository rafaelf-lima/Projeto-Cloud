package br.edu.ibmec.projeto_cloud.service;

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
        // Busca cartão pelo id
        Optional<Cartao> cartaoOptional = cartaoRepository.findById(id);

        // Verifica se o cartão existe
        if (!cartaoOptional.isPresent())
            return null;
        
        Cartao cartao = cartaoOptional.get();

        // Busca o cliente pelo cartão
        Optional<Cliente> clienteOptional = clienteRepository.findByCartoes(cartao);

        // Verifica se o cliente existe
        if (!clienteOptional.isPresent())
            throw new Exception("Erro ao achar o cliente");

        Cliente cliente = clienteOptional.get();

        // Cria a notificação
        Notificacao notificacao = new Notificacao();
        
        String ultimosQuatroDigitos = cartao.getNumeroCartao().substring(cartao.getNumeroCartao().length() - 4);

        notificacao.setTipoNotificacao("Tentativa de transação");
        notificacao.setDataNotificacao(transacao.getDataTransacao());

        String mensagemBase = "Tentativa de transação no cartão com final " + ultimosQuatroDigitos + ". Motivo da recusa: ";

        // Verifica se o cartão está ativo
        if (!cartao.getEstaAtivado()) {
            // Envia notificação
            notificacao.setMensagem(mensagemBase + "Cartão desativado");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new Exception("Cartão desativado.");
        }

        // Verifica se o cartão tem saldo
        if (cartao.getSaldo() < transacao.getValor()) {
            // Envia notificação
            notificacao.setMensagem(mensagemBase + "Saldo insuficiente");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new Exception("Saldo insuficiente para a compra");
        }

        if (cartao.getLimite() < transacao.getValor()) {
            notificacao.setMensagem(mensagemBase + "Limite insuficiente");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new Exception("Limite insuficiente para a compra");
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
            // Envia notificação
            notificacao.setMensagem(mensagemBase + "Alta frequência de transações");
            cliente.associarNotificacao(notificacao);
            notificacaoRepository.save(notificacao);

            throw new Exception("Limite de 3 transações em 2 minutos excedido.");
        }

        // Verifica se existe alguma transação em comum entre as duas listas
        for (Transacao transacaoDuplicada : transacoesDuplicadas) {
            for (Transacao transacaoCartao : transacoesCartao) {
                if (transacaoCartao.getValor() == transacaoDuplicada.getValor() &&
                    transacaoCartao.getComerciante().equals(transacaoDuplicada.getComerciante())) {
                    // Se as transações forem do mesmo valor e comerciante, compara a data de transação
                    if (isWithinTwoMinutes(transacaoCartao.getDataTransacao(), transacaoDuplicada.getDataTransacao())) {
                        // Envia notificação
                        notificacao.setMensagem(mensagemBase + "Transação duplicada");
                        cliente.associarNotificacao(notificacao);
                        notificacaoRepository.save(notificacao);
            
                        throw new Exception("Transação duplicada encontrada.");
                    }
                }
            }
        }
        // Ajusta notificação para 'Transação aprovada'
        notificacao.setTipoNotificacao("Transação aprovada");
        notificacao.setMensagem("Transação aprovada no cartão com final " + ultimosQuatroDigitos + ". Valor de R$" + transacao.getValor() + " em " + transacao.getComerciante());

        // Associa a notificação ao cliente
        cliente.associarNotificacao(notificacao);

        // Salvar notificação no repositório
        notificacaoRepository.save(notificacao);

        // Associa a transação ao cartão
        cartao.adicionarTransacao(transacao);

        // Salva a transação no banco de dados
        transacaoRepository.save(transacao);

        // Atualiza o saldo do cartão
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
            return null;
        
        Cartao cartao = cartaoExistente.get();
        
        return cartao.getTransacoes();
    }

    private boolean isWithinTwoMinutes(LocalDateTime dataTransacaoExistente, LocalDateTime dataTransacaoNova) {
        // Calcula a diferença entre as duas datas
        Duration duration = Duration.between(dataTransacaoExistente, dataTransacaoNova);

        // Verifica se a diferença é menor que 2 minutos (em termos absolutos)
        return Math.abs(duration.toMinutes()) < 2;
    }

}
