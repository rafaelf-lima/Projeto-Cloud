package br.edu.ibmec.projeto_cloud.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ibmec.projeto_cloud.model.Transacao;
import br.edu.ibmec.projeto_cloud.repository.TransacaoRepository;
import br.edu.ibmec.projeto_cloud.service.TransacaoService;

@WebMvcTest(controllers = TransacaoController.class)
@AutoConfigureMockMvc
public class TransacaoControllerTest {

    @MockBean
    private TransacaoRepository transacaoRepository;

    @MockBean
    private TransacaoService transacaoService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void should_get_all_transacoes() throws Exception {
      
        Transacao transacao1 = new Transacao();
        transacao1.setId(1);
        transacao1.setComerciante("Comerciante 1");
        transacao1.setValor(100.0);
        transacao1.setDataTransacao(LocalDateTime.now());

        Transacao transacao2 = new Transacao();
        transacao2.setId(2);
        transacao2.setComerciante("Comerciante 2");
        transacao2.setValor(200.0);
        transacao2.setDataTransacao(LocalDateTime.now());

        List<Transacao> transacoes = Arrays.asList(transacao1, transacao2);

        
        given(transacaoRepository.findAll()).willReturn(transacoes);

      
        this.mvc.perform(MockMvcRequestBuilders.get("/transacao")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].comerciante", is("Comerciante 1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].comerciante", is("Comerciante 2")));
    }

    @Test
    public void should_get_transacao_by_id() throws Exception {
       
        Transacao transacao = new Transacao();
        transacao.setId(1);
        transacao.setComerciante("Comerciante 1");
        transacao.setValor(100.0);
        transacao.setDataTransacao(LocalDateTime.now());

       
        given(transacaoRepository.findById(1)).willReturn(Optional.of(transacao));

       
        this.mvc.perform(MockMvcRequestBuilders.get("/transacao/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comerciante", is("Comerciante 1")));
    }

    @Test
    public void should_return_not_found_when_transacao_does_not_exist() throws Exception {
        
        given(transacaoRepository.findById(1)).willReturn(Optional.empty());

        
        this.mvc.perform(MockMvcRequestBuilders.get("/transacao/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void should_create_transacao() throws Exception {
        
        Transacao transacao = new Transacao();
        transacao.setId(1);
        transacao.setComerciante("Comerciante 1");
        transacao.setValor(100.0);
        transacao.setDataTransacao(LocalDateTime.now());

        
        given(transacaoService.createTransacao(any(Transacao.class), any(Integer.class))).willReturn(transacao);

        
        this.mvc.perform(MockMvcRequestBuilders.post("/transacao/cartao/1")
                .content(this.mapper.writeValueAsString(transacao))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comerciante", is("Comerciante 1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valor", is(100.0)));
    }

    @Test
public void should_return_not_found_when_creating_invalid_transacao() throws Exception {
  
    Transacao transacao = new Transacao();
    transacao.setComerciante("Comerciante inválido");
    transacao.setValor(0.0); // Valor inválido
    transacao.setDataTransacao(LocalDateTime.now());

    
    given(transacaoService.createTransacao(any(Transacao.class), any(Integer.class))).willReturn(null);

    
    this.mvc.perform(MockMvcRequestBuilders.post("/transacao/cartao/1")
            .content(this.mapper.writeValueAsString(transacao)) 
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
}

    }

