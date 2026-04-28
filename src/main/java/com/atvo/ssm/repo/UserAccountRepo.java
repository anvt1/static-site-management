package com.atvo.ssm.repo;

import com.atvo.ssm.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepo extends JpaRepository<UserAccount, Long> {
  Optional<UserAccount> findByEmail(String email);
  Optional<UserAccount> findByVerificationToken(String token);
}
