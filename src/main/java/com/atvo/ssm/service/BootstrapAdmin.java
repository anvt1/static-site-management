package com.atvo.ssm.service;

import com.atvo.ssm.model.UserAccount;
import com.atvo.ssm.repo.UserAccountRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapAdmin {
  private final UserAccountRepo userAccountRepo;
  private final PasswordEncoder passwordEncoder;

  @Value("${ssm.bootstrap.adminEmail:admin@example.com}")
  private String adminEmail;

  @Value("${ssm.bootstrap.adminPassword:admin}")
  private String adminPassword;

  @PostConstruct
  void ensureAdmin() {
    userAccountRepo.findByEmail(adminEmail).orElseGet(() -> {
      UserAccount admin = UserAccount.builder()
        .email(adminEmail)
        .passwordHash(passwordEncoder.encode(adminPassword))
        .role("ROLE_ADMIN")
        .emailVerified(true)
        .build();
      return userAccountRepo.save(admin);
    });
  }
}
