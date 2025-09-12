package com.biblioteca.controller;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.entity.Loan;
import com.biblioteca.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    // Simple error response DTO
    public static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }

    @Autowired
    private LoanService loanService;

    @PostMapping
    public ResponseEntity<?> createLoan(@Valid @RequestBody LoanRequestDTO loanRequest) {
        try {
            Loan newLoan = loanService.createLoan(loanRequest);
            return new ResponseEntity<>(newLoan, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<?> returnLoan(@PathVariable Long id) {
        try {
            Loan returnedLoan = loanService.returnLoan(id);
            return ResponseEntity.ok(returnedLoan);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}
