// src/main/java/com/biblioteca/controller/BookController.java
package com.biblioteca.controller;

import com.biblioteca.entity.Book;
import com.biblioteca.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        // Regra simples para iniciar: status baseado na quantidade
        if (book.getAvailableQuantity() > 0) {
            book.setStatus(com.biblioteca.entity.BookStatus.DISPONIVEL);
        } else {
            book.setStatus(com.biblioteca.entity.BookStatus.INDISPONIVEL);
        }
        Book savedBook = bookRepository.save(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}