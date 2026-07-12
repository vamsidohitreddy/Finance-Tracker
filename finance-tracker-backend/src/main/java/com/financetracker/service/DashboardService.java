package com.financetracker.service;

import com.financetracker.dto.DashboardDtos.CategorySummary;
import com.financetracker.dto.DashboardDtos.DashboardSummary;
import com.financetracker.dto.DashboardDtos.MonthlyTrend;
import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import com.financetracker.model.User;
import com.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final CurrentUserProvider currentUserProvider;

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    public DashboardSummary getSummary() {
        User currentUser = currentUserProvider.getCurrentUser();
        List<Transaction> all = transactionRepository.findByUserOrderByDateDesc(currentUser);

        BigDecimal totalIncome = sumByType(all, TransactionType.INCOME);
        BigDecimal totalExpense = sumByType(all, TransactionType.EXPENSE);
        BigDecimal balance = totalIncome.subtract(totalExpense);

        List<CategorySummary> expenseByCategory = groupByCategory(all, TransactionType.EXPENSE);
        List<CategorySummary> incomeByCategory = groupByCategory(all, TransactionType.INCOME);
        List<MonthlyTrend> monthlyTrends = buildMonthlyTrends(all);

        return DashboardSummary.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .expenseByCategory(expenseByCategory)
                .incomeByCategory(incomeByCategory)
                .monthlyTrends(monthlyTrends)
                .build();
    }

    private BigDecimal sumByType(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<CategorySummary> groupByCategory(List<Transaction> transactions, TransactionType type) {
        Map<String, BigDecimal> grouped = transactions.stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        return grouped.entrySet().stream()
                .map(e -> CategorySummary.builder().category(e.getKey()).total(e.getValue()).build())
                .sorted(Comparator.comparing(CategorySummary::getTotal).reversed())
                .collect(Collectors.toList());
    }

    private List<MonthlyTrend> buildMonthlyTrends(List<Transaction> transactions) {
        Map<String, BigDecimal> incomeByMonth = new TreeMap<>();
        Map<String, BigDecimal> expenseByMonth = new TreeMap<>();

        for (Transaction t : transactions) {
            String monthKey = t.getDate().format(MONTH_FORMAT);
            Map<String, BigDecimal> target = t.getType() == TransactionType.INCOME ? incomeByMonth : expenseByMonth;
            target.merge(monthKey, t.getAmount(), BigDecimal::add);
        }

        return mergeMonthlyTrends(incomeByMonth, expenseByMonth);
    }

    private List<MonthlyTrend> mergeMonthlyTrends(Map<String, BigDecimal> income, Map<String, BigDecimal> expense) {
        java.util.TreeSet<String> allMonths = new java.util.TreeSet<>();
        allMonths.addAll(income.keySet());
        allMonths.addAll(expense.keySet());

        return allMonths.stream()
                .map(m -> MonthlyTrend.builder()
                        .month(m)
                        .income(income.getOrDefault(m, BigDecimal.ZERO))
                        .expense(expense.getOrDefault(m, BigDecimal.ZERO))
                        .build())
                .collect(Collectors.toList());
    }
}
