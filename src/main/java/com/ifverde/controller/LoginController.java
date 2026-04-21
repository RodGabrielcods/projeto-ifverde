package com.ifverde.controller;

import com.ifverde.model.Usuario;
import com.ifverde.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/usuario/registrar")
    public String registrarUsuario(Usuario usuario, Model model) {
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            model.addAttribute("erro", "Este nome de usuário já está em uso.");
            return "cadastro";
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);

        return "redirect:/login?sucesso";
    }

     @GetMapping("/esqueci-senha")
    public String paginaEsqueceuSenha() {
        return "esqueci-senha";
    }

    @PostMapping("/redefinir-senha")
    public String redefinirSenha(@RequestParam String username, @RequestParam String novaSenha, Model model) {
        var usuario = usuarioRepository.findByUsername(username);

        if (usuario.isEmpty()) {
            model.addAttribute("erro", "Usuário não encontrado.");
            return "esqueci-senha";
        }

        Usuario usuarioEncontrado = usuario.get();
        usuarioEncontrado.setPassword(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuarioEncontrado);

        model.addAttribute("sucesso", "Senha redefinida com sucesso! Faça login.");
        return "esqueci-senha";
    }
}