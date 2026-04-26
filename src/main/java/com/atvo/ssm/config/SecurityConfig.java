package com.atvo.ssm.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.DispatcherTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private static final String ZUL_FILES = "/zkau/web/**/*.zul";
  private static final String ZHTML_FILES = "/~./zul/**/*.zhtml";
  private static final String ZK_RESOURCES = "/zkres/**";
  private static final String REMOVE_DESKTOP_REGEX = "/zkau\\?dtid=.*&cmd_0=rmDesktop&.*";

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> {
        // allow default error dispatcher
        auth.requestMatchers(new AndRequestMatcher(
          new DispatcherTypeRequestMatcher(DispatcherType.ERROR),
          AntPathRequestMatcher.antMatcher("/error")
        )).permitAll();
        // allow forwarded access to zhtml files (served via Spring MVC forward)
        auth.requestMatchers(new AndRequestMatcher(
          new DispatcherTypeRequestMatcher(DispatcherType.FORWARD),
          AntPathRequestMatcher.antMatcher(ZHTML_FILES)
        )).permitAll();
        // block direct access to zul/zhtml under classpath web resource folder
        auth.requestMatchers(AntPathRequestMatcher.antMatcher(ZUL_FILES)).denyAll();
        auth.requestMatchers(AntPathRequestMatcher.antMatcher(ZHTML_FILES)).denyAll();
        // allow ZK resources
        auth.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, ZK_RESOURCES)).permitAll();
        // allow desktop cleanup
        auth.requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, REMOVE_DESKTOP_REGEX)).permitAll();
        auth.requestMatchers(req -> "rmDesktop".equals(req.getParameter("cmd_0"))).permitAll();
        // allow ZK AU requests (required for ZK pages including login)
        auth.requestMatchers(AntPathRequestMatcher.antMatcher("/zkau/**")).permitAll();
        // public pages
        auth.requestMatchers(
          AntPathRequestMatcher.antMatcher("/login"),
          AntPathRequestMatcher.antMatcher("/logout"),
          AntPathRequestMatcher.antMatcher("/register"),
          AntPathRequestMatcher.antMatcher("/api/public/**")
        ).permitAll();
        // static resources
        auth.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/favicon.ico")).permitAll();
        auth.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/css/**")).permitAll();
        auth.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/js/**")).permitAll();
        auth.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/img/**")).permitAll();
        auth.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/dist/**")).permitAll();
        // protected
        auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/admin/**")).hasRole("ADMIN");
        auth.anyRequest().authenticated();
      })
      .formLogin(form -> form
        .loginPage("/login")
        .defaultSuccessUrl("/", true)
        .failureUrl("/login?error")
      )
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login")
      );

    return http.build();
  }
}
