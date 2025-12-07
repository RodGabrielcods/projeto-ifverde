package com.ifverde.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private Double quantidadeVendida;
    private String descricao;
    private Double valor;
    private LocalDate data;

    // NOVO: Vincula a venda ao usuário dono
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}