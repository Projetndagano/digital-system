package com.group.payment.transaction;

import com.group.payment.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderOrReceiverOrderByCreatedAtDesc(User sender, User receiver);
}