package com.biblioteca.controller;

import com.biblioteca.entity.Book;
import com.biblioteca.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class) // 1. Foca o teste apenas no BookController
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc; // 2. Objeto para simular as requisições HTTP

    @MockitoBean
    private BookService bookService; // 3. Mock do serviço, pois não queremos testá-lo aqui

    @Test
    @DisplayName("Deve retornar status 200 e um livro ao buscar por ID existente")
    void getBookById_WithExistingId_ShouldReturnOk() throws Exception {
        // Arrange (Preparação)
        Book mockBook = new Book();
        mockBook.setId(1L);
        mockBook.setTitle("O Senhor dos Anéis");
        mockBook.setAuthor("J.R.R. Tolkien");

        given(bookService.findBookById(1L)).willReturn(Optional.of(mockBook));

        // Act & Assert (Ação e Verificação)
        mockMvc.perform(get("/api/books/1") // Simula um GET para /api/books/1
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Espera que o status da resposta seja 200 OK
                .andExpect(jsonPath("$.title").value("O Senhor dos Anéis")); // Verifica se o título no corpo da resposta está correto
    }
}