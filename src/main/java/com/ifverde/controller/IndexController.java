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

    // Método auxiliar para pegar o usuário logado
    private Usuario getUsuarioLogado(Principal principal) {
        String username = principal.getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // --- PÁGINA INICIAL ---
    @GetMapping("/")
    public String paginaIndex(Model model, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        // Agora usamos countByUsuario para contar apenas os dados dele
        model.addAttribute("totalProdutos", produtoRepository.countByUsuario(usuario));
        model.addAttribute("totalVendas", vendaRepository.countByUsuario(usuario));
        model.addAttribute("totalDespesas", despesaRepository.countByUsuario(usuario));
        return "index.html";
    }

    // --- ESTOQUE ---
    @GetMapping("/estoque")
    public String paginaEstoque(Model model, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);
        // Busca apenas produtos do usuário
        model.addAttribute("listaDeProdutos", produtoRepository.findByUsuario(usuario));
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

        // Só permite editar se o produto for do usuário (Segurança)
        Produto produto = produtoRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new IllegalArgumentException("Produto inválido ou sem permissão: " + id));

        model.addAttribute("produto", produto);
        return "produto-form.html";
    }

    @PostMapping("/produto/salvar")
    public String salvarProduto(@ModelAttribute Produto produto, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        // Associa o produto ao usuário logado
        produto.setUsuario(usuario);

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
    public String excluirProduto(@PathVariable Long id, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);
        // Verifica se o produto é do usuário antes de deletar
        Produto produto = produtoRepository.findByIdAndUsuario(id, usuario).orElse(null);

        if (produto != null) {
            produtoRepository.delete(produto);
        }
        return "redirect:/estoque";
    }

    // --- VENDAS ---
    @GetMapping("/vendas")
    public String paginaVendas(Model model, @RequestParam(required = false) String erro, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        model.addAttribute("listaVendas", vendaRepository.findByUsuario(usuario));
        model.addAttribute("novaVenda", new Venda());
        // Só mostra no select os produtos DESTE usuário
        model.addAttribute("produtosDisponiveis", produtoRepository.findByUsuario(usuario));

        if (erro != null) {
            model.addAttribute("mensagemErro", "Estoque insuficiente para realizar esta venda!");
        }

        return "vendas.html";
    }

    @PostMapping("/venda/salvar")
    public String salvarVenda(@ModelAttribute Venda venda, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);

        // Busca o produto no banco, garantindo que pertence ao usuário logado
        Produto produtoNoBanco = produtoRepository.findByIdAndUsuario(venda.getProduto().getId(), usuario)
                .orElse(null);

        if (produtoNoBanco != null) {
            if (produtoNoBanco.getQuantidade() >= venda.getQuantidadeVendida()) {

                // Baixa no Estoque
                produtoNoBanco.setQuantidade(produtoNoBanco.getQuantidade() - venda.getQuantidadeVendida());
                produtoNoBanco.setValorTotalEstoque(produtoNoBanco.getQuantidade() * produtoNoBanco.getPrecoUnitario());
                produtoRepository.save(produtoNoBanco);

                if (venda.getDescricao() == null || venda.getDescricao().isEmpty()) {
                    venda.setDescricao("Venda de " + venda.getQuantidadeVendida() + " " + produtoNoBanco.getUnidade()
                            + " de " + produtoNoBanco.getNome());
                }

                venda.setProduto(produtoNoBanco);
                venda.setUsuario(usuario); // Associa venda ao usuário
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
        // Segurança simples: só deleta se encontrar a venda para esse usuário (você
        // poderia criar findByIdAndUsuario em VendaRepository também, mas aqui faremos
        // via verificação manual rápida)
        vendaRepository.findById(id).ifPresent(venda -> {
            if (venda.getUsuario().getId().equals(usuario.getId())) {
                vendaRepository.deleteById(id);
            }
        });
        return "redirect:/vendas";
    }

    // --- DESPESAS ---
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
        despesa.setUsuario(usuario); // Associa ao usuário
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

    // --- CALENDÁRIO ---
    @GetMapping("/calendario")
    public String paginaCalendario() {
        return "calendario.html";
    }

    // --- HISTÓRICO ---
    @GetMapping("/historico")
    public String paginaHistorico(Model model, Principal principal) {
        Usuario usuario = getUsuarioLogado(principal);
        model.addAttribute("listaVendas", vendaRepository.findByUsuario(usuario));
        model.addAttribute("listaDespesas", despesaRepository.findByUsuario(usuario));
        return "historico.html";
    }
}