package br.edu.ibmec.projeto_cloud.service;

import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.ibmec.projeto_cloud.model.Cartao;
import br.edu.ibmec.projeto_cloud.model.Cliente;
import br.edu.ibmec.projeto_cloud.model.Transacao;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;


@SpringBootTest
class TransacaoServiceTest {

    @Autowired
    private TransacaoService service;

    @Autowired
    private ClienteService clienteService;

    private Cliente clientePadrao;
    private Cartao cartaoPadrao;

    @BeforeEach
    public void setup() {
        // Cria um cliente padrão
        clientePadrao = new Cliente();
        clientePadrao.setNome("Lua");
        clientePadrao.setCpf("123.456.789-09");
        clientePadrao.setEmail("lua@cat.com");
        clientePadrao.setTelefone("(21)99888-0000");
        clientePadrao.setEndereco("Rua A apto 104");
        clientePadrao.setDataNascimento(LocalDate.parse("2000-04-20"));


        // Cria um cartão padrão associado ao cliente
        cartaoPadrao = new Cartao();
        cartaoPadrao.setNumeroCartao("1934567818845678");
        cartaoPadrao.setCvv("123");
        cartaoPadrao.setDataValidade(LocalDate.of(2025, 12, 31));
        cartaoPadrao.setLimite(5000.00);
        cartaoPadrao.setSaldo(5000.00);
        cartaoPadrao.setEstaAtivado(true);


    }    


    @Test
    public void should_create_transacao() throws Exception {
        // Arrange
        Cliente cliente = clienteService.createCliente(clientePadrao);
        Cliente clientecomcartao = clienteService.associarCartao(cartaoPadrao, cliente.getId());
        List<Cartao> cartoes = clientecomcartao.getCartoes();
        Cartao cartao = cartoes.get(0);

        Transacao transacao = new Transacao();
        transacao.setDataTransacao(LocalDateTime.parse("2024-08-08T12:50:59"));
        transacao.setValor(250.55);
        transacao.setComerciante("Amazon");

        // Act
        Transacao resultado = service.createTransacao(transacao,cartao.getId());


        // Assert
        Assertions.assertNotNull(resultado);
        Assertions.assertNotNull(resultado.getDataTransacao());
        Assertions.assertNotNull(resultado.getValor());
        Assertions.assertNotNull(resultado.getComerciante());
        Assertions.assertEquals(resultado.getId(), resultado.getId());
    }

    @Test
    public void should_not_create_duplicate_transacao() throws Exception {
        // Arrange
        Cliente cliente = clienteService.createCliente(clientePadrao);
        Cliente clientecomcartao = clienteService.associarCartao(cartaoPadrao, cliente.getId());
        List<Cartao> cartoes = clientecomcartao.getCartoes();
        Cartao cartao = cartoes.get(0);

        Transacao transacao1 = new Transacao();
        transacao1.setDataTransacao(LocalDateTime.parse("2024-08-08T12:50:59"));
        transacao1.setValor(250.55);
        transacao1.setComerciante("Amazon");

        Transacao transacao2 = new Transacao();
        transacao2.setDataTransacao(LocalDateTime.parse("2024-08-08T12:50:59"));
        transacao2.setValor(250.55);
        transacao2.setComerciante("Amazon");
        
        // Act
        Transacao resultado1 = service.createTransacao(transacao1,cartao.getId());


        // Assert
        Assertions.assertThrowsExactly(Exception.class, () -> {
            service.createTransacao(transacao2,cartao.getId());
        });
    }
}


