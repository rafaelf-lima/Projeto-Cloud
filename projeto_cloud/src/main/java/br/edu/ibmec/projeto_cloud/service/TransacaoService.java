package br.edu.ibmec.projeto_cloud.service;

// import br.edu.ibmec.projeto_cloud.model.Cliente;
// import br.edu.ibmec.projeto_cloud.model.Cartao;
import br.edu.ibmec.projeto_cloud.model.Transacao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransacaoService {
    private static List<Transacao> Transacoes = new ArrayList<>();

    public List<Transacao> getAllItems() {
        return TransacaoService.Transacoes;
    }

    public Transacao buscaTransacao(UUID id) {
        return findTransacao(id);
    }

    // createTransacao

    // associaTransacaoCliente

    // associaTransacaoComerciante

    // enviarNotificacaoSobreTransacao


    private Transacao findTransacao(UUID id) {
        Transacao response = null;

        for (Transacao transacao : Transacoes) {
            if (transacao.getId().equals(id)) {
                response = transacao;
                break;
            }
        }
        return response;
    }
}
