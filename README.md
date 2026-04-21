# Coding Assistants Workshop

## 🎯 Workshop Goal

Welcome! In this workshop, you'll learn how to work with **OpenCode**, an AI coding assistant framework, by progressively building your own AI-powered coding workflows. You'll start with manual implementation, then gradually introduce AI assistance to automate tedious tasks—ultimately orchestrating multiple AI agents to solve complex problems.

By the end, you'll understand how to leverage AI assistants effectively in your development workflow.

## 📋 Prerequisites & Setup

1. **Install OpenCode**: Download from [https://opencode.ai](https://opencode.ai)
2. **Clone this repository**: `git clone <repo-url>`
3. **Check out the baseline branch**: `git checkout baseline`
4. **Optional**: Read the [OpenCode docs](https://opencode.ai/docs) for detailed guidance

For the project:

- JAVA 21
- node >=21
- docker

### Project Overview

This is a **Maven multi-module project** with two main parts:

- **customer-manager-client**: A Vite + React + TypeScript frontend (in `customer-manager-client/`)
- **customer-manager-server**: A Spring Boot backend (in `customer-manager-server/`)

All exercises focus on implementing a **"Customer Deletion" feature** with a confirmation dialog—a real-world feature that touches both frontend and backend.

---

## 🏋️ The Exercises

> **Note**: Each exercise builds on the `baseline` branch. Create local branches for your work (e.g., `ex-01`, `ex-02`) but **don't push them** to origin—they're for learning only. After the workshop, check out the `main` branch to see full working examples of all exercises.

### Exercise #1: Build Without Help ⏱️ ~20–25 min

**What you'll learn**: Getting a feeling, what potential and also which problems AI code generation has

**What to do**:
1. Clone repository: 
2. Check out the `baseline` branch
3. Create a new local branch called `ex-01`
4. Implement the **"Customer Deletion" feature** without any AI assistance:
   - Add a "Delete" button to the customer list in the UI
   - Create a backend endpoint to delete a customer
   - Handle the confirmation dialog and success/error states
5. Test your implementation manually in the running app
6. Commit your changes (no need to push)

**How you'll know you're done**: The feature works end-to-end: you can delete a customer and see it removed from the list.

---

### Exercise #2: Meet Your AI Assistant ⏱️ ~25–30 min

**What you'll learn**: How to use OpenCode commands to analyze your codebase and get AI-assisted implementation plans.

**What to do**:
1. Check out `baseline` again and create a new branch called `ex-02`
2. Use the `/init` command to let OpenCode analyze the repository (it will create `AGENTS.md`)
3. Ask the **Planning Agent** to create a detailed implementation plan for the customer deletion feature
4. Ask the **Build Agent** to implement the feature based on the plan
5. Let the agent do most of the work—only step in if something goes wrong
6. Commit your changes

**How you'll know you're done**: The feature is implemented by the AI with minimal manual intervention from you.

---

### Exercise #3: Write Your First Command ⏱️ ~25–30 min

**What you'll learn**: How to extend OpenCode with custom commands tailored to your needs.

**What to do**:
1. Use your `ex-02` branch and create a new branch called `ex-03`
2. Implement a custom `/review` command that:
   - Reviews only the changes between `ex-03` and `baseline` (not the entire codebase)
   - Checks for **clean code** practices
   - Checks for **security issues**
   - Assigns severities: `medium`, `high`, `critical`
   - Outputs results with **file names and line numbers** for easy navigation
3. Test your review command on your implementation
4. Compare it with the review command in the `main` branch (optional challenge)

**Bonus (optional)**: Implement automatic commit squashing with conventional commit messages (e.g., `feat: add customer deletion`).

**How you'll know you're done**: The `/review` command runs and produces meaningful feedback on your code changes.

---

### Exercise #4: Create a Skill ⏱️ ~20–25 min

**What you'll learn**: How to create reusable skills that customize agent behavior.

**What to do**:
1. Use your `ex-03` branch and create a new branch called `ex-04`
2. Create a new skill that ensures every agent **commits its code after each message** with this format:

```
temp: <agent-name>

<prompt-or-description>
```

3. Test this skill by triggering an agent and observing the commits
4. Commit your skill definition

**How you'll know you're done**: Agents automatically commit after each message using your defined format.

---

### Exercise #5: Integrate External Tools ⏱️ ~25–35 min

**What you'll learn**: How to extend OpenCode with external tools and verify implementations programmatically.

**What to do**:
1. Use your `ex-04` branch and create a new branch called `ex-05`
2. The **confirmation dialog specification** is already defined in `specifications/2026-04-21-delete-customer.md`
3. Implement the confirmation dialog according to the spec (if not already done)
4. Create a verification workflow using the **Playwright MCP** (Model Context Protocol) that:
   - Validates the dialog appears when the delete button is clicked
   - Confirms both "Confirm" and "Cancel" buttons work
   - Verifies the dialog styling matches the spec
5. Let OpenCode use the Playwright MCP to verify your implementation automatically

**How you'll know you're done**: The Playwright verification script runs successfully and confirms the dialog meets all requirements.

---

### Exercise #6: Orchestrate Multiple Agents ⏱️ ~40–50 min

**What you'll learn**: How to define a complete workflow with multiple specialized agents working together.

**What to do**:
1. Use your `ex-05` branch and create a new branch called `ex-06`
2. Define and configure **five specialized agents**:
   - **requirements-engineer**: Analyzes requirements
   - **architect**: Designs the technical solution
   - **developer**: Implements the code
   - **tester**: Writes and runs tests
   - **reviewer**: Reviews code quality and architecture
3. Create a workflow that orchestrates these agents to implement a new feature
4. Ensure agents execute in the right order and each one triggers the next based on completion
5. Test the workflow end-to-end

**How you'll know you're done**: A full feature implementation is completed by the agent workflow with minimal human intervention.

---

## 📚 Quick Links

- **OpenCode Documentation**: [https://opencode.ai/docs](https://opencode.ai/docs)
- **Customer Deletion Specification**: `specifications/2026-04-21-delete-customer.md`
- **Full Working Example**: Check out the `main` branch after completing the workshop to see all solutions

---

Good luck! 🚀
