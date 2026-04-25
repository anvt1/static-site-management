package com.atvo.ssm.repo;

import com.atvo.ssm.model.PaymentTransaction;
import com.atvo.ssm.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, Long> {
  List<PaymentTransaction> findByUserOrderByCreatedAtDesc(UserAccount user);
  List<PaymentTransaction> findByStatusOrderByCreatedAtAsc(String status);
}
