package com.atvo.ssm.web;

import com.atvo.ssm.model.Role;
import com.atvo.ssm.model.UserAccount;
import com.atvo.ssm.repo.RoleRepo;
import com.atvo.ssm.repo.UserAccountRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class SiteControllerAuthorizationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserAccountRepo userRepo;

  @Autowired
  private RoleRepo roleRepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private UserAccount testUser;

  @BeforeEach
  void setup() {
    // Get or create CLIENT_OWNER role (seeded by RbacDataSeeder)
    Role clientRole = roleRepo.findByCode("CLIENT_OWNER")
      .orElseThrow(() -> new IllegalStateException("CLIENT_OWNER role not found"));

    testUser = UserAccount.builder()
      .email("testclient@example.com")
      .passwordHash(passwordEncoder.encode("password"))
      .emailVerified(true)
      .status("ACTIVE")
      .roles(Set.of(clientRole))
      .build();
    userRepo.save(testUser);
  }

  @Test
  @WithMockUser(username = "testclient@example.com", roles = "CLIENT_OWNER")
  void clientCanListOwnSites() throws Exception {
    mockMvc.perform(get("/api/sites"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "testclient@example.com", roles = "CLIENT_OWNER")
  void clientCanCreateSite() throws Exception {
    mockMvc.perform(post("/api/sites")
        .contentType("application/json")
        .content("{\"name\":\"Test Site\"}"))
      .andExpect(status().isOk());
  }

  @Test
  void unauthenticatedCannotAccessSites() throws Exception {
    mockMvc.perform(get("/api/sites"))
      .andExpect(status().is3xxRedirection()); // Redirects to login
  }
}
