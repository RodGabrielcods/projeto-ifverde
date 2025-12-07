package com.ifverde.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double quantidade;
    private String unidade;
    private Double valorTotalEstoque;
    private Double precoUnitario;

    // NOVO: Vincula o produto ao usuário dono
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}