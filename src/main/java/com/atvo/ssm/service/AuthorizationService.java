package com.atvo.ssm.service;

import com.atvo.ssm.model.Role;
import com.atvo.ssm.model.Site;
import com.atvo.ssm.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

  public boolean hasPermission(UserAccount user, String permissionCode) {
    if (user == null || permissionCode == null) {
      return false;
    }
    if (user.isAdmin()) {
      return true;
    }
    for (Role role : user.getRoles()) {
      if (role.getPermissions().stream()
        .anyMatch(p -> p.getCode().equals(permissionCode))) {
        return true;
      }
    }
    return false;
  }

  public void requirePermission(UserAccount user, String permissionCode) {
    if (!hasPermission(user, permissionCode)) {
      throw new AccessDeniedException("Missing permission: " + permissionCode);
    }
  }

  public void requireSiteOwnerOrAdmin(UserAccount user, Site site) {
    if (user.isAdmin()) {
      return;
    }
    if (site.getOwner() == null || !site.getOwner().getId().equals(user.getId())) {
      throw new AccessDeniedException("Not owner of this site");
    }
  }

  public boolean isSiteOwner(UserAccount user, Site site) {
    return site.getOwner() != null && site.getOwner().getId().equals(user.getId());
  }
}
