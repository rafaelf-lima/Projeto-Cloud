package br.edu.ibmec.projeto_cloud.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.*;

import br.edu.ibmec.projeto_cloud.model.Transacao;

@SpringBootTest
class TransacaoServiceTest {

    @Autowired
    private TransacaoService service;

    @Test
    public void should_create_transacao() throws Exception {
        // Arrange
        int id_cartao = 6;

        Transacao transacao = new Transacao();
        transacao.setDataTransacao(LocalDateTime.parse("2024-08-08T14:40:59"));
        transacao.setValor(250.55);
        transacao.setComerciante("Amazon");

        // Act
        Transacao resultado = service.createTransacao(transacao, id_cartao);
        int id = resultado.getId();

        // Assert
        Assertions.assertNotNull(resultado);
        Assertions.assertNotNull(resultado.getDataTransacao());
        Assertions.assertNotNull(resultado.getValor());
        Assertions.assertNotNull(resultado.getComerciante());
        Assertions.assertEquals(id, transacao.getId());
        Assertions.assertEquals(resultado.getValor(), 250.55);
        Assertions.assertEquals(resultado.getComerciante(), "Amazon");
    }
}