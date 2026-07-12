package com.financetracker.dto;

import com.financetracker.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionRequest {
        @NotBlank(message = "Category is required")
        private String category;

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private BigDecimal amount;

        @NotNull(message = "Type is required")
        private TransactionType type;

        @NotNull(message = "Date is required")
        private LocalDate date;

        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionResponse {
        private Long id;
        private String category;
        private BigDecimal amount;
        private TransactionType type;
        private LocalDate date;
        private String description;
    }
}
