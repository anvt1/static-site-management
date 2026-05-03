package com.atvo.ssm.service;

import com.atvo.ssm.model.Role;
import com.atvo.ssm.model.UserAccount;
import com.atvo.ssm.repo.RoleRepo;
import com.atvo.ssm.repo.UserAccountRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@org.springframework.context.annotation.DependsOn("rbacDataSeeder")
public class BootstrapAdmin {
  private final UserAccountRepo userAccountRepo;
  private final RoleRepo roleRepo;
  private final PasswordEncoder passwordEncoder;

  @Value("${ssm.bootstrap.adminEmail:admin@example.com}")
  private String adminEmail;

  @Value("${ssm.bootstrap.adminPassword:admin}")
  private String adminPassword;

  @PostConstruct
  void ensureAdmin() {
    userAccountRepo.findByEmail(adminEmail).orElseGet(() -> {
      Role adminRole = roleRepo.findByCode("ADMIN")
        .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

      UserAccount admin = UserAccount.builder()
        .email(adminEmail)
        .passwordHash(passwordEncoder.encode(adminPassword))
        .emailVerified(true)
        .status("ACTIVE")
        .roles(Set.of(adminRole))
        .build();
      return userAccountRepo.save(admin);
    });
  }
}
