package com.ifverde.controller;

import com.ifverde.model.Usuario;
import com.ifverde.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Tela de Login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Tela de Cadastro
    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    // Processar Cadastro
    @PostMapping("/usuario/registrar")
    public String registrarUsuario(Usuario usuario, Model model) {
        // Verifica se já existe
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            model.addAttribute("erro", "Este nome de usuário já está em uso.");
            return "cadastro";
        }

        // Criptografa a senha antes de salvar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);

        return "redirect:/login?sucesso";
    }
}