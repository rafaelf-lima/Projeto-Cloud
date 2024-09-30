package br.edu.ibmec.projeto_cloud.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ibmec.projeto_cloud.model.Cliente;
import br.edu.ibmec.projeto_cloud.repository.ClienteRepository;
import br.edu.ibmec.projeto_cloud.service.ClienteService;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ClienteController.class)
@AutoConfigureMockMvc
public class ClienteControllerTest {

    @MockBean
    private ClienteRepository clienteRepository;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders
                   .webAppContextSetup(context)
                   .build();
    }

    @Test
    public void should_create_cliente() throws Exception {
        
        Cliente cliente = new Cliente();
        cliente.setId(10);  
        cliente.setNome("João Silva"); 
        cliente.setCpf("123.456.789-09");  
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));  
        cliente.setEmail("joao.silva@example.com"); 
        cliente.setTelefone("(11)98765-4321"); 
        cliente.setEndereco("Rua Exemplo, 123");  

        
        given(this.clienteService.createCliente(any(Cliente.class))).willReturn(cliente);

        
        this.mvc.perform(post("/cliente")
            .content(this.mapper.writeValueAsString(cliente))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())  
            .andExpect(jsonPath("$.id", is(10)))
            .andExpect(jsonPath("$.nome", is("João Silva")))
            .andExpect(jsonPath("$.cpf", is("123.456.789-09")))
            .andExpect(jsonPath("$.email", is("joao.silva@example.com")))
            .andExpect(jsonPath("$.telefone", is("(11)98765-4321")))
            .andExpect(jsonPath("$.endereco", is("Rua Exemplo, 123")));
    }

    @Test
    public void should_get_cliente() throws Exception {
       
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNome("João");
        cliente.setEmail("joao@example.com");

       
        given(this.clienteRepository.findById(1)).willReturn(Optional.of(cliente));

        
        this.mvc.perform(MockMvcRequestBuilders.get("/cliente/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.nome", is("João")));
    }

    @Test
    public void should_get_cliente_with_not_found() throws Exception {
        
        given(this.clienteRepository.findById(1)).willReturn(Optional.empty());

       
        this.mvc.perform(MockMvcRequestBuilders.get("/cliente/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
