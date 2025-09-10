// src/main/java/com/biblioteca/repository/BookRepository.java
package com.biblioteca.repository;

import com.biblioteca.entity.Loan;
import com.biblioteca.entity.LoanStatus;
import com.biblioteca.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserAndStatus(User user, LoanStatus status);
    List<Loan> findByExpectedReturnDateBeforeAndStatus(LocalDate today, LoanStatus status);
}