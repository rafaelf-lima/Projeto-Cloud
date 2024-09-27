package br.edu.ibmec.projeto_cloud.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        cartao.setNumeroCartao("1234123412345698");
        cartao.setDataValidade(LocalDate.parse("2026-08-08"));
        cartao.setCvv("147");
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
    @Test
    public void should_throw_exception_when_transacao_exceeds_cartao_limit() throws Exception {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNome("Ivo");
        cliente.setCpf("123-456-789-09");
        cliente.setDataNascimento(LocalDate.parse("1997-03-07"));
        cliente.setEmail("asc12345@bb.com");
        cliente.setTelefone("998880000");
        cliente.setEndereco("Rua A apto 104");

        Cartao cartao = new Cartao();
        cartao.setId(1);
        cartao.setNumeroCartao("1234123412345698");
        cartao.setDataValidade(LocalDate.parse("2026-08-08"));
        cartao.setCvv("147");
        cartao.setLimite(100.0); // Limite do cartão
        cartao.setSaldo(100.0);
        cartao.setEstaAtivado(true); // Cartão ativado

        // Simule o comportamento dos repositórios
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(cartaoRepository.save(any(Cartao.class))).thenReturn(cartao);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Associa o cartão ao cliente
        cliente.associarCartao(cartao); // Usando o método de associação

        // Agora, tente criar uma transação com um valor maior que o limite
        Transacao transacao = new Transacao();
        transacao.setValor(150.0); // Valor da transação maior que o limite
        transacao.setComerciante("Amazon");
        transacao.setDataTransacao(LocalDateTime.now());

        // Simule a busca do cartão
        when(cartaoRepository.findById(1)).thenReturn(Optional.of(cartao));

        // Act & Assert
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            service.createTransacao(transacao, 1);
        });

        Assertions.assertEquals("Limite do cartão insuficiente.", exception.getMessage());
    }

}    