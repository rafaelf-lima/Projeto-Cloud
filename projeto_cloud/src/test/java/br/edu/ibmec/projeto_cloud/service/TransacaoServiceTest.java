package br.edu.ibmec.projeto_cloud.service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.*;

import br.edu.ibmec.projeto_cloud.model.Cartao;
import br.edu.ibmec.projeto_cloud.model.Cliente;
// import br.edu.ibmec.projeto_cloud.model.Cartao;
import br.edu.ibmec.projeto_cloud.model.Transacao;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class TransacaoServiceTest {

    @Autowired
    private TransacaoService service;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
    // Cria um cliente padrão
    Cliente clientePadrao = new Cliente();
    clientePadrao.setId(1);
    clientePadrao.setNome("Ivo");
    clientePadrao.setCpf("12345678909");
    clientePadrao.setEmail("asc12345@bb.com");
    clientePadrao.setTelefone("998880000");
    clientePadrao.setEndereco("Rua A apto 104");
    clientePadrao.setDataNascimento(LocalDate.parse("1997-03-07"));

    // Salva o cliente no banco de dados
    entityManager.persist(clientePadrao);

    // Cria um cartão padrão associado ao cliente
    Cartao cartaoPadrao = new Cartao();
    cartaoPadrao.setId(1);
    cartaoPadrao.setNumeroCartao("1234123412345698L");
    cartaoPadrao.setCvv("147");
    cartaoPadrao.setDataValidade(LocalDate.parse("2026-08-08"));
    cartaoPadrao.setLimite(100.0);
    cartaoPadrao.setSaldo(100.0);
    cartaoPadrao.setEstaAtivado(true);

    // Associa o cartão ao cliente
    clientePadrao.getCartoes().add(cartaoPadrao);

    // Salva o cartão no banco de dados
    entityManager.persist(cartaoPadrao);
}
    @Test
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        cartao.setNumeroCartao("1234123412345698L");
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
        Transacao resultado = service.createTransacao(transacao, 1);

        // Busca a transação no banco de dados H2
        entityManager.flush(); // Agora o flush deve funcionar corretamente
      
        Transacao savedTransacao = entityManager.find(Transacao.class, resultado.getId());
      
        // Assert
        Assertions.assertNotNull(savedTransacao);
        Assertions.assertNotNull(savedTransacao.getDataTransacao());
        Assertions.assertNotNull(savedTransacao.getValor());
        Assertions.assertNotNull(savedTransacao.getComerciante());
        Assertions.assertEquals(resultado.getId(), savedTransacao.getId());
    }

}    