// src/main/java/com/biblioteca/repository/BookRepository.java
package com.biblioteca.repository;

import com.biblioteca.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByCpf(String cpf);
}