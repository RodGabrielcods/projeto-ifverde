package com.ifverde.controller;

import com.ifverde.model.Lembrete;
import com.ifverde.repository.LembreteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController define que esta classe é um controlador de API REST
// @RequestMapping define o "endereço" base para este controlador
@RestController
@RequestMapping("/api/lembretes")
public class LembreteController {

    // @Autowired injeta o repositório automaticamente
    @Autowired
    private LembreteRepository lembreteRepository;

    // --- CRUD ---

    // CREATE (Criar)
    // @PostMapping responde a requisições POST
    // @RequestBody pega o JSON enviado e transforma em objeto Lembrete
    @PostMapping
    public ResponseEntity<Lembrete> criarLembrete(@RequestBody Lembrete lembrete) {
        Lembrete novoLembrete = lembreteRepository.save(lembrete);
        return new ResponseEntity<>(novoLembrete, HttpStatus.CREATED);
    }

    // READ (Ler Todos)
    // @GetMapping responde a requisições GET
    @GetMapping
    public List<Lembrete> listarLembretes() {
        return lembreteRepository.findAll();
    }

    // READ (Ler Um)
    // @GetMapping com /{id} busca um lembrete específico
    @GetMapping("/{id}")
    public ResponseEntity<Lembrete> buscarLembretePorId(@PathVariable Long id) {
        return lembreteRepository.findById(id)
                .map(lembrete -> new ResponseEntity<>(lembrete, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // UPDATE (Atualizar)
    // @PutMapping responde a requisições PUT
    @PutMapping("/{id}")
    public ResponseEntity<Lembrete> atualizarLembrete(@PathVariable Long id, @RequestBody Lembrete lembreteAtualizado) {
        return lembreteRepository.findById(id)
                .map(lembrete -> {
                    lembrete.setDescricao(lembreteAtualizado.getDescricao());
                    lembrete.setData(lembreteAtualizado.getData());
                    Lembrete salvo = lembreteRepository.save(lembrete);
                    return new ResponseEntity<>(salvo, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE (Deletar)
    // @DeleteMapping responde a requisições DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLembrete(@PathVariable Long id) {
        if (lembreteRepository.existsById(id)) {
            lembreteRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}