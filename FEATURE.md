# Feature Checklist

## MVP (v0.1)
- [ ] ZK MVVM: Login page (Spring Security login flow)
- [ ] ZK MVVM: Register page
- [ ] ZK MVVM: Site list page (bind to `SiteService`)
- [ ] ZK MVVM: Create site form (bind to `SiteService`)
- [ ] ZK MVVM: Deployments list per site (bind to `DeploymentService`)
- [ ] ZK MVVM: Upload ZIP for preview (bind to `ZipDeployService`)
- [ ] ZK MVVM: Publish deployment (bind to deployment publish service)
- [ ] ZK MVVM: Payment request page (bind to `PaymentService`)
- [ ] ZK MVVM: Payment submit receipt + reference (bind to `PaymentService`)
- [ ] ZK MVVM: Admin pending payments list (bind to admin/payment service)
- [ ] ZK MVVM: Admin approve payment action (bind to admin/payment service)

## v0.2
- [ ] Replace HTTP Basic with session-based login (Spring Security)
- [ ] Custom domain mapping (model + API + UI)
- [ ] Better deployment history UI (diff, timestamps, status)
- [ ] Email notifications (payment approved, deploy published)

## Hardening / Quality
- [ ] Add integration tests for core APIs (sites + deployments + payments)
- [ ] Centralized error handling (consistent API error format)
- [ ] Rate limits / upload protection review

## Backlog / Ideas
- [ ] Team / multi-user site collaboration
- [ ] CDN integration
- [ ] Audit log for admin actions
