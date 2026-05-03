package com.atvo.ssm.repo;

import com.atvo.ssm.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
  Optional<Role> findByCode(String code);
  boolean existsByCode(String code);
}
