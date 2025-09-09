package com.biblioteca.service;

import com.biblioteca.entity.Book;
import com.biblioteca.entity.BookStatus;
import com.biblioteca.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public Book createBook(Book book) {
        if (book.getAvailableQuantity() > 0) {
            book.setStatus(BookStatus.DISPONIVEL);
        } else {
            book.setStatus(BookStatus.INDISPONIVEL);
        }
        return bookRepository.save(book);
    }

    @Transactional
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    

}
