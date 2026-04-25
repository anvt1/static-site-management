# Static Site Manager (MVP)

## Run (Docker)

1. Start services

```bash
docker compose up --build
```

2. Open

- http://localhost:8080/index.zul

## Default admin

- Email: `admin@example.com`
- Password: `admin`

## Quick API test

- Register:

```bash
curl -X POST http://localhost:8080/register -H "Content-Type: application/json" -d "{\"email\":\"u1@example.com\",\"password\":\"pass\"}"
```

- Create site (basic auth):

```bash
curl -u u1@example.com:pass -X POST http://localhost:8080/api/sites -H "Content-Type: application/json" -d "{\"name\":\"My First Site\"}"
```

- List sites:

```bash
curl -u u1@example.com:pass http://localhost:8080/api/sites
```
