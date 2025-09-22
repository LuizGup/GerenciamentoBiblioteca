// src/main/java/com/biblioteca/entity/Book.java
package com.biblioteca.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O campo title não pode ser nulo")
    @Size(min = 2, max = 100, message = "O título deve ter entre 2 e 100 caracteres.")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "O campo author não pode ser nulo")
    @Column(nullable = false)
    private String author;

    @NotNull(message = "O campo isbn não pode ser nulo")
    @Column(nullable = false, unique = true)
    private String isbn;

    private Integer publicationYear;

    @NotNull(message = "O campo totalQuantity não pode ser nulo")
    @Column(nullable = false)
    private Integer totalQuantity;

    @NotNull(message = "O campo availableQuantity não pode ser nulo")
    @Column(nullable = false)
    private Integer availableQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Loan> loans;
}