package com.atvo.ssm.service;

import com.atvo.ssm.model.UserAccount;
import com.atvo.ssm.repo.UserAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserAccountRepo userAccountRepo;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserAccount user = userAccountRepo.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

    if (!user.isEmailVerified()) {
      throw new DisabledException("Email address not verified");
    }

    return new org.springframework.security.core.userdetails.User(
      user.getEmail(),
      user.getPasswordHash(),
      List.of(new SimpleGrantedAuthority(user.getRole()))
    );
  }
}
