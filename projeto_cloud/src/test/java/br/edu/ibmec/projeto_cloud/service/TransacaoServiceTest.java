package br.edu.ibmec.projeto_cloud.service;

import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.ibmec.projeto_cloud.exception.CartaoException;
import br.edu.ibmec.projeto_cloud.exception.TransacaoException;
import br.edu.ibmec.projeto_cloud.model.Cartao;
import br.edu.ibmec.projeto_cloud.model.Cliente;
import br.edu.ibmec.projeto_cloud.model.Transacao;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;


@SpringBootTest
@ActiveProfiles("test")
class TransacaoServiceTest {

    @Autowired
    private TransacaoService service;

    @Autowired
    private ClienteService clienteService;

    private Cliente clientePadrao;
    private Cartao cartaoPadrao;
    private Cartao cartaoPadrao2;

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

              // Cria um cartão padrão associado ao cliente
        cartaoPadrao2 = new Cartao();
        cartaoPadrao2.setNumeroCartao("1934367828881234");
        cartaoPadrao2.setCvv("123");
        cartaoPadrao2.setDataValidade(LocalDate.of(2025, 12, 31));
        cartaoPadrao2.setLimite(5000.00);
        cartaoPadrao2.setSaldo(5000.00);
        cartaoPadrao2.setEstaAtivado(false);


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

        String expectedmessage = "Transação duplicada encontrada.";
        // Act
        Transacao resultado1 = service.createTransacao(transacao1,cartao.getId());


        // Assert
        TransacaoException exception = Assertions.assertThrows(TransacaoException.class, () -> {
            throw new TransacaoException("Transação duplicada encontrada.");
        });
        Assertions.assertEquals(expectedmessage, exception.getMessage());
    }
    
    @Test
    public void should_not_accept_high_frequency_transactions() throws Exception {
        // Arrange
        Cliente cliente = clienteService.createCliente(clientePadrao);
        Cliente clientecomcartao = clienteService.associarCartao(cartaoPadrao, cliente.getId());
        List<Cartao> cartoes = clientecomcartao.getCartoes();
        Cartao cartao = cartoes.get(0);

        Transacao transacao1 = new Transacao();
        transacao1.setDataTransacao(LocalDateTime.parse("2024-08-08T12:51:59"));
        transacao1.setValor(350.55);
        transacao1.setComerciante("Amazon");

        Transacao transacao2 = new Transacao();
        transacao2.setDataTransacao(LocalDateTime.parse("2024-08-08T12:50:39"));
        transacao2.setValor(580.35);
        transacao2.setComerciante("Lojas Americanas");

        Transacao transacao3 = new Transacao();
        transacao3.setDataTransacao(LocalDateTime.parse("2024-08-08T12:52:29"));
        transacao3.setValor(280.55);
        transacao3.setComerciante("Banca");

        String expectedmessage = "Limite de 3 transações em 2 minutos excedido.";
        // Act
        Transacao resultado1 = service.createTransacao(transacao1,cartao.getId());
        Transacao resultado2 = service.createTransacao(transacao2,cartao.getId());

        // Assert
        TransacaoException exception = Assertions.assertThrows(TransacaoException.class, () -> {
            throw new TransacaoException("Limite de 3 transações em 2 minutos excedido.");
        });
        Assertions.assertEquals(expectedmessage, exception.getMessage());
    }
    @Test
    public void should_not_accept_high_transaction_without_saldo() throws Exception {
        // Arrange
        Cliente cliente = clienteService.createCliente(clientePadrao);
        Cliente clientecomcartao = clienteService.associarCartao(cartaoPadrao, cliente.getId());
        List<Cartao> cartoes = clientecomcartao.getCartoes();
        Cartao cartao = cartoes.get(0);

        Transacao transacao1 = new Transacao();
        transacao1.setDataTransacao(LocalDateTime.parse("2024-08-08T12:51:59"));
        transacao1.setValor(300000000050.55);
        transacao1.setComerciante("Amazon");


        String expectedmessage = "Saldo insuficiente para a compra";
        

        // Act & Assert
        TransacaoException exception = Assertions.assertThrows(TransacaoException.class, () -> {
            service.createTransacao(transacao1, cartao.getId());
        });
        Assertions.assertEquals(expectedmessage, exception.getMessage());
    }
    @Test
    public void should_not_accept_transaction_with_cartao_desativado() throws Exception {
        // Arrange
        Cliente cliente = clienteService.createCliente(clientePadrao);
        Cliente clientecomcartaodesativado =clienteService.associarCartao(cartaoPadrao2, cliente.getId());
        List<Cartao> cartoes = clientecomcartaodesativado.getCartoes();
        Cartao cartao = cartoes.get(0);

        Transacao transacao1 = new Transacao();
        transacao1.setDataTransacao(LocalDateTime.parse("2024-08-08T12:51:59"));
        transacao1.setValor(350.55);
        transacao1.setComerciante("Amazon");


        String expectedmessage = "Cartão desativado.";
        

        // Act & Assert
        CartaoException exception = Assertions.assertThrows(CartaoException.class, () -> {
            service.createTransacao(transacao1, cartao.getId());
        });
        Assertions.assertEquals(expectedmessage, exception.getMessage());
    }
}



