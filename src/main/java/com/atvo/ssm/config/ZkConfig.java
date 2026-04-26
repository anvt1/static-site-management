package com.atvo.ssm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
@Controller
public class ZkConfig {

  @GetMapping("/")
  public String index() {
    return "forward:~./zul/index.zhtml";
  }

  @GetMapping("/login")
  public String login() {
    return "forward:~./zul/login.zhtml";
  }
}
