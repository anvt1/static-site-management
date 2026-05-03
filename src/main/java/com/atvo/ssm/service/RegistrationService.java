package com.atvo.ssm.service;

import com.atvo.ssm.model.Role;
import com.atvo.ssm.model.UserAccount;
import com.atvo.ssm.repo.RoleRepo;
import com.atvo.ssm.repo.UserAccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

  private final UserAccountRepo userAccountRepo;
  private final RoleRepo roleRepo;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void register(String email, String password) {
    if (userAccountRepo.findByEmail(email).isPresent()) {
      throw new IllegalArgumentException("Email already in use");
    }

    Role clientOwnerRole = roleRepo.findByCode("CLIENT_OWNER")
      .orElseThrow(() -> new IllegalStateException("CLIENT_OWNER role not found"));

    String token = UUID.randomUUID().toString();

    UserAccount user = UserAccount.builder()
      .email(email)
      .passwordHash(passwordEncoder.encode(password))
      .status("ACTIVE")
      .emailVerified(false)
      .verificationToken(token)
      .roles(Set.of(clientOwnerRole))
      .build();

    userAccountRepo.save(user);

    log.info("Email verification link for {}: /verify?token={}", email, token);
  }

  @Transactional
  public void verifyEmail(String token) {
    UserAccount user = userAccountRepo.findByVerificationToken(token)
      .orElseThrow(() -> new IllegalArgumentException("Invalid or expired verification token"));

    user.setEmailVerified(true);
    user.setVerificationToken(null);
    userAccountRepo.save(user);
  }
}
