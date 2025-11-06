package com.ifverde.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data; // Importa do Lombok

// @Entity informa ao JPA que esta classe é uma tabela no banco
// @Data (Lombok) cria getters, setters, toString, etc. automaticamente
@Entity
@Data 
public class Produto {

    // @Id define a chave primária
    // @GeneratedValue diz que o ID será gerado automaticamente
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Double quantidade;
    private String unidade; // Ex: "unid.", "kg", "caixa"
}