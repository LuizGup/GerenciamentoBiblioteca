package com.biblioteca.service;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.entity.*;
import com.biblioteca.exception.ResourceNotFoundException;
import com.biblioteca.repository.BookRepository;
import com.biblioteca.repository.LoanRepository;
import com.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;

    private Users activeUser;
    private Book availableBook;
    private LoanRequestDTO loanRequestDTO;

    private Loan loan;


    @BeforeEach
    void setUp() {
        activeUser = new Users();
        activeUser.setId(1L);
        activeUser.setStatus(UserStatus.ATIVO);

        availableBook = new Book();
        availableBook.setId(1L);
        availableBook.setStatus(BookStatus.DISPONIVEL);
        availableBook.setAvailableQuantity(5);

        loanRequestDTO = new LoanRequestDTO();
        loanRequestDTO.setUserId(1L);
        loanRequestDTO.setBookId(1L);

        loan.setId(1L);
        loan.setStatus(LoanStatus.ATIVO);
        loan.setBook(availableBook);
        loan.setUser(activeUser);
    }

    @Test
    @DisplayName("Deve criar um empréstimo com sucesso quando todas as regras são atendidas")
    void createLoan_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(loanRepository.countByUserAndStatus(activeUser, LoanStatus.ATIVO)).thenReturn(0);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Loan createdLoan = loanService.createLoan(loanRequestDTO);

        assertNotNull(createdLoan);
        assertEquals(LoanStatus.ATIVO, createdLoan.getStatus());
        assertEquals(activeUser, createdLoan.getUser());
        assertEquals(availableBook, createdLoan.getBook());
        assertEquals(4, availableBook.getAvailableQuantity());

        verify(bookRepository, times(1)).save(availableBook);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    @DisplayName("Deve buscar e retornar todos os empréstimos")
    void findAllLoans_Success() {
        when(loanRepository.findAll()).thenReturn(List.of(loan));

        List<Loan> result = loanService.findAllLoans();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar um empréstimo com sucesso quando todas as regras são atendidas")
    void returnLoan_Success() {
        Loan activeLoan = new Loan();
        activeLoan.setId(1L);
        activeLoan.setStatus(LoanStatus.ATIVO);
        activeLoan.setBook(availableBook);
        activeLoan.setUser(activeUser);

        int initialQuantity = availableBook.getAvailableQuantity();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(activeLoan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan returnedLoan = loanService.returnLoan(1L);

        assertNotNull(returnedLoan);
        assertEquals(LoanStatus.DEVOLVIDO, returnedLoan.getStatus());
        assertNotNull(returnedLoan.getReturnDate());
        assertEquals(initialQuantity + 1, returnedLoan.getBook().getAvailableQuantity());

        verify(loanRepository, times(1)).save(activeLoan);
        verify(bookRepository, times(1)).save(availableBook);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar empréstimo com livro indisponível")
    void createLoan_WhenBookIsNotAvailable_ShouldThrowException() {
        availableBook.setStatus(BookStatus.INDISPONIVEL);
        availableBook.setAvailableQuantity(0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loanService.createLoan(loanRequestDTO);
        });

        assertEquals("Livro não está disponível para empréstimo.", exception.getMessage());
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar empréstimo quando o limite de empréstimos foi atingido")
    void createLoan_WhenLoanLimitExceeded_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(loanRepository.countByUserAndStatus(activeUser, LoanStatus.ATIVO)).thenReturn(3);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loanService.createLoan(loanRequestDTO);
        });

        assertEquals("Usuário já possui 3 empréstimos ativos. Limite excedido.", exception.getMessage());
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar empréstimo com usuário inativo")
    void createLoan_WhenUserIsInactive_ShouldThrowException() {
        activeUser.setStatus(UserStatus.INATIVO);
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            loanService.createLoan(loanRequestDTO);
        });

        assertEquals("Usuário não está ativo e não pode realizar empréstimos.", exception.getMessage());
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o usuário não existe")
    void createLoan_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loanService.createLoan(loanRequestDTO);
        });
    }
}
