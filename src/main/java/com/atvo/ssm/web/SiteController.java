package com.atvo.ssm.web;

import com.atvo.ssm.model.Deployment;
import com.atvo.ssm.model.Site;
import com.atvo.ssm.model.UserAccount;
import com.atvo.ssm.repo.DeploymentRepo;
import com.atvo.ssm.repo.SiteRepo;
import com.atvo.ssm.service.CurrentUserService;
import com.atvo.ssm.service.ZipDeployService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class SiteController {
  private final SiteRepo siteRepo;
  private final DeploymentRepo deploymentRepo;
  private final CurrentUserService currentUserService;
  private final ZipDeployService zipDeployService;

  @GetMapping("/sites")
  public List<Site> mySites() {
    UserAccount user = currentUserService.requireCurrentUser();
    return siteRepo.findByOwner(user);
  }

  @PostMapping("/sites")
  public Site createSite(@RequestBody CreateSiteRequest req) {
    UserAccount user = currentUserService.requireCurrentUser();
    Site site = Site.builder()
      .owner(user)
      .name(req.getName())
      .slug(slugify(req.getName()))
      .status("ACTIVE")
      .createdAt(Instant.now())
      .build();
    return siteRepo.save(site);
  }

  @PostMapping("/sites/{siteId}/deployments/preview")
  public Deployment uploadZipPreview(@PathVariable long siteId, @RequestParam("file") MultipartFile file) throws IOException {
    UserAccount user = currentUserService.requireCurrentUser();
    Site site = siteRepo.findByIdAndOwner(siteId, user).orElseThrow();
    return zipDeployService.createPreviewDeployment(site, file);
  }

  @PostMapping("/sites/{siteId}/deployments/{deploymentId}/publish")
  public ResponseEntity<?> publish(@PathVariable long siteId, @PathVariable long deploymentId) throws IOException {
    UserAccount user = currentUserService.requireCurrentUser();
    Site site = siteRepo.findByIdAndOwner(siteId, user).orElseThrow();
    Deployment deployment = deploymentRepo.findById(deploymentId).orElseThrow();
    if (!deployment.getSite().getId().equals(site.getId())) {
      return ResponseEntity.badRequest().body("Deployment not in site");
    }
    zipDeployService.publish(site, deployment);
    site.setCurrentDeploymentId(deployment.getId());
    siteRepo.save(site);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/sites/{siteId}/deployments")
  public List<Deployment> listDeployments(@PathVariable long siteId) {
    UserAccount user = currentUserService.requireCurrentUser();
    Site site = siteRepo.findByIdAndOwner(siteId, user).orElseThrow();
    return deploymentRepo.findBySiteOrderByCreatedAtDesc(site);
  }

  private static String slugify(String s) {
    String out = s == null ? "" : s.trim().toLowerCase();
    out = out.replaceAll("[^a-z0-9]+", "-");
    out = out.replaceAll("(^-+|-+$)", "");
    if (out.isBlank()) {
      out = "site";
    }
    return out;
  }

  @Data
  public static class CreateSiteRequest {
    @NotBlank
    private String name;
  }
}
