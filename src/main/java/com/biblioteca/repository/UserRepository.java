// src/main/java/com/biblioteca/repository/BookRepository.java
package com.biblioteca.repository;

import com.biblioteca.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}