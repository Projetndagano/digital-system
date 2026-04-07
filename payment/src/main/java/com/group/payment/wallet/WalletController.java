package com.group.payment.wallet;

import com.group.payment.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<?> createWallet(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(201).body(walletService.createWallet(user));
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(walletService.getBalance(user));
    }

    @PostMapping("/fund")
    public ResponseEntity<?> fundWallet(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, BigDecimal> body) {
        return ResponseEntity.ok(walletService.fundWallet(user, body.get("amount")));
    }
}