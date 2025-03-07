# Git Workflow Guide

## Overview
This document describes the Git workflow used in the Ti Penso project for managing parallel development of the ESP32 firmware and Android application.

## Branch Structure
```
master
└── temp-[type]
    ├── temp-embedded
    └── temp-app
```

## Branch Naming Convention

### Branch Types
- `temp-feat-feature-name` - For new features
  - Example: `temp-feat-ble-connection`
  - Use when: Adding new functionality to the project
  - Example: Adding a new BLE characteristic, implementing a new UI screen

- `temp-fix-issue-name` - For bug fixes
  - Example: `temp-fix-connection-timeout`
  - Use when: Fixing bugs or issues
  - Example: Resolving connection drops, fixing UI glitches

- `temp-refactor-component-name` - For code refactoring
  - Example: `temp-refactor-ble-service`
  - Use when: Restructuring code without changing functionality
  - Example: Improving BLE service architecture, reorganizing Android components

- `temp-chore-task-name` - For maintenance tasks
  - Example: `temp-chore-update-dependencies`
  - Use when: Updating dependencies, cleaning up code, or other maintenance tasks
  - Example: Updating Gradle version, cleaning up unused imports

- `temp-docs-component-name` - For documentation updates
  - Example: `temp-docs-api-documentation`
  - Use when: Updating documentation only
  - Example: Updating API docs, improving README files

### Component Branches
For each type of branch, create the corresponding component branches:
```
temp-[type]-feature-name
├── temp-embedded
└── temp-app
```

Example:
```
temp-feat-ble-connection
├── temp-embedded
└── temp-app
```

### When to Use Each Type

#### `temp-feat`
- Adding new functionality
- Implementing new features
- Creating new components
- Adding new integrations

#### `temp-fix`
- Fixing bugs
- Resolving issues
- Addressing user-reported problems
- Fixing crashes or errors

#### `temp-refactor`
- Improving code structure
- Optimizing performance
- Reducing technical debt
- Making code more maintainable

#### `temp-chore`
- Updating dependencies
- Cleaning up code
- Removing unused files
- Updating build configurations

#### `temp-docs`
- Updating documentation
- Adding new documentation
- Improving existing docs
- Creating new guides

## Workflow Steps

### 1. Starting a New Feature
```bash
# Create feature branch from master
git checkout master
git checkout -b temp-feature-feature-name

# Create component branches
git checkout -b temp-embedded
git checkout -b temp-app
```

### 2. Development Process
```bash
# In temp-embedded branch
git add .
git commit -m "feat(esp32): implement feature X"
git push origin temp-embedded

# In temp-app branch
git add .
git commit -m "feat(android): implement feature X"
git push origin temp-app
```

### 3. Merging Changes
```bash
# Switch to feature branch
git checkout temp-feature-feature-name

# Merge component branches
git merge temp-embedded
git merge temp-app

# Resolve any conflicts
# Push feature branch
git push origin temp-feature-feature-name
```

### 4. Merging to Master
```bash
# Switch to master
git checkout master

# Merge feature branch
git merge temp-feature-feature-name

# Push to master
git push origin master
```

### 5. Cleanup
```bash
# Delete local branches
git branch -d temp-embedded
git branch -d temp-app
git branch -d temp-feature-feature-name

# Delete remote branches (optional)
git push origin --delete temp-embedded
git push origin --delete temp-app
git push origin --delete temp-feature-feature-name
```

## Commit Message Convention
Use the following format for commit messages:
```
type(component): description
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Adding or modifying tests
- `chore`: Maintenance tasks

Components:
- `esp32`: ESP32 firmware changes
- `android`: Android app changes

Examples:
```
feat(esp32): add BLE characteristic for button state
fix(android): resolve connection timeout issue
docs(esp32): update pin mapping documentation
```

## Pull Requests
See [Pull Request Template](pr_template.md) for detailed guidelines on creating pull requests.

## Best Practices

### 1. Branch Management
- Keep branches up to date with master
- Delete branches after merging
- Use meaningful branch names
- Keep branches focused on single features

### 2. Commits
- Write clear commit messages
- Follow the commit message convention
- Keep commits focused and small

### 3. Merging
- Always merge to master through feature branches
- Resolve conflicts in feature branches
- Test after merging
- Keep master branch stable

### 4. Code Review
- Review changes before merging
- Test changes in both components
- Ensure compatibility between ESP32 and Android
- Document any breaking changes

## Common Scenarios

### 1. Feature Requires Only One Component
```bash
# Create feature branch
git checkout -b temp-feature/feature-name

# Create only needed component branch
git checkout -b temp-embedded  # or temp-app

# Develop and merge as usual
```

### 2. Hotfix Required
```bash
# Create hotfix branch from master
git checkout -b hotfix/issue-description

# Fix the issue
git add .
git commit -m "fix(component): description"

# Merge to master
git checkout master
git merge hotfix/issue-description

# Cleanup
git branch -d hotfix/issue-description
```

### 3. Feature Needs to be Abandoned
```bash
# Delete local branches
git branch -D temp-embedded
git branch -D temp-app
git branch -D temp-feature/feature-name

# Delete remote branches
git push origin --delete temp-embedded
git push origin --delete temp-app
git push origin --delete temp-feature/feature-name
```
