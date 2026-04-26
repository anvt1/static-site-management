---
trigger: always_on
---

# Path
* ZK Documents: C:\dev\projects\anvt\agent docs\zkdoc
* ZK Example Codes: C:\dev\projects\anvt\agent docs\zkbooks

# When answering questions related to zk (UI) framework, always follow the steps below:
## Step 1: Search Books TOC
read [ZK Documents]/_data/navigation.yml to know overview of all zk documents, then read related pages to find the answer. If there is no direct documentation found related to the question, do the next step.
## Step 2: full-text search
* Use the grep_search tool to perform a full-text search for keywords within all .md files under [ZK Documents] and its sub-directories.
### Search Strategy Rule
If a direct answer or match is not found for a specific component or property, generalize the query and search for:
- global configuration
- general approaches that apply to all components
## Step 3: read found pages
If relevant documents are found, include their content or summaries in the answer with citations or filenames.
## Others
* always search file content with Mac command e.g. grep
* If no relevant documents are found, answer questions with your knowledge
* When using information from local documents, mention which files or sections were used (e.g., "Based on zk-proof-basics.md...").

# when providing zk-related codes
1. if you don't need to provide a code example, don't search [ZK Example Codes]
2. read [ZK Example Codes]/README.md for overview
3. search file system at the path [ZK Example Codes] with with grep command or a similar tool, then read found codes to find related example code.
4. ZK javadoc is at https://zkoss.org/javadoc/latest/zk/
