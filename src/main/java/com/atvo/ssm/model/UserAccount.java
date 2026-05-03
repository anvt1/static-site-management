package com.atvo.ssm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @JsonIgnore
  @Column(nullable = false)
  private String passwordHash;

  @Builder.Default
  @Column(nullable = false, columnDefinition = "varchar(20) default 'ACTIVE'")
  private String status = "ACTIVE"; // ACTIVE, SUSPENDED

  @Builder.Default
  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean emailVerified = false;

  @Column(unique = true)
  private String verificationToken;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  @Builder.Default
  @JsonIgnore
  private Set<Role> roles = new HashSet<>();

  @Column(nullable = false)
  @Builder.Default
  private java.time.Instant createdAt = java.time.Instant.now();

  @Deprecated
  public String getRole() {
    return roles.isEmpty() ? null : "ROLE_" + roles.iterator().next().getCode();
  }

  public boolean hasRole(String roleCode) {
    return roles.stream().anyMatch(r -> r.getCode().equals(roleCode));
  }

  public boolean isAdmin() {
    return hasRole("ADMIN");
  }
}
