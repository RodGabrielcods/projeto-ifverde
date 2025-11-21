package com.ifverde.controller;

import com.ifverde.model.Despesa;
import com.ifverde.model.Produto;
import com.ifverde.model.Venda;
import com.ifverde.repository.DespesaRepository;
import com.ifverde.repository.ProdutoRepository;
import com.ifverde.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private VendaRepository vendaRepository;

    // --- PÁGINA INICIAL ---
    @GetMapping("/")
    public String paginaIndex(Model model) {
        model.addAttribute("totalProdutos", produtoRepository.count());
        model.addAttribute("totalVendas", vendaRepository.count());
        model.addAttribute("totalDespesas", despesaRepository.count());
        return "index.html";
    }

    // --- ESTOQUE ---
    @GetMapping("/estoque")
    public String paginaEstoque(Model model) {
        model.addAttribute("listaDeProdutos", produtoRepository.findAll());
        return "estoque.html";
    }

    @GetMapping("/produto/novo")
    public String formularioNovoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto-form.html";
    }

    @GetMapping("/produto/editar/{id}")
    public String formularioEditarProduto(@PathVariable Long id, Model model) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido: " + id));
        model.addAttribute("produto", produto);
        return "produto-form.html";
    }

    @PostMapping("/produto/salvar")
    public String salvarProduto(@ModelAttribute Produto produto) {
        // LÓGICA DE CÁLCULO DO PREÇO UNITÁRIO
        if (produto.getQuantidade() != null && produto.getQuantidade() > 0 && produto.getValorTotalEstoque() != null) {
            double unitario = produto.getValorTotalEstoque() / produto.getQuantidade();
            produto.setPrecoUnitario(unitario);
        } else {
            produto.setPrecoUnitario(0.0);
        }

        produtoRepository.save(produto);
        return "redirect:/estoque";
    }

    @GetMapping("/produto/excluir/{id}")
    public String excluirProduto(@PathVariable Long id) {
        produtoRepository.deleteById(id);
        return "redirect:/estoque";
    }

    // --- VENDAS ---
    @GetMapping("/vendas")
    public String paginaVendas(Model model, @RequestParam(required = false) String erro) {
        model.addAttribute("listaVendas", vendaRepository.findAll());
        model.addAttribute("novaVenda", new Venda());
        model.addAttribute("produtosDisponiveis", produtoRepository.findAll());

        if (erro != null) {
            model.addAttribute("mensagemErro", "Estoque insuficiente para realizar esta venda!");
        }

        return "vendas.html";
    }

    @PostMapping("/venda/salvar")
    public String salvarVenda(@ModelAttribute Venda venda) {
        Produto produtoNoBanco = produtoRepository.findById(venda.getProduto().getId()).orElse(null);

        if (produtoNoBanco != null) {
            if (produtoNoBanco.getQuantidade() >= venda.getQuantidadeVendida()) {

                // Baixa no Estoque
                produtoNoBanco.setQuantidade(produtoNoBanco.getQuantidade() - venda.getQuantidadeVendida());

                // Recalcula o valor total do estoque (já que diminuímos a quantidade)
                // Valor Total Atual = Quantidade Atual * Preço Unitário
                produtoNoBanco.setValorTotalEstoque(produtoNoBanco.getQuantidade() * produtoNoBanco.getPrecoUnitario());

                produtoRepository.save(produtoNoBanco);

                // Preenche descrição se vazia
                if (venda.getDescricao() == null || venda.getDescricao().isEmpty()) {
                    venda.setDescricao("Venda de " + venda.getQuantidadeVendida() + " " + produtoNoBanco.getUnidade()
                            + " de " + produtoNoBanco.getNome());
                }

                venda.setProduto(produtoNoBanco);
                vendaRepository.save(venda);

                return "redirect:/vendas";
            } else {
                return "redirect:/vendas?erro=semEstoque";
            }
        }
        return "redirect:/vendas";
    }

    @GetMapping("/venda/excluir/{id}")
    public String excluirVenda(@PathVariable Long id) {
        vendaRepository.deleteById(id);
        return "redirect:/vendas";
    }

    // --- DESPESAS ---
    @GetMapping("/despesas")
    public String paginaDespesas(Model model) {
        model.addAttribute("listaDespesas", despesaRepository.findAll());
        model.addAttribute("novaDespesa", new Despesa());
        return "despesas.html";
    }

    @PostMapping("/despesa/salvar")
    public String salvarDespesa(@ModelAttribute Despesa despesa) {
        despesaRepository.save(despesa);
        return "redirect:/despesas";
    }

    @GetMapping("/despesa/excluir/{id}")
    public String excluirDespesa(@PathVariable Long id) {
        despesaRepository.deleteById(id);
        return "redirect:/despesas";
    }

    // --- CALENDÁRIO ---
    @GetMapping("/calendario")
    public String paginaCalendario() {
        return "calendario.html";
    }

    // --- HISTÓRICO ---
    @GetMapping("/historico")
    public String paginaHistorico(Model model) {
        model.addAttribute("listaVendas", vendaRepository.findAll());
        model.addAttribute("listaDespesas", despesaRepository.findAll());
        return "historico.html";
    }
}