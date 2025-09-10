package com.biblioteca.service;

import com.biblioteca.entity.Book;
import com.biblioteca.entity.BookStatus;
import com.biblioteca.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.biblioteca.exception.ResourceNotFoundException;

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

    @Transactional(readOnly = true)
    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Page<Book> findAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Transactional
    public Book updateBook(Long id, Book book) {
        Book existingBook = findBookById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
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
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
    }

}
