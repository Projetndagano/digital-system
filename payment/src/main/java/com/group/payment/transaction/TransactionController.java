package com.group.payment.transaction;

import com.group.payment.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMoney(
            @AuthenticationPrincipal User sender,
            @RequestBody Map<String, Object> body) {

        String receiverEmail = (String) body.get("receiverEmail");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String description = (String) body.getOrDefault("description", "");

        return ResponseEntity.ok(transactionService.sendMoney(sender, receiverEmail, amount, description));
    }

    @GetMapping
    public ResponseEntity<?> getHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(transactionService.getHistory(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getHistory(user));
    }
}