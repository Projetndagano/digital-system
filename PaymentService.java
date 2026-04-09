package com.group.payment.payment;

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
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public ApiResponse payBill(User user, String billNumber, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setType(Payment.PaymentType.BILL);
        payment.setAmount(amount);
        payment.setRecipient(billNumber);
        paymentRepository.save(payment);

        return ApiResponse.ok("Bill paid successfully", Map.of(
                "paymentId", payment.getId(),
                "billNumber", billNumber,
                "amount", amount,
                "newBalance", wallet.getBalance(),
                "status", "SUCCESS"
        ));
    }

    @Transactional
    public ApiResponse buyAirtime(User user, String phoneNumber, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setType(Payment.PaymentType.AIRTIME);
        payment.setAmount(amount);
        payment.setRecipient(phoneNumber);
        paymentRepository.save(payment);

        return ApiResponse.ok("Airtime purchased successfully", Map.of(
                "paymentId", payment.getId(),
                "phoneNumber", phoneNumber,
                "amount", amount,
                "newBalance", wallet.getBalance(),
                "status", "SUCCESS"
        ));
    }

    public ApiResponse getPaymentHistory(User user) {
        var payments = paymentRepository.findByUserOrderByCreatedAtDesc(user);
        var list = payments.stream().map(p -> Map.of(
                "id", p.getId(),
                "type", p.getType(),
                "amount", p.getAmount(),
                "recipient", p.getRecipient(),
                "status", p.getStatus(),
                "createdAt", p.getCreatedAt()
        )).toList();
        return ApiResponse.ok("Payment history retrieved", list);
    }

    public ApiResponse getReceipt(User user, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return ApiResponse.ok("Receipt retrieved", Map.of(
                "receiptNumber", "RCP-" + payment.getId(),
                "type", payment.getType(),
                "amount", payment.getAmount(),
                "recipient", payment.getRecipient(),
                "status", payment.getStatus(),
                "date", payment.getCreatedAt()
        ));
    }
}