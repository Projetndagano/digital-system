package com.group.payment.admin;

import com.group.payment.auth.User;
import com.group.payment.auth.UserRepository;
import com.group.payment.config.ApiResponse;
import com.group.payment.transaction.TransactionRepository;
import com.group.payment.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        var users = userRepository.findAll().stream().map(u -> Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail(),
                "role", u.getRole(),
                "active", u.isActive(),
                "createdAt", u.getCreatedAt()
        )).toList();
        return ResponseEntity.ok(ApiResponse.ok("Users retrieved", users));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(ApiResponse.ok("User found", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "active", user.isActive()
        )));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<?> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = body.get("role");
        if (!role.equals("USER") && !role.equals("ADMIN")) {
            throw new RuntimeException("Invalid role");
        }

        user.setRole(User.Role.valueOf(role));
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.ok("Role updated", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
        )));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        return ResponseEntity.ok(ApiResponse.ok("User deleted", null));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        long totalUsers = userRepository.count();
        long totalWallets = walletRepository.count();
        long totalTransactions = transactionRepository.count();

        return ResponseEntity.ok(ApiResponse.ok("Dashboard retrieved", Map.of(
                "totalUsers", totalUsers,
                "totalWallets", totalWallets,
                "totalTransactions", totalTransactions
        )));
    }
}