package com.atvo.ssm.web;

import com.atvo.ssm.model.UserAccount;
import com.atvo.ssm.repo.UserAccountRepo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
public class AuthController {
  private final UserAccountRepo userAccountRepo;
  private final PasswordEncoder passwordEncoder;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    if (userAccountRepo.findByEmail(req.getEmail()).isPresent()) {
      return ResponseEntity.badRequest().body("Email already used");
    }
    UserAccount user = UserAccount.builder()
      .email(req.getEmail())
      .passwordHash(passwordEncoder.encode(req.getPassword()))
      .role("ROLE_USER")
      .build();
    userAccountRepo.save(user);
    return ResponseEntity.ok().build();
  }

  @Data
  public static class RegisterRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
  }
}
