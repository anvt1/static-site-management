package com.atvo.ssm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "deployments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deployment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Site site;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private String status; // PREVIEW, PUBLISHED, ROLLED_BACK

  @Column(nullable = false)
  private String diskPath; // absolute or relative path
}
