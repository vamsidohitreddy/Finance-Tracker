package com.financetracker.service;

import com.financetracker.dto.BudgetDtos.BudgetRequest;
import com.financetracker.dto.BudgetDtos.BudgetResponse;
import com.financetracker.exception.DuplicateResourceException;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.model.Budget;
import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import com.financetracker.model.User;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserProvider currentUserProvider;

    public BudgetResponse create(BudgetRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        budgetRepository.findByUserAndCategoryAndMonthAndYear(
                currentUser, request.getCategory(), request.getMonth(), request.getYear()
        ).ifPresent(b -> {
            throw new DuplicateResourceException(
                    "A budget for " + request.getCategory() + " already exists for " + request.getMonth() + "/" + request.getYear());
        });

        Budget budget = Budget.builder()
                .category(request.getCategory())
                .monthlyLimit(request.getMonthlyLimit())
                .month(request.getMonth())
                .year(request.getYear())
                .user(currentUser)
                .build();

        Budget saved = budgetRepository.save(budget);
        return toResponse(saved, currentUser);
    }

    public List<BudgetResponse> getForMonth(Integer month, Integer year) {
        User currentUser = currentUserProvider.getCurrentUser();
        return budgetRepository.findByUserAndMonthAndYear(currentUser, month, year)
                .stream()
                .map(b -> toResponse(b, currentUser))
                .collect(Collectors.toList());
    }

    public List<BudgetResponse> getAllForCurrentUser() {
        User currentUser = currentUserProvider.getCurrentUser();
        return budgetRepository.findByUser(currentUser)
                .stream()
                .map(b -> toResponse(b, currentUser))
                .collect(Collectors.toList());
    }

    public BudgetResponse update(Long id, BudgetRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();
        Budget budget = budgetRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        budget.setCategory(request.getCategory());
        budget.setMonthlyLimit(request.getMonthlyLimit());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        Budget updated = budgetRepository.save(budget);
        return toResponse(updated, currentUser);
    }

    public void delete(Long id) {
        User currentUser = currentUserProvider.getCurrentUser();
        Budget budget = budgetRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));
        budgetRepository.delete(budget);
    }

    private BudgetResponse toResponse(Budget budget, User user) {
        YearMonth ym = YearMonth.of(budget.getYear(), budget.getMonth());
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Transaction> categoryExpenses = transactionRepository
                .findByUserAndCategoryAndDateBetween(user, budget.getCategory(), start, end)
                .stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.toList());

        BigDecimal spent = categoryExpenses.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remaining = budget.getMonthlyLimit().subtract(spent);
        boolean overBudget = spent.compareTo(budget.getMonthlyLimit()) > 0;

        double percentUsed = budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0
                ? spent.divide(budget.getMonthlyLimit(), 4, RoundingMode.HALF_UP).doubleValue() * 100
                : 0.0;

        return BudgetResponse.builder()
                .id(budget.getId())
                .category(budget.getCategory())
                .monthlyLimit(budget.getMonthlyLimit())
                .month(budget.getMonth())
                .year(budget.getYear())
                .spent(spent)
                .remaining(remaining)
                .overBudget(overBudget)
                .percentUsed(percentUsed)
                .build();
    }
}
