package com.atvo.ssm.repo;

import com.atvo.ssm.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepo extends JpaRepository<Permission, Long> {
  Optional<Permission> findByCode(String code);
  boolean existsByCode(String code);
}
