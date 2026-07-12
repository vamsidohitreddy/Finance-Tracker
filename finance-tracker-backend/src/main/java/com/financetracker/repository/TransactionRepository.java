package com.financetracker.repository;

import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import com.financetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Every query is scoped to the owning user so one user never sees another's data
    List<Transaction> findByUserOrderByDateDesc(User user);

    Optional<Transaction> findByIdAndUser(Long id, User user);

    List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

    List<Transaction> findByUserAndCategoryAndDateBetween(User user, String category, LocalDate start, LocalDate end);

    List<Transaction> findByUserAndTypeAndDateBetween(User user, TransactionType type, LocalDate start, LocalDate end);
}
