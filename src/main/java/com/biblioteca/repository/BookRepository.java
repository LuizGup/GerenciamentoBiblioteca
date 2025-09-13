// src/main/java/com/biblioteca/repository/BookRepository.java
package com.biblioteca.repository;

import com.biblioteca.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
}