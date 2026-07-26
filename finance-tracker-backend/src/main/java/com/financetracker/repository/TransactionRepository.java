package com.financetracker.repository;

import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

    // Every query is scoped by userId so one user never sees another's data
    List<Transaction> findByUserIdOrderByDateDesc(String userId);

    Optional<Transaction> findByIdAndUserId(String id, String userId);

    List<Transaction> findByUserIdAndDateBetween(String userId, LocalDate start, LocalDate end);

    List<Transaction> findByUserIdAndCategoryAndDateBetween(String userId, String category, LocalDate start, LocalDate end);

    List<Transaction> findByUserIdAndTypeAndDateBetween(String userId, TransactionType type, LocalDate start, LocalDate end);
}


//package com.financetracker.repository;




//
//import com.financetracker.model.Transaction;
//import com.financetracker.model.TransactionType;
//import com.financetracker.model.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//public interface TransactionRepository extends JpaRepository<Transaction, Long> {
//
//    // Every query is scoped to the owning user so one user never sees another's data
//    List<Transaction> findByUserOrderByDateDesc(User user);
//
//    Optional<Transaction> findByIdAndUser(Long id, User user);
//
//    List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
//
//    List<Transaction> findByUserAndCategoryAndDateBetween(User user, String category, LocalDate start, LocalDate end);
//
//    List<Transaction> findByUserAndTypeAndDateBetween(User user, TransactionType type, LocalDate start, LocalDate end);
//}
