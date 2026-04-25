package com.atvo.ssm.repo;

import com.atvo.ssm.model.DomainMapping;
import com.atvo.ssm.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DomainMappingRepo extends JpaRepository<DomainMapping, Long> {
  List<DomainMapping> findBySite(Site site);
  Optional<DomainMapping> findByDomain(String domain);
}
