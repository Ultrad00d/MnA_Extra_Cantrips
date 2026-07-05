# Development Process

This document serves as the canonical, maintained guide for our team's actual development process. It reflects our current day-to-day workflow state. All team members are expected to follow these practices.

---

## 1. Project Management & Backlog Configuration

We use **GitHub Issues** to manage all Product Backlog Items (PBIs) and our weekly Sprint scope.

### Backlog Management
* **Product Backlog:** All user stories, features, TBD items and Bugs found are created and maintained as individual GitHub Issues.
* **Sprint Backlog:** At the start of the week, the Product Manager cherry-picks a high-priority selection of user stories and features needed for that week and moves them into the active sprint scope, giving labels to them for the most visibility.

### Board Columns & Workflow States

Work moves sequentially from left to right across our GitHub project board based on the following entry criteria:

| Board State / Column | Description | Entry Criteria (Move Conditions)                                                  |
| :--- | :--- |:----------------------------------------------------------------------------------|
| **Backlog** | Items with unspecified requirements, drafts, or upcoming features. | Default state for new issues; requirements are still TBD.                         |
| **Ready** | Fully defined work items ready for development. | The PM/team has finalized requirements, and engineering work may now start.       |
| **In Progress** | Work currently under active development. | A developer assigns themselves to the issue and moves it here when coding begins. |
| **In Review** | Code is complete and awaiting peer feedback. | A Pull Request has been opened and linked to the corresponding GitHub Issue(s).   |
| **Done** | Feature is fully complete and integrated. | The associated PR has been successfully merged into the `main` branch.            |

---

## 2. Git and Code Review Workflow

Our team adapts a feature-branch workflow structured directly around our GitHub Issue hierarchy.

### Issue Lifecycle & Hierarchy
* **Core Issue (User Story):** Every piece of substantive work begins with a core issue (e.g., a User Story like `#22`). This core issue serves as the parent tracking item containing the full context and overall Acceptance Criteria. The basic drive for all other issues are `User Story` issues, that are mostly created by a PM, based on community suggestions and customer requests. If at any point the system incorrect behavior was noticed, the `Bug` issue is created, explaining what went wrong in detail.
* **Sub-Issues:** To break down complex requirements, developers create and link smaller sub-issues under the core User Story issue. Each sub-issue targets specific components or individual Acceptance Criteria.
* **Resolution Tracking:** A Core Issue or Sub-Issue is only considered resolved when all its specified Acceptance Criteria have been successfully verified and its associated code changes are integrated.

### Branching Strategy
* **Base Branch:** `main` is our production source of truth. All core feature development branches must be cut from `main`.
* **Branch Naming Convention:** Feature branches are named using the pattern `[issue-number]-[short-description]`.
    * *Example:* `42-learning-cantrips`
* **Development Flow Options:** Depending on the scope and complexity of the User Story, developers may choose one of two execution paths:
    1. **Single Branch Layout:** For smaller or closely coupled stories, the developer crafts all changes within the primary feature branch (`42-learning-cantrips`), checking off each of the Acceptance Criteria and sub-issues step-by-step.
    2. **Sub-Branching Layout:** For larger, multi-developer, or multi-part stories, developers cut secondary sub-branches directly from the core branch (e.g., `87-cantrip-logic` from the branch `20-force-consume`). These sub-branches are subsequently reviewed and merged back into the core branch (`22-user-story-desc`). Once all features are implemented, an additional review is conducted, aimed at complete system stability checks. 
* **Ensuring transparency**: Once the work on the issue has started, the issue must also link a corresponding branch or a PR, when the work on this is issue is done.  

### Pull Requests (PRs)
* **Submission:** When work on a branch is ready for review, open a PR.
    * Sub-branches must target the core feature branch (`20-force-consume`).
    * Core feature branches must target the `main` branch.
* **PR Linkage:** Every PR must explicitly link to the issue(s) it resolves using GitHub keywords in the description (e.g., `Closes #22`) to automatically close them upon merge.

### Review Process
* **Criteria:** Reviewers evaluate code against the specific sub-issues or core Acceptance Criteria defined in the linked GitHub Issues.
* **Feedback:** Reviewers must provide clear, actionable feedback. If changes are required before a merge can proceed, the PR must be marked as "Changes Requested."
* **Example:** [Spectral bed bug fixes](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/67)

