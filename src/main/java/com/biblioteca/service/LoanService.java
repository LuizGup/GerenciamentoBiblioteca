package com.biblioteca.service;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.entity.*;
import com.biblioteca.exception.ResourceNotFoundException;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.LoanRepository;
import com.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private static final int LOAN_PERIOD_DAYS = 14;

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public Loan createLoan(LoanRequestDTO loanRequest) {
        Users user = userRepository.findById(loanRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + loanRequest.getUserId()));

        Book book = bookRepository.findById(loanRequest.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado com ID: " + loanRequest.getBookId()));

        if (user.getStatus() != UserStatus.ATIVO) {
            throw new IllegalStateException("Usuário não está ativo e não pode realizar empréstimos.");
        }
        if (book.getStatus() != BookStatus.DISPONIVEL || book.getAvailableQuantity() <= 0) {
            throw new IllegalStateException("Livro não está disponível para empréstimo.");
        }
        int activeLoansCount = loanRepository.countByUserAndStatus(user, LoanStatus.ATIVO);
        if (activeLoansCount >= 3) {
            throw new IllegalStateException("Usuário já possui 3 empréstimos ativos. Limite excedido.");
        }

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        if (book.getAvailableQuantity() == 0) {
            book.setStatus(BookStatus.INDISPONIVEL);
        }
        bookRepository.save(book);

        Loan newLoan = new Loan();
        newLoan.setUser(user);
        newLoan.setBook(book);
        newLoan.setLoanDate(LocalDate.now());
        newLoan.setExpectedReturnDate(LocalDate.now().plusDays(LOAN_PERIOD_DAYS));
        newLoan.setStatus(LoanStatus.ATIVO);

        return loanRepository.save(newLoan);
    }

    @Transactional
    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado com ID: " + loanId));

        if (loan.getStatus() == LoanStatus.DEVOLVIDO) {
            throw new IllegalStateException("Este empréstimo já foi devolvido.");
        }

        Book book = loan.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        if (book.getStatus() == BookStatus.INDISPONIVEL) {
            book.setStatus(BookStatus.DISPONIVEL);
        }
        bookRepository.save(book);

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.DEVOLVIDO);

        return loanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    public Page<Loan> findAllLoans(Pageable pageable) {
        return loanRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Loan> findLoansByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + userId);
        }
        return loanRepository.findByUserId(userId);
    }
}