package com.ifverde.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne; // Importante para a relação
import jakarta.persistence.JoinColumn;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Agora vinculamos diretamente ao Produto
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private Double quantidadeVendida; // Quanto saiu do estoque

    private String descricao; // Ex: "Venda para Restaurante X"
    private Double valor; // Valor total da venda (R$)
    private LocalDate data;
}