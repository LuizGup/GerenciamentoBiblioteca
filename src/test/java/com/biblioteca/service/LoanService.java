package com.biblioteca.service;

import com.biblioteca.dto.LoanRequestDTO;
import com.biblioteca.dto.LoanResponseDTO;
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
        activeUser.setName("Carlos Santana");
        activeUser.setStatus(UserStatus.ATIVO);

        availableBook = new Book();
        availableBook.setId(1L);
        availableBook.setTitle("O Senhor dos Anéis");
        availableBook.setStatus(BookStatus.DISPONIVEL);
        availableBook.setAvailableQuantity(5);

        loanRequestDTO = new LoanRequestDTO();
        loanRequestDTO.setUserId(1L);
        loanRequestDTO.setBookId(1L);

        loan = new Loan();
        loan.setId(1L);
        loan.setStatus(LoanStatus.ATIVO);
        loan.setBook(availableBook);
        loan.setUser(activeUser);
    }

    @Test
    @DisplayName("Deve criar um empréstimo e retornar um DTO com sucesso")
    void createLoan_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(availableBook));
        when(loanRepository.countByUserAndStatus(activeUser, LoanStatus.ATIVO)).thenReturn(0);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        LoanResponseDTO createdLoanDTO = loanService.createLoan(loanRequestDTO);

        assertNotNull(createdLoanDTO);
        assertEquals(LoanStatus.ATIVO, createdLoanDTO.getStatus());

        assertEquals(activeUser.getName(), createdLoanDTO.getUserName());
        assertEquals(availableBook.getTitle(), createdLoanDTO.getBookTitle());
        assertEquals(4, availableBook.getAvailableQuantity());

        verify(bookRepository, times(1)).save(availableBook);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    @DisplayName("Deve buscar e retornar todos os empréstimos")
    void findAllLoans_Success() {
        when(loanRepository.findAll()).thenReturn(List.of(loan));

        List<LoanResponseDTO> result = loanService.findAllLoans();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(loanRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve devolver um empréstimo com sucesso e retornar um DTO")
    void returnLoan_Success() {
        int initialQuantity = availableBook.getAvailableQuantity();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        LoanResponseDTO returnedLoanDTO = loanService.returnLoan(1L);

        assertNotNull(returnedLoanDTO);
        assertEquals(LoanStatus.DEVOLVIDO, returnedLoanDTO.getStatus());
        assertEquals(initialQuantity + 1, availableBook.getAvailableQuantity());
        verify(loanRepository, times(1)).save(loan);
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

    @Test
    @DisplayName("Deve lançar exceção ao tentar devolver um empréstimo que não existe")
    void returnLoan_WhenLoanNotFound_ShouldThrowException() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            loanService.returnLoan(99L);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar devolver um empréstimo já devolvido")
    void returnLoan_WhenLoanAlreadyReturned_ShouldThrowException() {
        loan.setStatus(LoanStatus.DEVOLVIDO);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class, () -> {
            loanService.returnLoan(1L);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar empréstimos de um usuário que não existe")
    void findLoansByUserId_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            loanService.findLoansByUserId(99L);
        });
    }
}
