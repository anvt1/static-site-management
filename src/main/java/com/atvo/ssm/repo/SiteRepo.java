package com.atvo.ssm.repo;

import com.atvo.ssm.model.Site;
import com.atvo.ssm.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiteRepo extends JpaRepository<Site, Long> {
  List<Site> findByOwner(UserAccount owner);
  Optional<Site> findByIdAndOwner(Long id, UserAccount owner);
  Optional<Site> findBySlug(String slug);
}
