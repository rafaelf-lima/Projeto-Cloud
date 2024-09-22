package br.edu.ibmec.projeto_cloud.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.*;

import br.edu.ibmec.projeto_cloud.model.Cartao;
import br.edu.ibmec.projeto_cloud.model.Cliente;
// import br.edu.ibmec.projeto_cloud.model.Cartao;
import br.edu.ibmec.projeto_cloud.model.Transacao;

@SpringBootTest
class TransacaoServiceTest {

    @Autowired
    private TransacaoService service;

    @Test
    public void should_create_transacao() throws Exception {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNome("Ivo");
        cliente.setCpf("12345678909");
        cliente.setDataNascimento(LocalDate.parse("1997-03-07"));
        cliente.setEmail("asc12345@bb.com");
        cliente.setTelefone("998880000");
        cliente.setEndereco("Rua A apto 104");

        Cartao cartao = new Cartao();
        cartao.setId(1);
        cartao.setNumeroCartao(1234123412345698L);
        cartao.setDataValidade(LocalDate.parse("2026-08-08"));
        cartao.setCvv(147);
        cartao.setLimite(100.0);
        cartao.setSaldo(100.0);
        cartao.setEstaAtivado(true);

        cliente.getCartoes().add(cartao);

        Transacao transacao = new Transacao();
        transacao.setDataTransacao(LocalDateTime.parse("2024-08-08T12:50:59"));
        transacao.setValor(250.55);
        transacao.setComerciante("Amazon");

        // Act
        Transacao resultado = service.createTransacao(transacao,1);
        int id = resultado.getId();

        // Assert
        Assertions.assertNotNull(resultado);
        Assertions.assertNotNull(resultado.getDataTransacao());
        Assertions.assertNotNull(resultado.getValor());
        Assertions.assertNotNull(resultado.getComerciante());
        Assertions.assertEquals(id, transacao.getId());
    }
}    