package com.atvo.ssm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "domain_mappings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainMapping {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Site site;

  @Column(nullable = false, unique = true)
  private String domain;

  @Column(nullable = false)
  private String status; // PENDING_VERIFICATION, VERIFIED, SSL_ISSUED

  @Column(nullable = false)
  private Instant createdAt;
}
