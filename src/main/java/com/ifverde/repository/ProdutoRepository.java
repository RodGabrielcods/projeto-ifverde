package com.ifverde.repository;

import com.ifverde.model.Produto;
import com.ifverde.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByUsuario(Usuario usuario);

    long countByUsuario(Usuario usuario);

    Optional<Produto> findByIdAndUsuario(Long id, Usuario usuario);
}