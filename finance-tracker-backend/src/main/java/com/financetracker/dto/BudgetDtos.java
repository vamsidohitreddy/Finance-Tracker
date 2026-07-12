package com.financetracker.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class BudgetDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetRequest {
        @NotBlank(message = "Category is required")
        private String category;

        @NotNull(message = "Monthly limit is required")
        @Positive(message = "Monthly limit must be positive")
        private BigDecimal monthlyLimit;

        @NotNull @Min(1) @Max(12)
        private Integer month;

        @NotNull
        private Integer year;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetResponse {
        private Long id;
        private String category;
        private BigDecimal monthlyLimit;
        private Integer month;
        private Integer year;
        private BigDecimal spent;
        private BigDecimal remaining;
        private boolean overBudget;
        private double percentUsed;
    }
}
