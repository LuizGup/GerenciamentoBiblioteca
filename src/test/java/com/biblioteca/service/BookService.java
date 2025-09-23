package com.biblioteca.service;

import com.biblioteca.entity.Book;
import com.biblioteca.entity.BookStatus;
import com.biblioteca.entity.Users;
import com.biblioteca.exception.ResourceNotFoundException;
import com.biblioteca.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("O Senhor dos Anéis");
        book.setAuthor("J.R.R. Tolkien");
        book.setIsbn("978-0618640157");
        book.setAvailableQuantity(5);
        book.setStatus(BookStatus.DISPONIVEL);
    }

    @Test
    @DisplayName("Deve criar um livro com status DISPONIVEL quando a quantidade é positiva")
    void createBook_WithPositiveQuantity_ShouldSetStatusToAvailable() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book savedBook = bookService.createBook(book);

        assertNotNull(savedBook);
        assertEquals(BookStatus.DISPONIVEL, savedBook.getStatus());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Deve buscar e retornar todos os livros")
    void findAllBooks_Success() {

        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> result = bookService.findAllBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve criar um livro com status INDISPONIVEL quando a quantidade é zero")
    void createBook_WithZeroQuantity_ShouldSetStatusToUnavailable() {
        // Arrange
        book.setAvailableQuantity(0);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book savedBook = bookService.createBook(book);

        // Assert
        assertNotNull(savedBook);
        assertEquals(BookStatus.INDISPONIVEL, savedBook.getStatus());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Deve atualizar um livro com sucesso")
    void updateBook_Success() {
        // Arrange
        Book bookDetails = new Book();
        bookDetails.setTitle("As Duas Torres");
        bookDetails.setAvailableQuantity(3);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book updatedBook = bookService.updateBook(1L, bookDetails);

        // Assert
        assertNotNull(updatedBook);
        assertEquals("As Duas Torres", updatedBook.getTitle());
        assertEquals(3, updatedBook.getAvailableQuantity());
        assertEquals(BookStatus.DISPONIVEL, updatedBook.getStatus());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("Deve atualizar o status para INDISPONIVEL quando a quantidade for zero")
    void updateBook_WithZeroQuantity_ShouldSetStatusToUnavailable() {
        // Arrange
        Book bookDetails = new Book();
        bookDetails.setTitle("As Duas Torres");
        bookDetails.setAvailableQuantity(0);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        Book updatedBook = bookService.updateBook(1L, bookDetails);

        // Assert
        assertEquals(BookStatus.INDISPONIVEL, updatedBook.getStatus());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um livro que não existe")
    void updateBook_WhenBookNotFound_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.updateBook(99L, new Book());
        });
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Deve deletar um livro com sucesso")
    void deleteBook_Success() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        // Act
        bookService.deleteBook(1L);

        // Assert
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar um livro que não existe")
    void deleteBook_WhenBookNotFound_ShouldThrowException() {
        // Arrange
        when(bookRepository.existsById(99L)).thenReturn(false);

        // Act & Assert: Verifique se a exceção correta é lançada
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.deleteBook(99L);
        });

        // Verify: Garanta que o método de deleção nunca foi chamado
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve retornar uma lista de livros")
    void findAllBooks_ShouldReturnListOfBooks() {
        // Arrange
        when(bookRepository.findAll()).thenReturn(List.of(book));

        // Act
        List<Book> result = bookService.findAllBooks();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("O Senhor dos Anéis", result.get(0).getTitle());
        verify(bookRepository, times(1)).findAll();
    }
}

