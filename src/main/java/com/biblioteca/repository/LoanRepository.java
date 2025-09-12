package com.biblioteca.repository;

import com.biblioteca.entity.Loan;
import com.biblioteca.entity.LoanStatus;
import com.biblioteca.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    /**
     * Novo método para contar empréstimos ativos de um usuário.
     * Essencial para a regra de negócio de limite de empréstimos.
     */
    int countByUserAndStatus(Users user, LoanStatus status);

    /**
     * Novo método para buscar todos os empréstimos de um usuário específico.
     */
    List<Loan> findByUserId(Long userId);
}