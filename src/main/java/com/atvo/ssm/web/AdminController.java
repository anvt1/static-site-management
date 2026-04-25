package com.atvo.ssm.web;

import com.atvo.ssm.model.PaymentTransaction;
import com.atvo.ssm.repo.PaymentTransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
  private final PaymentTransactionRepo paymentTransactionRepo;

  @GetMapping("/payments/pending")
  public List<PaymentTransaction> pendingPayments() {
    return paymentTransactionRepo.findByStatusOrderByCreatedAtAsc("PENDING");
  }

  @PostMapping("/payments/{txId}/approve")
  public ResponseEntity<?> approve(@PathVariable long txId) {
    PaymentTransaction tx = paymentTransactionRepo.findById(txId).orElseThrow();
    tx.setStatus("APPROVED");
    paymentTransactionRepo.save(tx);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/payments/{txId}/reject")
  public ResponseEntity<?> reject(@PathVariable long txId) {
    PaymentTransaction tx = paymentTransactionRepo.findById(txId).orElseThrow();
    tx.setStatus("REJECTED");
    paymentTransactionRepo.save(tx);
    return ResponseEntity.ok().build();
  }
}
