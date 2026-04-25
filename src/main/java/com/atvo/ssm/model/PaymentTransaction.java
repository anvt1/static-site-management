package com.atvo.ssm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private UserAccount user;

  @Column(nullable = false)
  private String status; // PENDING, APPROVED, REJECTED

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Long amountVnd;

  @Column(nullable = false)
  private String paymentReference; // PAY-<transactionId>

  @Column
  private String bankTransactionId;

  @Column
  private String receiptPath;
}
