package com.atvo.ssm.service;

import com.atvo.ssm.model.Deployment;
import com.atvo.ssm.model.Site;
import com.atvo.ssm.repo.DeploymentRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.Instant;
import java.util.Enumeration;

@Service
@RequiredArgsConstructor
public class ZipDeployService {
  private final StoragePaths storagePaths;
  private final DeploymentRepo deploymentRepo;

  @Value("${ssm.upload.maxZipBytes:52428800}")
  private long maxZipBytes;

  @Value("${ssm.upload.maxFileCount:5000}")
  private int maxFileCount;

  @Value("${ssm.upload.maxExtractedBytes:209715200}")
  private long maxExtractedBytes;

  @PostConstruct
  void ensureBaseDir() throws IOException {
    Files.createDirectories(storagePaths.baseDir());
  }

  public Deployment createPreviewDeployment(Site site, MultipartFile zipFile) throws IOException {
    if (zipFile.isEmpty()) {
      throw new IllegalArgumentException("Empty zip");
    }
    if (zipFile.getSize() > maxZipBytes) {
      throw new IllegalArgumentException("Zip too large");
    }

    Deployment deployment = Deployment.builder()
      .site(site)
      .createdAt(Instant.now())
      .status("PREVIEW")
      .diskPath("")
      .build();

    deployment = deploymentRepo.save(deployment);

    Path deployDir = storagePaths.deploymentDir(site.getId(), deployment.getId());
    Files.createDirectories(deployDir);

    Path tmpZip = deployDir.resolve("upload.zip");
    try (InputStream is = zipFile.getInputStream()) {
      Files.copy(is, tmpZip, StandardCopyOption.REPLACE_EXISTING);
    }

    unzipSafe(tmpZip, deployDir.resolve("files"));
    requireIndexHtml(deployDir.resolve("files"));

    deployment.setDiskPath(deployDir.toString());
    return deploymentRepo.save(deployment);
  }

  public void publish(Site site, Deployment deployment) throws IOException {
    Path filesDir = Path.of(deployment.getDiskPath()).resolve("files");
    Path current = storagePaths.currentSymlink(site.getId());

    Files.createDirectories(storagePaths.siteDir(site.getId()));

    try {
      Files.deleteIfExists(current);
      Files.createSymbolicLink(current, filesDir);
    } catch (UnsupportedOperationException | IOException e) {
      // Windows may block symlinks without admin. Fallback: copy.
      if (Files.exists(current)) {
        deleteRecursive(current);
      }
      copyRecursive(filesDir, current);
    }

    deployment.setStatus("PUBLISHED");
    deploymentRepo.save(deployment);
  }

  private void requireIndexHtml(Path root) {
    if (!Files.exists(root.resolve("index.html"))) {
      throw new IllegalArgumentException("Missing index.html");
    }
  }

  private void unzipSafe(Path zipPath, Path destDir) throws IOException {
    Files.createDirectories(destDir);

    int count = 0;
    long extracted = 0;

    try (ZipFile zf = ZipFile.builder().setPath(zipPath).get()) {
      Enumeration<ZipArchiveEntry> entries = zf.getEntries();
      while (entries.hasMoreElements()) {
        ZipArchiveEntry entry = entries.nextElement();
        count++;
        if (count > maxFileCount) {
          throw new IllegalArgumentException("Too many files");
        }

        String name = entry.getName();
        if (name == null || name.isBlank()) {
          continue;
        }
        if (name.contains("..") || name.startsWith("/") || name.startsWith("\\")) {
          throw new IllegalArgumentException("Unsafe zip entry: " + name);
        }

        Path outPath = destDir.resolve(name).normalize();
        if (!outPath.startsWith(destDir)) {
          throw new IllegalArgumentException("Zip entry escapes target dir: " + name);
        }

        if (entry.isDirectory()) {
          Files.createDirectories(outPath);
          continue;
        }

        Files.createDirectories(outPath.getParent());
        try (InputStream is = zf.getInputStream(entry)) {
          long written = Files.copy(is, outPath, StandardCopyOption.REPLACE_EXISTING);
          extracted += written;
          if (extracted > maxExtractedBytes) {
            throw new IllegalArgumentException("Extracted content too large");
          }
        }
      }
    }
  }

  private void deleteRecursive(Path path) throws IOException {
    if (!Files.exists(path)) {
      return;
    }
    Files.walk(path)
      .sorted((a, b) -> b.getNameCount() - a.getNameCount())
      .forEach(p -> {
        try {
          Files.deleteIfExists(p);
        } catch (IOException ignored) {
        }
      });
  }

  private void copyRecursive(Path src, Path dest) throws IOException {
    Files.walk(src).forEach(p -> {
      try {
        Path rel = src.relativize(p);
        Path target = dest.resolve(rel);
        if (Files.isDirectory(p)) {
          Files.createDirectories(target);
        } else {
          Files.createDirectories(target.getParent());
          Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }
}
