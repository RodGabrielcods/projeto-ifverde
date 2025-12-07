package com.ifverde.repository;

import com.ifverde.model.Produto;
import com.ifverde.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Busca todos os produtos DESTE usuário
    List<Produto> findByUsuario(Usuario usuario);

    // Conta produtos DESTE usuário (para o dashboard)
    long countByUsuario(Usuario usuario);

    // Garante que só podemos editar/excluir um produto se ele for do usuário logado
    Optional<Produto> findByIdAndUsuario(Long id, Usuario usuario);
}