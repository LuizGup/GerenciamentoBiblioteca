package com.biblioteca.repository;

import com.biblioteca.entity.Loan;
import com.biblioteca.entity.LoanStatus;
import com.biblioteca.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    int countByUserAndStatus(Users user, LoanStatus status);
    List<Loan> findByUserId(Long userId);
}