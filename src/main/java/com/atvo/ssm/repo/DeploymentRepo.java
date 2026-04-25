package com.atvo.ssm.repo;

import com.atvo.ssm.model.Deployment;
import com.atvo.ssm.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeploymentRepo extends JpaRepository<Deployment, Long> {
  List<Deployment> findBySiteOrderByCreatedAtDesc(Site site);
}
