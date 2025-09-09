package com.biblioteca.service;

import com.biblioteca.entity.Book;
import com.biblioteca.entity.BookStatus;
import com.biblioteca.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Transactional
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Transactional
    public Book updateBook(Long id, Book book) {
        Book existingBook = bookRepository.findById(id).orElse(null);

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPublicationYear(book.getPublicationYear());
        existingBook.setAvailableQuantity(book.getAvailableQuantity());

        if (book.getAvailableQuantity() > 0) {
            existingBook.setStatus(BookStatus.DISPONIVEL);
        } else {
            existingBook.setStatus(BookStatus.INDISPONIVEL);
        }
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

}
