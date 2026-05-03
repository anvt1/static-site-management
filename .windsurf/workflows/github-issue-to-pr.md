---
description: Fetch a GitHub issue, post an implementation plan as a comment, implement, then create a PR
---

## GitHub Issue → Plan → Implement → PR

### Step 1: Collect inputs
Ask the user for:
- GitHub repo owner (e.g., `my-org`)
- GitHub repo name (e.g., `my-repo`)
- Issue number (e.g., `42`)
- Base branch to merge into (default: `main`)

### Step 2: Fetch issue context
Use the GitHub MCP tool to:
- Get the issue title, body, and all comments
- Summarize the requirements in your own words
- Identify acceptance criteria or any design constraints mentioned in comments

### Step 3: Draft and post an implementation plan
Before writing any code, create a clear plan with:
- Summary of what will be implemented
- List of files expected to be changed
- Any technical decisions or trade-offs
- Estimated steps

Post this plan as a comment on the issue using the GitHub MCP `create_issue_comment` tool.
Format the comment as:

```
## 🤖 Windsurf Implementation Plan

**Issue:** #{issue_number} — {issue_title}

### Summary
{brief summary of what will be done}

### Files to Change
- `path/to/file1` — reason
- `path/to/file2` — reason

### Steps
1. {step 1}
2. {step 2}
3. ...

### Notes
{any trade-offs, assumptions, or open questions}

---
_This plan was generated automatically. Implementation will begin now._
```

### Step 4: Confirm with user
Ask the user: "Plan posted to the issue. Shall I proceed with implementation?"

### Step 5: Create a feature branch
Run:
```
git checkout -b feat/issue-{number}-{short-description}
```
where `{short-description}` is 3-5 words from the issue title, hyphenated, lowercase.

### Step 6: Implement the changes
Follow the plan from Step 3. Make focused, minimal changes. Follow existing code style.

### Step 7: Commit and push
```
git add .
git commit -m "feat: {short summary} (closes #{issue_number})"
git push origin feat/issue-{number}-{short-description}
```

### Step 8: Create the Pull Request
Use the GitHub MCP tool to create a PR with:
- **Title:** `feat: {issue title}`
- **Base branch:** the branch from Step 1 input
- **Head branch:** the branch created in Step 5
- **Body:**
```
## Summary
{what was implemented}

## Changes
- {change 1}
- {change 2}

## Testing
{how to verify the changes}

Closes #{issue_number}
```

### Step 9: Post PR link to issue
After the PR is created, post a follow-up comment on the issue:
```
## ✅ PR Created

A pull request has been opened: {pr_url}
```
