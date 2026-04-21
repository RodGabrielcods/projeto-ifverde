package com.ifverde.controller;

import com.ifverde.model.Despesa;
import com.ifverde.model.Produto;
import com.ifverde.model.Usuario;
import com.ifverde.model.Venda;
import com.ifverde.repository.DespesaRepository;
import com.ifverde.repository.ProdutoRepository;
import com.ifverde.repository.UsuarioRepository;
import com.ifverde.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario getUsuarioLogado(Principal principal) {
        String username = principal.getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @GetMapping("/")
    public String paginaIndex(Model model, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        model.addAttribute("totalProdutos", produtoRepository.countByUsuario(usuario));
        model.addAttribute("totalVendas", vendaRepository.countByUsuario(usuario));
        model.addAttribute("totalDespesas", despesaRepository.countByUsuario(usuario));
        return "index.html";
    }

    @GetMapping("/estoque")
    public String paginaEstoque(Model model, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);
        List<Produto> produtos = produtoRepository.findByUsuario(usuario);

        Double valorTotalEstoque = produtos.stream()
                .map(p -> p.getValorTotalEstoque() != null ? p.getValorTotalEstoque() : 0.0)
                .reduce(0.0, Double::sum);

        model.addAttribute("listaDeProdutos", produtos);
        model.addAttribute("valorTotalEstoque", valorTotalEstoque);
        return "estoque.html";
    }

    @GetMapping("/produto/novo")
    public String formularioNovoProduto(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto-form.html";
    }

    @GetMapping("/produto/editar/{id}")
    public String formularioEditarProduto(@PathVariable Long id, Model model, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        Produto produto = produtoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido ou sem permissão: " + id));

        model.addAttribute("produto", produto);
        return "produto-form.html";
    }

    @PostMapping("/produto/salvar")
    public String salvarProduto(@ModelAttribute Produto produto, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        produto.setUsuario(usuario);

        if (produto.getQuantidade() != null && produto.getQuantidade() > 0 && produto.getPrecoUnitario() != null) {
            double valorTotal = produto.getQuantidade() * produto.getPrecoUnitario();
            produto.setValorTotalEstoque(valorTotal);
        } else {
            produto.setValorTotalEstoque(0.0);
        }

        produtoRepository.save(produto);
        return "redirect:/estoque";
    }

    @GetMapping("/produto/excluir/{id}")
    public String excluirProduto(@PathVariable Long id, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);
        Produto produto = produtoRepository.findByIdAndUsuario(id, usuario).orElse(null);

        if (produto != null) {
            produtoRepository.delete(produto);
        }
        return "redirect:/estoque";
    }

    @GetMapping("/vendas")
    public String paginaVendas(Model model, @RequestParam(required = false) String erro, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        model.addAttribute("listaVendas", vendaRepository.findByUsuario(usuario));
        model.addAttribute("novaVenda", new Venda());
        model.addAttribute("produtosDisponiveis", produtoRepository.findByUsuario(usuario));

        if (erro != null) {
            model.addAttribute("mensagemErro", "Estoque insuficiente para realizar esta venda!");
        }

        return "vendas.html";
    }

    @PostMapping("/venda/salvar")
    public String salvarVenda(@ModelAttribute Venda venda, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        Produto produtoNoBanco = produtoRepository.findByIdAndUsuario(venda.getProduto().getId(), usuario)
                .orElse(null);

        if (produtoNoBanco != null) {
            if (produtoNoBanco.getQuantidade() >= venda.getQuantidadeVendida()) {

                produtoNoBanco.setQuantidade(produtoNoBanco.getQuantidade() - venda.getQuantidadeVendida());
                produtoNoBanco.setValorTotalEstoque(produtoNoBanco.getQuantidade() * produtoNoBanco.getPrecoUnitario());
                produtoRepository.save(produtoNoBanco);

                if (venda.getDescricao() == null || venda.getDescricao().isEmpty()) {
                    venda.setDescricao("Venda de " + venda.getQuantidadeVendida() + " " + produtoNoBanco.getUnidade()
                            + " de " + produtoNoBanco.getNome());
                }

                venda.setProduto(produtoNoBanco);
                venda.setUsuario(usuario);
                vendaRepository.save(venda);

                return "redirect:/vendas";
            } else {
                return "redirect:/vendas?erro=semEstoque";
            }
        }
        return "redirect:/vendas";
    }

    @GetMapping("/venda/excluir/{id}")
    public String excluirVenda(@PathVariable Long id, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);
        vendaRepository.findById(id).ifPresent(venda -> {
            if (venda.getUsuario().getId().equals(usuario.getId())) {
                vendaRepository.deleteById(id);
            }
        });
        return "redirect:/vendas";
    }

    @GetMapping("/despesas")
    public String paginaDespesas(Model model, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);
        model.addAttribute("listaDespesas", despesaRepository.findByUsuario(usuario));
        model.addAttribute("novaDespesa", new Despesa());
        return "despesas.html";
    }

    @PostMapping("/despesa/salvar")
    public String salvarDespesa(@ModelAttribute Despesa despesa, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);
        despesa.setUsuario(usuario);
        despesaRepository.save(despesa);
        return "redirect:/despesas";
    }

    @GetMapping("/despesa/excluir/{id}")
    public String excluirDespesa(@PathVariable Long id, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);
        despesaRepository.findById(id).ifPresent(despesa -> {
            if (despesa.getUsuario().getId().equals(usuario.getId())) {
                despesaRepository.deleteById(id);
            }
        });
        return "redirect:/despesas";
    }

    @GetMapping("/calendario")
    public String paginaCalendario() {
        return "calendario.html";
    }

    @GetMapping("/historico")
    public String paginaHistorico(Model model, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        List<Venda> vendas = vendaRepository.findByUsuario(usuario);
        List<Despesa> despesas = despesaRepository.findByUsuario(usuario);

        Double totalVendas = vendas.stream()
                .map(v -> v.getValor() != null ? v.getValor() : 0.0)
                .reduce(0.0, Double::sum);

        Double totalDespesas = despesas.stream()
                .map(d -> d.getValor() != null ? d.getValor() : 0.0)
                .reduce(0.0, Double::sum);

        Double saldoTotal = totalVendas - totalDespesas;

        Venda maiorVenda = vendas.stream()
                .max((v1, v2) -> Double.compare(v1.getValor() != null ? v1.getValor() : 0,
                        v2.getValor() != null ? v2.getValor() : 0))
                .orElse(null);

        Despesa maiorDespesa = despesas.stream()
                .max((d1, d2) -> Double.compare(d1.getValor() != null ? d1.getValor() : 0,
                        d2.getValor() != null ? d2.getValor() : 0))
                .orElse(null);

        model.addAttribute("listaVendas", vendas);
        model.addAttribute("listaDespesas", despesas);
        model.addAttribute("totalVendas", totalVendas);
        model.addAttribute("totalDespesas", totalDespesas);
        model.addAttribute("saldoTotal", saldoTotal);
        model.addAttribute("maiorVenda", maiorVenda);
        model.addAttribute("maiorDespesa", maiorDespesa);

        return "historico.html";
    }

    @GetMapping("/manual")
    public String paginaManual() {
        return "manual";
    }
}