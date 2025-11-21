package com.ifverde.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private Double quantidade; // Ex: 100

    private String unidade; // Ex: "unid.", "kg"

    // Novos Campos
    private Double valorTotalEstoque; // Ex: Gastei R$ 200,00 para produzir esses 100 itens

    private Double precoUnitario; // Ex: R$ 2,00 (Calculado automaticamente)
}