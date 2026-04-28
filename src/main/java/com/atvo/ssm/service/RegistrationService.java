package com.atvo.ssm.service;

import com.atvo.ssm.model.UserAccount;
import com.atvo.ssm.repo.UserAccountRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

  private final UserAccountRepo userAccountRepo;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void register(String email, String password) {
    if (userAccountRepo.findByEmail(email).isPresent()) {
      throw new IllegalArgumentException("Email already in use");
    }

    String token = UUID.randomUUID().toString();

    UserAccount user = UserAccount.builder()
      .email(email)
      .passwordHash(passwordEncoder.encode(password))
      .role("ROLE_USER")
      .emailVerified(false)
      .verificationToken(token)
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
