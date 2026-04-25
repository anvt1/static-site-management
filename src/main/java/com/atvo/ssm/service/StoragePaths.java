package com.atvo.ssm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class StoragePaths {
  private final Path baseDir;

  public StoragePaths(@Value("${ssm.storage.baseDir:data}") String baseDir) {
    this.baseDir = Path.of(baseDir).toAbsolutePath().normalize();
  }

  public Path baseDir() {
    return baseDir;
  }

  public Path siteDir(long siteId) {
    return baseDir.resolve("sites").resolve(String.valueOf(siteId));
  }

  public Path deploymentsDir(long siteId) {
    return siteDir(siteId).resolve("deployments");
  }

  public Path deploymentDir(long siteId, long deploymentId) {
    return deploymentsDir(siteId).resolve(String.valueOf(deploymentId));
  }

  public Path currentSymlink(long siteId) {
    return siteDir(siteId).resolve("current");
  }

  public Path receiptsDir() {
    return baseDir.resolve("receipts");
  }
}
