package br.com.valueprojects.mock_spring.controller;

import br.com.valueprojects.mock_spring.repository.JogoDao;
import org.springframework.web.bind.annotation.*;

import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.business.Juiz;

import java.util.ArrayList;
import java.util.List;

@RestController

@RequestMapping("/jogos")
public class JogoController {

    private Juiz juiz = new Juiz();

    private JogoDao dao;

    public JogoController(JogoDao dao) {
        this.dao = dao;
    }

    // Criar um novo jogo
    @PostMapping("/criar")
    public Jogo criarJogo(@RequestBody String descricao) {
        Jogo jogo = new Jogo(descricao);
        this.dao.salva(jogo);
        return jogo;
    }

   
    // Julgar um jogo
    @PostMapping("/{id}/julgar")
    public String julgarJogo(@PathVariable int id) {
        Jogo jogo = dao.emAndamento().get(id);
        juiz.julga(jogo);
        double primeiroColocado = juiz.getPrimeiroColocado();
        double ultimoColocado = juiz.getUltimoColocado();
        return "Primeiro colocado: " + primeiroColocado + ", Último colocado: " + ultimoColocado;
    }

    // Listar todos os jogos
    @GetMapping
    public List<Jogo> listarJogos() {
        return this.dao.emAndamento();
    }

}

