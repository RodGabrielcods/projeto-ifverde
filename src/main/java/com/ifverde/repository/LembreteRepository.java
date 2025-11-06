package com.ifverde.repository;

import com.ifverde.model.Lembrete;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface LembreteRepository extends JpaRepository<Lembrete, Long> {
    
    // Podemos criar buscas customizadas!
    // Ex: Buscar todos os lembretes de um mês específico
    List<Lembrete> findByDataBetween(LocalDate inicio, LocalDate fim);
}