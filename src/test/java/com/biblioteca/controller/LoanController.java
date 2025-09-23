package com.biblioteca.controller;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.entity.*;
import com.biblioteca.entity.Loan;
import com.biblioteca.exception.ResourceNotFoundException;
import com.biblioteca.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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
    @DisplayName("Deve criar um empréstimo e retornar status 201")
    void createLoan_WithValidData_ShouldReturnCreated() throws Exception {

        LoanRequestDTO NewloanRequestDTO = new LoanRequestDTO();
        NewloanRequestDTO.setUserId(1L);
        NewloanRequestDTO.setBookId(1L);

        Loan newLoan = new Loan();
        newLoan.setId(1L);
        newLoan.setStatus(LoanStatus.ATIVO);

        given(loanService.createLoan(any(LoanRequestDTO.class))).willReturn(newLoan);

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(NewloanRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar status 200 e uma lista vazia quando não há empréstimos")
    void getAllLoans_ShouldReturnOkAndEmptyList() throws Exception {
        given(loanService.findAllLoans(any(Pageable.class))).willReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }


}