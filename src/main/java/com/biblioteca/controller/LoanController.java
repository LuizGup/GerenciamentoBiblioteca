package com.biblioteca.controller;

import com.biblioteca.dto.LoanRequestDTO;
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
    public ResponseEntity<?> createLoan(@Valid @RequestBody LoanRequestDTO loanRequest) {
        Loan newLoan = loanService.createLoan(loanRequest);
        return new ResponseEntity<>(newLoan, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Loan>> getAllLoans(
            @PageableDefault(page = 0, size = 10, sort = "loanDate") Pageable pageable) {
        Page<Loan> loans = loanService.findAllLoans(pageable);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Loan>> getLoansByUserId(@PathVariable Long userId) {
        List<Loan> loans = loanService.findLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<Loan> returnLoan(@PathVariable Long id) {
        Loan returnedLoan = loanService.returnLoan(id);
        return ResponseEntity.ok(returnedLoan);
    }
}