package com.atvo.ssm.vm;

import java.util.Set;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;

public class IndexVM {

  private static final Set<String> VALID_PAGES = Set.of(
    "home", "sites", "payments", "admin"
  );

  private String contentSrc = "~./zul/pages/home.zul";

  @Init
  public void init(@QueryParam("page") String page) {
    if (page != null && VALID_PAGES.contains(page)) {
      contentSrc = "~./zul/pages/" + page + ".zul";
    }
  }

  public String getContentSrc() {
    return contentSrc;
  }
}
