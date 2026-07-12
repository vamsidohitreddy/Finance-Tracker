package com.financetracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "budgets", uniqueConstraints = {
        // one budget limit per category per month per user
        @UniqueConstraint(columnNames = {"user_id", "category", "budget_month", "budget_year"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyLimit;

    // NOTE: "month" and "year" are reserved words in H2's SQL grammar, so they're
    // mapped to differently-named columns here. The Java field names (month/year)
    // are unchanged, so Spring Data derived queries like findByUserAndMonthAndYear
    // keep working exactly as before — only the physical column name changes.
    @Column(name = "budget_month", nullable = false)
    private Integer month; // 1-12

    @Column(name = "budget_year", nullable = false)
    private Integer year;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
