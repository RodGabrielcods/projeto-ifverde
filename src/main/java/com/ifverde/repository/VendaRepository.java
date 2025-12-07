package com.ifverde.repository;

import com.ifverde.model.Usuario;
import com.ifverde.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByUsuario(Usuario usuario);

    long countByUsuario(Usuario usuario);
}