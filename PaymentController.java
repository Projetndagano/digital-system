package com.group.payment.payment;

import com.group.payment.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/bills")
    public ResponseEntity<?> payBill(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        String billNumber = (String) body.get("billNumber");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        return ResponseEntity.ok(paymentService.payBill(user, billNumber, amount));
    }

    @PostMapping("/airtime")
    public ResponseEntity<?> buyAirtime(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> body) {
        String phoneNumber = (String) body.get("phoneNumber");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        return ResponseEntity.ok(paymentService.buyAirtime(user, phoneNumber, amount));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(user));
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<?> getReceipt(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getReceipt(user, id));
    }
}