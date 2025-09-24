package com.biblioteca.controller;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.dto.LoanResponseDTO;
import com.biblioteca.entity.*;
import com.biblioteca.entity.Loan;
import com.biblioteca.exception.ResourceNotFoundException;
import com.biblioteca.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve criar um empréstimo e retornar status 201 com o DTO de resposta")
    void createLoan_WithValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        LoanRequestDTO requestDTO = new LoanRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setBookId(1L);

        // Crie o DTO que você espera que o serviço retorne
        LoanResponseDTO responseDTO = new LoanResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStatus(LoanStatus.ATIVO);
        responseDTO.setUserName("Carlos Santana"); // Adicione os dados que você quer verificar

        // O mock agora retorna o LoanResponseDTO
        given(loanService.createLoan(any(LoanRequestDTO.class))).willReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.userName").value("Carlos Santana")); // Verifique um campo do DTO
    }

    @Test
    @DisplayName("Deve retornar status 200 e uma lista vazia quando não há empréstimos")
    void getAllLoans_ShouldReturnOkAndEmptyList() throws Exception {
        given(loanService.findAllLoans()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Deve retornar status 200 e um empréstimo ao buscar por ID do usuário existente com o DTO de resposta")
    void getLoanById_WithExistingUserId_ShouldReturnOk() throws Exception {
        LoanResponseDTO responseDTO = new LoanResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStatus(LoanStatus.ATIVO);

        given(loanService.findLoansByUserId(1L)).willReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/loans/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar status 200 e um retorno empréstimo ao retornar um empréstimo buscando por Id")
    void returnLoanById_WithExistingId_ShouldReturnOk() throws Exception {
        LoanResponseDTO responseDTO = new LoanResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setStatus(LoanStatus.DEVOLVIDO);

        given(loanService.returnLoan(1L)).willReturn(responseDTO);

        mockMvc.perform(patch("/api/loans/1/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DEVOLVIDO"));
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar criar empréstimo para usuário que não existe")
    void createLoan_WithNonExistingUser_ShouldReturnNotFound() throws Exception {

        LoanRequestDTO loanRequestDTO = new LoanRequestDTO();
        loanRequestDTO.setUserId(99L);
        loanRequestDTO.setBookId(1L);

        given(loanService.createLoan(any(LoanRequestDTO.class)))
                .willThrow(new ResourceNotFoundException("Usuário não encontrado"));

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar retornar um empréstimo que não existe")
    void returnLoanById_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        Loan returnedLoan = new Loan();
        returnedLoan.setId(1L);
        returnedLoan.setStatus(LoanStatus.DEVOLVIDO);

        given(loanService.returnLoan(99L))
                .willThrow(new ResourceNotFoundException("empréstimo não encontrado"));

        mockMvc.perform(patch("/api/loans/99/return"))
                .andExpect(status().isNotFound());
    }

}