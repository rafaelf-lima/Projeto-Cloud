package br.edu.ibmec.projeto_cloud.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.ibmec.projeto_cloud.model.Cliente;
// import br.edu.ibmec.projeto_cloud.model.Cartao;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
public class ClienteServiceTest {
    
    @Autowired
    private ClienteService service;

    @Test
    public void should_create_cliente() throws Exception {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setNome("João");
        cliente.setCpf("123.456.789-09");
        cliente.setEmail("joao@teste.com");
        cliente.setTelefone("123456789");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));

        // Act
        Cliente resultado = service.createCliente(cliente);
        UUID id = resultado.getId();

        // Assert
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(id, cliente.getId());
    }

    @Test
    public void should_not_accept_duplicate_cpf() throws Exception {
        // Arrange
        Cliente cliente1 = new Cliente();
        cliente1.setNome("João");
        cliente1.setCpf("123.456.789-09");
        cliente1.setEmail("joao@email.com");
        cliente1.setTelefone("123456789");
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