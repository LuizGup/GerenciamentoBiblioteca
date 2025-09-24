package com.biblioteca.controller;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.dto.LoanResponseDTO;
import com.biblioteca.entity.Loan;
import com.biblioteca.exception.ResourceNotFoundException;
import com.biblioteca.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponseDTO> createLoan(@Valid @RequestBody LoanRequestDTO loanRequest) {
        LoanResponseDTO newLoanDTO = loanService.createLoan(loanRequest);
        return new ResponseEntity<>(newLoanDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LoanResponseDTO>> getAllLoans(){
        List<LoanResponseDTO> loans = loanService.findAllLoans();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponseDTO>> getLoansByUserId(@PathVariable Long userId) {
        List<LoanResponseDTO> loans = loanService.findLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<LoanResponseDTO> returnLoan(@PathVariable Long id) {
        LoanResponseDTO returnedLoan = loanService.returnLoan(id);
        return ResponseEntity.ok(returnedLoan);
    }
}