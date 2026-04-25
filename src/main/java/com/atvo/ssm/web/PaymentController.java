package com.atvo.ssm.web;

import com.atvo.ssm.model.PaymentTransaction;
import com.atvo.ssm.model.UserAccount;
import com.atvo.ssm.repo.PaymentTransactionRepo;
import com.atvo.ssm.service.CurrentUserService;
import com.atvo.ssm.service.StoragePaths;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {
  private final PaymentTransactionRepo paymentTransactionRepo;
  private final CurrentUserService currentUserService;
  private final StoragePaths storagePaths;

  @PostConstruct
  void init() throws IOException {
    Files.createDirectories(storagePaths.receiptsDir());
  }

  @PostMapping
  public PaymentTransaction create(@RequestBody CreatePaymentRequest req) {
    UserAccount user = currentUserService.requireCurrentUser();

    PaymentTransaction tx = PaymentTransaction.builder()
      .user(user)
      .status("PENDING")
      .createdAt(Instant.now())
      .amountVnd(req.getAmountVnd())
      .paymentReference("PENDING")
      .build();
    tx = paymentTransactionRepo.save(tx);

    tx.setPaymentReference("PAY-" + tx.getId());
    return paymentTransactionRepo.save(tx);
  }

  @GetMapping
  public List<PaymentTransaction> myTransactions() {
    UserAccount user = currentUserService.requireCurrentUser();
    return paymentTransactionRepo.findByUserOrderByCreatedAtDesc(user);
  }

  @PostMapping("/{txId}/submit")
  public ResponseEntity<?> submit(
    @PathVariable long txId,
    @RequestParam("paymentReference") String paymentReference,
    @RequestParam(value = "bankTransactionId", required = false) String bankTransactionId,
    @RequestParam("receipt") MultipartFile receipt
  ) throws IOException {
    UserAccount user = currentUserService.requireCurrentUser();
    PaymentTransaction tx = paymentTransactionRepo.findById(txId).orElseThrow();
    if (!tx.getUser().getId().equals(user.getId())) {
      return ResponseEntity.status(403).build();
    }
    if (!tx.getPaymentReference().equals(paymentReference)) {
      return ResponseEntity.badRequest().body("Invalid paymentReference");
    }
    if (receipt.isEmpty()) {
      return ResponseEntity.badRequest().body("Missing receipt");
    }

    Path out = storagePaths.receiptsDir().resolve("tx-" + tx.getId() + "-" + sanitize(receipt.getOriginalFilename()));
    try (InputStream is = receipt.getInputStream()) {
      Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
    }

    tx.setBankTransactionId(bankTransactionId);
    tx.setReceiptPath(out.toString());
    paymentTransactionRepo.save(tx);

    return ResponseEntity.ok().build();
  }

  private static String sanitize(String name) {
    if (name == null) {
      return "receipt";
    }
    return name.replaceAll("[^a-zA-Z0-9._-]", "_");
  }

  @Data
  public static class CreatePaymentRequest {
    @Min(1000)
    private long amountVnd;
  }
}
