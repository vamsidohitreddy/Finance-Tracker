package com.financetracker.controller;

import com.financetracker.dto.BudgetDtos.BudgetRequest;
import com.financetracker.dto.BudgetDtos.BudgetResponse;
import com.financetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> create(@Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAll() {
        return ResponseEntity.ok(budgetService.getAllForCurrentUser());
    }

    // e.g. GET /api/budgets/month?month=7&year=2026
    @GetMapping("/month")
    public ResponseEntity<List<BudgetResponse>> getForMonth(@RequestParam Integer month,
                                                              @RequestParam Integer year) {
        return ResponseEntity.ok(budgetService.getForMonth(month, year));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
