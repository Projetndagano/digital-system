package com.group.payment.transaction;

import com.group.payment.auth.User;
import com.group.payment.config.ApiResponse;
import com.group.payment.wallet.Wallet;
import com.group.payment.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public ApiResponse sendMoney(User sender, String receiverEmail, BigDecimal amount, String description) {

        // Check amount is positive
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        // Get sender wallet
        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        // Check sufficient balance
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Get receiver wallet
        Wallet receiverWallet = walletRepository.findByUserEmail(receiverEmail)
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        // Deduct from sender
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        walletRepository.save(senderWallet);

        // Add to receiver
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
        walletRepository.save(receiverWallet);

        // Record transaction
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiverWallet.getUser());
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transactionRepository.save(transaction);

        return ApiResponse.ok("Money sent successfully", Map.of(
                "transactionId", transaction.getId(),
                "amount", amount,
                "newBalance", senderWallet.getBalance()
        ));
    }

    public ApiResponse getHistory(User user) {
        var transactions = transactionRepository
                .findBySenderOrReceiverOrderByCreatedAtDesc(user, user);

        var list = transactions.stream().map(t -> Map.of(
                "id", t.getId(),
                "amount", t.getAmount(),
                "description", t.getDescription() != null ? t.getDescription() : "",
                "status", t.getStatus(),
                "createdAt", t.getCreatedAt(),
                "type", t.getSender().getId().equals(user.getId()) ? "SENT" : "RECEIVED"
        )).toList();

        return ApiResponse.ok("Transaction history retrieved", list);
    }
}