### Merging & Closing Issues
* **Merge Strategy:** We only use Merge when pulling feature branches into `main` to maintain a clean git history.
* **Resolution:** Once a PR is pulled into the `main` branch, the linked core issue and its respective sub-issues are updated, closed, and the feature branch is safely deleted.

---

## 3. Configuration & Secret Management

### Secrets Storage
* **No Secrets Committed:** Because this project is a Minecraft add-on, it contains no API keys, private database credentials, or server-side secrets. All game logic, behavior packs, and resource packs are entirely open/public within the context of the addon's installation.

### Ignored Files
The following local files and folders are strictly ignored in our `.gitignore` to prevent clutter and conflicts:
* Local Minecraft client directories (`run`, `run-data`)
* Build caches (directories like `build`, `.gradle`, `.idea`)
* Local `libs` directory, that is used for references and temporal saves

### Runtime Configuration
* **Metadata & Definition:** Mod identification, versioning, and base dependencies are statically defined in `gradle.properties` and injected into the compiled `mods.toml` artifact during the build process.
* **Game Configuration:** Dynamic runtime configurations (such as game balance adjustments or client-side settings) are handled natively through Forge's built-in config registry.

---

## 4. Reproducible Development Environment

Because this project utilizes the Minecraft Forge MDK and Gradle, our local environment setup relies heavily on the Java Development Kit and built-in Gradle tasks to guarantee that all developers are working with matching toolsets.

### Expected Tooling & Versions
* **Java Development Kit (JDK):** JDK17 is required, since this is a requirement to run Minecraft 1.20.1.
* **Build System:** Gradle (handled entirely via the repository's included `./gradlew` wrapper script to lock down the exact version).
* **Supported IDEs:** IntelliJ IDEA, Eclipse or VS Code.

### Initial Setup Steps
To set up your local workspace from scratch, execute the following workflow:

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Ultrad00d/MnA_Extra_Cantrips
   cd MnA_Extra_Cantrips
   ```

2. **Verify JDK Installation:** Ensure your `JAVA_HOME` environment variable points to the correct required JDK version by running:
   ```bash
   java --version
   ```

3. **Download Dependencies & Decompile Minecraft:** Run the Forge setup tasks via the Gradle wrapper to prepare the decompiled game binaries:
   ```bash
   # On macOS/Linux:
   ./gradlew genSources
   
   # On Windows (cmd):
   gradlew.bat genSources
   ```

4. **Generate IDE Configurations:** Set up your specific development environment using your preferred IDE task:
    * **For IntelliJ IDEA:** Run `./gradlew genIntellijRuns` (or simply import the project as a Gradle project and let IDEA index it).
    * **For Eclipse:** Run `./gradlew eclipse` to construct the `.classpath` and `.project` structures.

5. **Run the Test Client:** Verify the reproducibility of the workspace by launching the game binary locally:
   ```bash
   ./gradlew runClient
   ```
   This populates the local `run/` and `run-data/` folders safely ignored by version control.

6. **Build from sources:** Get an output `.jar` file generated from sources by running:
   ```bash
   ./gradlew build
   ```
   The file will appear in `build/libs` directory
---

## 5. Continuous Integration & Deployment (CI/CD)

### CI Pipeline & Automated Validation
Because our project is a specialized Minecraft mod that interfaces deeply with the runtime game client, full automated functional testing (such as unit testing or integration testing) is practically impossible to execute deterministically in an unheaded CI environment.

Instead, our automation focuses strictly on verification of compilation sanity. Every time a branch is pushed or a Pull Request is opened, our CI workflow triggers the following validation:

1. **Build Verification:** The pipeline runs the standard Gradle compilation task via the project wrapper:
   ```bash
   ./gradlew build
   ```
2. **Success Criteria:** The automated runner ensures that the source code compiles error-free against the specific Minecraft Forge MDK setup and successfully outputs the compiled `.jar` mod artifact.

If compilation fails, the build is flagged as broken, and the associated Pull Request **cannot be merged** until the compilation errors are fixed.

### Deployment Automation & Continuous Delivery
* **Continuous Delivery:** There is no automated continuous delivery or automatic distribution of build artifacts to CurseForge and Modrinth mod-hosting platforms.
* **Release Process:** Stable build artifacts are manually verified locally by launching the game via `./gradlew runClient`. Once a milestone is met and manually signed off, production-ready `.jar` distributions are manually packaged and published.