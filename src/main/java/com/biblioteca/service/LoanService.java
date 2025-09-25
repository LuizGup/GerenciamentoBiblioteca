package com.biblioteca.service;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.dto.LoanResponseDTO;
import com.biblioteca.entity.*;
import com.biblioteca.exception.ResourceNotFoundException;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.LoanRepository;
import com.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private static final int LOAN_PERIOD_DAYS = 14;

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;

    @Transactional()
    public LoanResponseDTO createLoan(LoanRequestDTO loanRequest) {
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

        Loan savedLoan = loanRepository.save(newLoan); // Salva primeiro

        return convertToResponseDTO(savedLoan);
    }

    @Transactional
    public LoanResponseDTO returnLoan(Long loanId) {
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

        Loan savedLoan = loanRepository.save(loan);

        return convertToResponseDTO(savedLoan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponseDTO> findAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoanResponseDTO> findLoansByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + userId);
        }
        return loanRepository.findByUserId(userId)
                .stream() // Converte a lista para uma stream
                .map(this::convertToResponseDTO) // Aplica a conversão para cada empréstimo
                .collect(Collectors.toList()); // Coleta em uma nova lista de DTOs
    }

    private LoanResponseDTO convertToResponseDTO(Loan loan) {
        LoanResponseDTO dto = new LoanResponseDTO();
        dto.setId(loan.getId());
        dto.setBookId(loan.getBook().getId());
        dto.setBookTitle(loan.getBook().getTitle()); // Expondo apenas o necessário
        dto.setUserId(loan.getUser().getId());
        dto.setUserName(loan.getUser().getName()); // Expondo apenas o necessário
        dto.setLoanDate(loan.getLoanDate());
        dto.setExpectedReturnDate(loan.getExpectedReturnDate());
        dto.setStatus(loan.getStatus());
        return dto;
    }
}