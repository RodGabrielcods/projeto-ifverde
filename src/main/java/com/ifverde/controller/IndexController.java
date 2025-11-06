package com.ifverde.controller;

import com.ifverde.model.Produto;
import com.ifverde.repository.ProdutoRepository;
import com.ifverde.repository.LembreteRepository;
// ... importe os outros repositórios (DespesaRepository, VendaRepository)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.List;

@Controller // Note: @Controller, e não @RestController
public class IndexController {

    // Injetamos os repositórios que vamos usar para buscar dados
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private LembreteRepository lembreteRepository;

    // ... injete os outros repositórios aqui ...

    @GetMapping("/")
    public String paginaIndex(Model model) {
        // 'Model' é o objeto que usamos para enviar dados para o Thymeleaf
        model.addAttribute("totalProdutos", produtoRepository.count());
        model.addAttribute("totalLembretes", lembreteRepository.count());
        
        // Retorna o nome do arquivo "index.html"
        return "index.html"; 
    }

    @GetMapping("/calendario")
    public String paginaCalendario(Model model) {
        // 1. Busca todos os lembretes salvos no banco
        // 2. Adiciona essa lista ao 'model' com o nome "listaLembretes"
        model.addAttribute("listaLembretes", lembreteRepository.findAll());
        
        // Retorna o "calendario.html"
        return "calendario.html";
    }

    @GetMapping("/estoque")
    public String paginaEstoque(Model model) {
        // 1. Busca todos os produtos salvos no banco
        List<Produto> produtos = produtoRepository.findAll();
        
        // 2. Envia a lista para o HTML sob o nome "listaDeProdutos"
        model.addAttribute("listaDeProdutos", produtos);
        
        // Retorna "estoque.html"
        return "estoque.html";
    }

    @GetMapping("/despesas")
    public String paginaDespesas(Model model) {
        // model.addAttribute("listaDespesas", despesaRepository.findAll());
        return "despesas.html";
    }

    @GetMapping("/historico")
    public String paginaHistorico(Model model) {
        // model.addAttribute("listaVendas", vendaRepository.findAll());
        // model.addAttribute("listaDespesas", despesaRepository.findAll());
        return "historico.html";
    }
}