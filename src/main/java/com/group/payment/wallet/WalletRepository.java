package com.group.payment.wallet;
import org.springframework.data.jpa.repository.Query;
import com.group.payment.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
    boolean existsByUser(User user);
    

@Query("SELECT w FROM Wallet w WHERE w.user.email = :email")
Optional<Wallet> findByUserEmail(String email);
}