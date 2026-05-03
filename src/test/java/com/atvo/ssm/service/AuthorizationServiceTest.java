package com.atvo.ssm.service;

import com.atvo.ssm.model.Permission;
import com.atvo.ssm.model.Role;
import com.atvo.ssm.model.Site;
import com.atvo.ssm.model.UserAccount;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationServiceTest {

  private final AuthorizationService authService = new AuthorizationService();

  @Test
  void adminHasAllPermissions() {
    UserAccount admin = createUserWithRole("ADMIN");
    assertTrue(authService.hasPermission(admin, "SITE_CREATE"));
    assertTrue(authService.hasPermission(admin, "SITE_DELETE_OWN"));
    assertTrue(authService.hasPermission(admin, "USER_MANAGE_ALL"));
  }

  @Test
  void clientOwnerHasLimitedPermissions() {
    UserAccount client = createUserWithRole("CLIENT_OWNER",
      "SITE_CREATE", "SITE_READ_OWN", "SITE_PUBLISH_OWN");

    assertTrue(authService.hasPermission(client, "SITE_CREATE"));
    assertTrue(authService.hasPermission(client, "SITE_READ_OWN"));
    assertFalse(authService.hasPermission(client, "USER_READ_ALL"));
    assertFalse(authService.hasPermission(client, "SITE_DELETE_OWN")); // not assigned
  }

  @Test
  void requirePermissionThrowsWhenMissing() {
    UserAccount user = createUserWithRole("CLIENT_OWNER", "SITE_READ_OWN");

    assertThrows(AccessDeniedException.class, () ->
      authService.requirePermission(user, "USER_READ_ALL"));
  }

  @Test
  void requireSiteOwnerAllowsAdmin() {
    UserAccount admin = createUserWithRole("ADMIN");
    Site site = new Site();
    site.setOwner(createUserWithRole("CLIENT_OWNER"));

    assertDoesNotThrow(() -> authService.requireSiteOwnerOrAdmin(admin, site));
  }

  @Test
  void requireSiteOwnerAllowsOwner() {
    UserAccount owner = createUserWithRole("CLIENT_OWNER");
    owner.setId(1L);
    Site site = new Site();
    site.setOwner(owner);

    assertDoesNotThrow(() -> authService.requireSiteOwnerOrAdmin(owner, site));
  }

  @Test
  void requireSiteOwnerDeniesNonOwner() {
    UserAccount user = createUserWithRole("CLIENT_OWNER");
    user.setId(1L);

    UserAccount other = new UserAccount();
    other.setId(2L);
    Site site = new Site();
    site.setOwner(other);

    assertThrows(AccessDeniedException.class, () ->
      authService.requireSiteOwnerOrAdmin(user, site));
  }

  private UserAccount createUserWithRole(String roleCode, String... permissions) {
    Role role = Role.builder()
      .code(roleCode)
      .build();

    for (String permCode : permissions) {
      role.getPermissions().add(Permission.builder().code(permCode).build());
    }

    return UserAccount.builder()
      .id(1L)
      .email("test@example.com")
      .roles(Set.of(role))
      .build();
  }
}
