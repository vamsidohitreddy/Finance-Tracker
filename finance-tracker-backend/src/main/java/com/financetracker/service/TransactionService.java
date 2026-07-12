package com.financetracker.service;

import com.financetracker.dto.TransactionDtos.TransactionRequest;
import com.financetracker.dto.TransactionDtos.TransactionResponse;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CurrentUserProvider currentUserProvider;

    public TransactionResponse create(TransactionRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        Transaction transaction = Transaction.builder()
                .category(request.getCategory())
                .amount(request.getAmount())
                .type(request.getType())
                .date(request.getDate())
                .description(request.getDescription())
                .user(currentUser)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return toResponse(saved);
    }

    public List<TransactionResponse> getAllForCurrentUser() {
        User currentUser = currentUserProvider.getCurrentUser();
        return transactionRepository.findByUserOrderByDateDesc(currentUser)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse getById(Long id) {
        User currentUser = currentUserProvider.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return toResponse(transaction);
    }

    public TransactionResponse update(Long id, TransactionRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));

        transaction.setCategory(request.getCategory());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDate(request.getDate());
        transaction.setDescription(request.getDescription());

        Transaction updated = transactionRepository.save(transaction);
        return toResponse(updated);
    }

    public void delete(Long id) {
        User currentUser = currentUserProvider.getCurrentUser();
        Transaction transaction = transactionRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        transactionRepository.delete(transaction);
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .category(t.getCategory())
                .amount(t.getAmount())
                .type(t.getType())
                .date(t.getDate())
                .description(t.getDescription())
                .build();
    }
}
