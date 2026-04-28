package com.atvo.ssm.web;

import com.atvo.ssm.service.RegistrationService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@Validated
public class AuthController {
  private final RegistrationService registrationService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
    try {
      registrationService.register(req.getEmail(), req.getPassword());
      return ResponseEntity.ok().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/verify")
  public RedirectView verify(@RequestParam String token) {
    try {
      registrationService.verifyEmail(token);
      return new RedirectView("/login?verified");
    } catch (IllegalArgumentException e) {
      return new RedirectView("/login?error");
    }
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
