// no pacote com.biblioteca.dto
package com.biblioteca.dto;

import com.biblioteca.entity.LoanStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LoanResponseDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String userName;
    private LocalDate loanDate;
    private LocalDate expectedReturnDate;
    private LoanStatus status;
}