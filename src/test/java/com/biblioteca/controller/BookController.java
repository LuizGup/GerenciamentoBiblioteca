package com.biblioteca.controller;

import com.biblioteca.entity.Book;
import com.biblioteca.exception.ResourceNotFoundException;
import com.biblioteca.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean // 2. Substituir @MockBean por @MockitoBean
    private BookService bookService;

    @Test
    @DisplayName("Deve retornar status 200 e um livro ao buscar por ID existente")
    void getBookById_WithExistingId_ShouldReturnOk() throws Exception {
        Book mockBook = new Book();
        mockBook.setId(1L);
        mockBook.setTitle("O Senhor dos Anéis");
        given(bookService.findBookById(1L)).willReturn(Optional.of(mockBook));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("O Senhor dos Anéis"));
    }

    @Test
    @DisplayName("Deve retornar status 200 e uma lista vazia quando não há livros")
    void getAllBooks_ShouldReturnOkAndEmptyList() throws Exception {
        given(bookService.findAllBooks()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Deve criar um livro e retornar status 201")
    void createBook_WithValidData_ShouldReturnCreated() throws Exception {
        Book newBook = new Book();
        newBook.setId(1L);
        newBook.setTitle("O Hobbit");
        given(bookService.createBook(any(Book.class))).willReturn(newBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("O Hobbit"));
    }

    @Test
    @DisplayName("Deve atualizar um livro existente e retornar status 200")
    void updateBook_WithExistingId_ShouldReturnOk() throws Exception {
        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("O Hobbit 2");
        given(bookService.updateBook(anyLong(), any(Book.class))).willReturn(updatedBook);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("O Hobbit 2"));
    }

    @Test
    @DisplayName("Deve deletar um livro existente e retornar status 204")
    void deleteBook_WithExistingId_ShouldReturnNoContent() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar deletar um livro que não existe")
    void deleteBook_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Livro não encontrado")).when(bookService).deleteBook(99L);

        mockMvc.perform(delete("/api/books/99"))
                .andExpect(status().isNotFound());
    }
}