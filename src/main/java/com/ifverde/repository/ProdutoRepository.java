package com.ifverde.repository;

import com.ifverde.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

// Esta interface herda de JpaRepository, passando a Entidade (Produto) 
// e o tipo da chave primária (Long)
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Spring Data JPA já nos dá:
    // save() - (Cria e Atualiza)
    // findById() - (Busca 1)
    // findAll() - (Busca todos)
    // deleteById() - (Deleta)
}