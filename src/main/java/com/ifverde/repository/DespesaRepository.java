package com.ifverde.repository;

import com.ifverde.model.Despesa;
import com.ifverde.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    List<Despesa> findByUsuario(Usuario usuario);

    long countByUsuario(Usuario usuario);
}