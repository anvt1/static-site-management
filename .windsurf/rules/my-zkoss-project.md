# My ZK Spring Boot Project Rules

## Serving ZHTML pages from Spring MVC controllers

Always use `forward:~./zul/<page>.zhtml` pattern in Spring MVC controllers — never return a plain view name for ZHTML files.

```java
@GetMapping("/login")
public String login() {
  return "forward:~./zul/login.zhtml";
}

@GetMapping("/")
public String index() {
  return "forward:~./zul/index.zhtml";
}
```

**Why**: The `~./` prefix tells ZK to load the file from `classpath:/web/`, giving it a proper `file://` URL. Without it, ZK's `ExtendletLoader` gets a null URL → null `baseUri` → zsoup throws `BaseURI must not be null`.

Using a plain view name (e.g. `return "login"`) goes through the ZK view resolver which constructs `/zul/login.zhtml` — a path ZK cannot resolve to a valid URL in Spring Boot embedded server.

---

## Resource folder structure

ZHTML files go under `src/main/resources/web/zul/`:
```
src/main/resources/
├── metainfo/zk/
│   └── zk.xml              ← ZK config (requires <config-name>)
└── web/
    └── zul/
        ├── index.zhtml
        └── login.zhtml
```

- Do NOT place ZUL/ZHTML files in `src/main/resources/webapp/` — this causes ZK to use the wrong classpath root.
- Do NOT add stale `.zul` files directly under `web/` if `.zhtml` versions exist in `web/zul/`.

---

## zk.xml location and required fields

Place `zk.xml` at `src/main/resources/metainfo/zk/zk.xml` (NOT `webapp/WEB-INF/zk.xml`).

`<config-name>` is **required** — ZK throws `IllegalSyntaxException` without it.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<zk>
  <config-name>ssm</config-name>
  <device-config>
    <device-type>ajax</device-type>
  </device-config>
  <session-config>
    <session-timeout>60</session-timeout>
  </session-config>
</zk>
```

Do NOT add `<language-mapping>` or `<language-config>` for `.zhtml` extension — this forces ZK to use an XML (SAX) parser on ZHTML files, which breaks `<!doctype html>`. ZHTML parsing is handled by the richlet filter (zsoup/HTML parser) configured in `application.yml`.

---

## application.yml — ZK settings

```yaml
zk:
  zul-view-resolver-prefix: /zul
  zul-view-resolver-suffix: .zhtml
  resource-uri: /zkres
  update-uri: /zkau
  richlet-filter-mapping: "*.zhtml"
```

- Do NOT use `homepage: index` — handle the root URL with an explicit `@GetMapping("/")` controller instead.

---

## Spring Security — allowing ZK FORWARD requests

The security config must permit FORWARD dispatcher to ZK's classpath resource URL pattern:

```java
private static final String ZHTML_FILES = "/~./zul/**/*.zhtml";

auth.requestMatchers(new AndRequestMatcher(
  new DispatcherTypeRequestMatcher(DispatcherType.FORWARD),
  AntPathRequestMatcher.antMatcher(ZHTML_FILES)
)).permitAll();
```

This covers the FORWARD created by `return "forward:~./zul/<page>.zhtml"`.
