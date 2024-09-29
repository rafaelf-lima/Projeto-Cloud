package br.edu.ibmec.projeto_cloud.service;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.ibmec.projeto_cloud.model.Cartao;
import br.edu.ibmec.projeto_cloud.model.Cliente;
import br.edu.ibmec.projeto_cloud.repository.CartaoRepository;
import br.edu.ibmec.projeto_cloud.repository.ClienteRepository;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles("test")
public class ClienteServiceTest {
    
    @Autowired
    private ClienteService service;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CartaoRepository cartaoRepository;

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

        // Salva o cliente no banco de dados
        clienteRepository.save(clientePadrao);

        // Cria um cartão padrão associado ao cliente
        cartaoPadrao = new Cartao();
        cartaoPadrao.setNumeroCartao("1234567812345678");
        cartaoPadrao.setCvv("123");
        cartaoPadrao.setDataValidade(LocalDate.of(2025, 12, 31));
        cartaoPadrao.setLimite(5000.00);
        cartaoPadrao.setSaldo(5000.00);
        cartaoPadrao.setEstaAtivado(true);

        // Associa o cartão ao cliente
        clientePadrao.associarCartao(cartaoPadrao);

        // Salva o cartão no banco de dados
        cartaoRepository.save(cartaoPadrao);
    }    
    

    @Test
    public void should_create_cliente() throws Exception {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setNome("João");
        cliente.setCpf("987.654.321-00");
        cliente.setEmail("joao@teste.com");
        cliente.setEndereco("Rua 1 casa B");
        cliente.setTelefone("(21)12345-6789");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));

        // Act
        Cliente resultado = service.createCliente(cliente);
        int id = resultado.getId();

        // Assert
        Assertions.assertNotNull(resultado);
        Assertions.assertNotNull(resultado.getNome());
        Assertions.assertNotNull(resultado.getCpf());
        Assertions.assertNotNull(resultado.getEmail());
        Assertions.assertNotNull(resultado.getEndereco());
        Assertions.assertNotNull(resultado.getTelefone());
        Assertions.assertNotNull(resultado.getDataNascimento());
        Assertions.assertEquals(id, cliente.getId());
    }

    @Test
    public void should_not_accept_duplicate_cpf() throws Exception {
        // Arrange
        Cliente cliente1 = new Cliente();
        cliente1.setNome("João");
        cliente1.setCpf("584.232.147-53");
        cliente1.setEmail("joao@email.com");
        cliente1.setTelefone("(91)12345-6789");
        cliente1.setEndereco("Rua paulo cesar de andrade 232");
        cliente1.setDataNascimento(LocalDate.of(1990, 1, 1));
        service.createCliente(cliente1);

        Cliente cliente2 = new Cliente();
        cliente2.setNome("Joel");
        cliente2.setCpf("123.456.789-09");
        cliente2.setEmail("joel@email.com");
        cliente2.setTelefone("993456789");
        cliente2.setDataNascimento(LocalDate.of(1990, 12, 11));

        // Act & Assert
        Assertions.assertThrows(Exception.class, () -> {
            service.createCliente(cliente2);
        });
    }

    @Test
    public void should_not_accept_invalid_cpf() throws Exception {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setNome("Joana");
        cliente.setCpf("123.456.111-11");
        cliente.setEmail("joana@teste.com.br");
        cliente.setTelefone("123456888");
        cliente.setEndereco("Rua 2 casa B");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
        
        // Act & Assert
        Assertions.assertThrows(Exception.class, () -> {
            service.createCliente(cliente);
        });
    }

    // @Test
    //     public void should_associar_cartao() {

    // }
}