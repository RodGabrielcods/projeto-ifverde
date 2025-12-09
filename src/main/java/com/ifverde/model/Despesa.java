package com.ifverde.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private Double valor;
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}