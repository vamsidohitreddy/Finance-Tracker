package com.financetracker.repository;

import com.financetracker.model.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends MongoRepository<Budget, String> {

    List<Budget> findByUserIdAndMonthAndYear(String userId, Integer month, Integer year);

    Optional<Budget> findByUserIdAndCategoryAndMonthAndYear(String userId, String category, Integer month, Integer year);

    Optional<Budget> findByIdAndUserId(String id, String userId);

    List<Budget> findByUserId(String userId);
}




//package com.financetracker.repository;

//
//import com.financetracker.model.Budget;
//import com.financetracker.model.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface BudgetRepository extends JpaRepository<Budget, Long> {
//
//    List<Budget> findByUserAndMonthAndYear(User user, Integer month, Integer year);
//
//    Optional<Budget> findByUserAndCategoryAndMonthAndYear(User user, String category, Integer month, Integer year);
//
//    Optional<Budget> findByIdAndUser(Long id, User user);
//
//    List<Budget> findByUser(User user);
//}
