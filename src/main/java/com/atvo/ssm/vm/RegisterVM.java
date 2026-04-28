package com.atvo.ssm.vm;

import com.atvo.ssm.service.RegistrationService;
import lombok.Getter;
import lombok.Setter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@Getter
@Setter
public class RegisterVM {

  @WireVariable
  private RegistrationService registrationService;

  private String email;
  private String password;
  private String confirmPassword;
  private String errorMessage;
  private boolean registered;

  @Init
  public void init() {
    registered = false;
  }

  @Command
  @NotifyChange({"errorMessage", "registered"})
  public void register() {
    errorMessage = null;

    if (email == null || email.isBlank()) {
      errorMessage = "Email is required.";
      return;
    }
    if (!email.contains("@")) {
      errorMessage = "Please enter a valid email address.";
      return;
    }
    if (password == null || password.length() < 8) {
      errorMessage = "Password must be at least 8 characters.";
      return;
    }
    if (!password.equals(confirmPassword)) {
      errorMessage = "Passwords do not match.";
      return;
    }

    try {
      registrationService.register(email, password);
      registered = true;
    } catch (IllegalArgumentException e) {
      errorMessage = e.getMessage();
    }
  }
}
