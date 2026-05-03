package com.atvo.ssm.service;

import com.atvo.ssm.model.Permission;
import com.atvo.ssm.model.Role;
import com.atvo.ssm.repo.PermissionRepo;
import com.atvo.ssm.repo.RoleRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RbacDataSeeder {

  private final RoleRepo roleRepo;
  private final PermissionRepo permissionRepo;

  @PostConstruct
  @Transactional
  public void seed() {
    Map<String, Permission> permissions = seedPermissions();
    seedRoles(permissions);
    log.info("RBAC data seeded successfully");
  }

  private Map<String, Permission> seedPermissions() {
    Map<String, Permission> map = new HashMap<>();

    String[][] defs = {
      // Site lifecycle
      {"SITE_CREATE", "Create Site", "Create new sites"},
      {"SITE_READ_OWN", "Read Own Sites", "View own sites"},
      {"SITE_UPDATE_OWN", "Update Own Sites", "Update own sites"},
      {"SITE_DELETE_OWN", "Delete Own Sites", "Delete own sites"},

      // Publishing
      {"SITE_PUBLISH_OWN", "Publish Own Sites", "Publish/preview deployments for own sites"},
      {"SITE_ROLLBACK_OWN", "Rollback Own Sites", "Rollback deployments for own sites"},

      // Domains/SSL (optional for MVP)
      {"DOMAIN_MANAGE_OWN", "Manage Own Domains", "Manage custom domains for own sites"},

      // Templates/categories
      {"TEMPLATE_READ", "Read Templates", "View available templates"},
      {"TEMPLATE_MANAGE", "Manage Templates", "Create/update/delete templates"},
      {"CATEGORY_READ", "Read Categories", "View categories"},
      {"CATEGORY_MANAGE", "Manage Categories", "Create/update/delete categories"},

      // User admin
      {"USER_READ_ALL", "Read All Users", "View all users in system"},
      {"USER_MANAGE_ALL", "Manage All Users", "Create/disable/reset users"},

      // System
      {"AUDITLOG_READ_ALL", "Read All Audit Logs", "View system audit logs"},

      // Payment (existing functionality)
      {"PAYMENT_READ_ALL", "Read All Payments", "View all payment transactions"},
      {"PAYMENT_APPROVE", "Approve Payments", "Approve payment transactions"},
      {"PAYMENT_REJECT", "Reject Payments", "Reject payment transactions"},
    };

    for (String[] def : defs) {
      String code = def[0];
      Permission p = permissionRepo.findByCode(code)
        .orElseGet(() -> {
          Permission newP = Permission.builder()
            .code(code)
            .name(def[1])
            .description(def[2])
            .build();
          return permissionRepo.save(newP);
        });
      map.put(code, p);
    }

    return map;
  }

  private void seedRoles(Map<String, Permission> permissions) {
    // ADMIN role - all permissions
    Role admin = roleRepo.findByCode("ADMIN")
      .orElseGet(() -> roleRepo.save(Role.builder()
        .code("ADMIN")
        .name("Administrator")
        .build()));
    admin.getPermissions().clear();
    admin.getPermissions().addAll(permissions.values());
    roleRepo.save(admin);

    // CLIENT_OWNER role - limited permissions
    Role clientOwner = roleRepo.findByCode("CLIENT_OWNER")
      .orElseGet(() -> roleRepo.save(Role.builder()
        .code("CLIENT_OWNER")
        .name("Client Owner")
        .build()));
    clientOwner.getPermissions().clear();
    clientOwner.getPermissions().add(permissions.get("SITE_CREATE"));
    clientOwner.getPermissions().add(permissions.get("SITE_READ_OWN"));
    clientOwner.getPermissions().add(permissions.get("SITE_UPDATE_OWN"));
    clientOwner.getPermissions().add(permissions.get("SITE_DELETE_OWN"));
    clientOwner.getPermissions().add(permissions.get("SITE_PUBLISH_OWN"));
    clientOwner.getPermissions().add(permissions.get("TEMPLATE_READ"));
    clientOwner.getPermissions().add(permissions.get("CATEGORY_READ"));
    roleRepo.save(clientOwner);

    // SUPPORT role (optional, admin-lite)
    Role support = roleRepo.findByCode("SUPPORT")
      .orElseGet(() -> roleRepo.save(Role.builder()
        .code("SUPPORT")
        .name("Support")
        .build()));
    support.getPermissions().clear();
    support.getPermissions().add(permissions.get("USER_READ_ALL"));
    support.getPermissions().add(permissions.get("SITE_READ_OWN"));
    support.getPermissions().add(permissions.get("AUDITLOG_READ_ALL"));
    roleRepo.save(support);
  }
}
