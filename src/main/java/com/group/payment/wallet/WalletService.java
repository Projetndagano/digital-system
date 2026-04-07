package com.group.payment.wallet;

import com.group.payment.auth.User;
import com.group.payment.config.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public ApiResponse createWallet(User user) {
        if (walletRepository.existsByUser(user)) {
            throw new RuntimeException("Wallet already exists for this user");
        }
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        walletRepository.save(wallet);
        return ApiResponse.ok("Wallet created successfully", walletInfo(wallet));
    }

    public ApiResponse getBalance(User user) {
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return ApiResponse.ok("Balance retrieved", walletInfo(wallet));
    }

    public ApiResponse fundWallet(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        return ApiResponse.ok("Wallet funded successfully", walletInfo(wallet));
    }

    private Map<String, Object> walletInfo(Wallet wallet) {
        return Map.of(
            "id", wallet.getId(),
            "balance", wallet.getBalance(),
            "currency", wallet.getCurrency(),
            "createdAt", wallet.getCreatedAt()
        );
    }
}