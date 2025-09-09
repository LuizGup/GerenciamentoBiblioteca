// src/main/java/com/biblioteca/repository/BookRepository.java
package com.biblioteca.repository;

import com.biblioteca.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}