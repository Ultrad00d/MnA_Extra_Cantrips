# Process Requirements (Same as the course documents)

These requirements define the reusable Scrum, workflow, and product-management expectations used across the course. Assignment documents may add stricter or more specific requirements for a particular week, but they must not redefine the same shared terms differently.

## Product Backlog Items and Scope

1. A Product Backlog item (PBI) is an issue or equivalent tracked item representing product work that improves the product.

2. PBIs may include user stories, bugs, technical work, infrastructure work, research, design, testing, deployment, and maintained product documentation or maintained workflow documentation that improves the product repository.

3. A Course Task is tracked work whose primary purpose is course reporting, grading evidence, submission packaging, or course administration rather than improving the product.

4. Course Tasks are not PBIs even if the team tracks them in the same platform.

5. By default, artifacts under `reports/` are Course Task artifacts.

6. Maintained artifacts such as `docs/user-stories.md`, `docs/roadmap.md`, `docs/definition-of-done.md`, root setup documentation, deployment/run instructions, interface specifications, and `CHANGELOG.md` are not Course Tasks when they improve the maintained product repository.

## Product Goal and Product Backlog Management

1. Maintain a Product Goal as the longer-lived target for the product. The Product Backlog should evolve toward that goal.

2. Maintain one current Product Backlog that acts as the single ordered source of product work for the team.

3. Product Backlog refinement is an ongoing recurring activity. Refine the backlog throughout the Sprint instead of postponing all clarification until Sprint Planning.

4. The Product Backlog must satisfy **DEEP**:

   * **Detailed appropriately:** near-term and high-priority PBIs are more detailed than distant PBIs.
   * **Emergent:** add, remove, split, merge, clarify, and reorder PBIs as the team learns.
   * **Estimated:** sufficiently understood PBIs are estimated before the team commits to them in a Sprint.
   * **Prioritized:** the backlog is ordered so the most valuable or important work is considered first.

5. Keep enough refined backlog near the top that the next Sprint can be planned without major preventable ambiguity.

## User Stories, Requirement Status, and Decomposition

1. A user-story issue must contain:

   * The stable user-story ID where applicable
   * The user-story statement
   * Relevant notes, constraints, assumptions, or open questions
   * Acceptance criteria

2. Use stable user-story IDs consistently once assigned. Do not change, reuse, or reassign them.

3. Requirement status for a user story is one of:

   * `Active` - an accepted, current product requirement
   * `Removed` - no longer considered a current product requirement

4. Preserve the stable IDs and history of removed stories. Explain why they were removed instead of deleting the requirement history.

5. Do not use a user-story issue as the main container for implementation subtasks.

6. When implementation, design, testing, deployment, or other supporting work needs to be tracked explicitly, create separate linked PBIs.

7. A separate linked PBI is required when the work needs its own implementer responsibility, review, acceptance criteria, estimation, or verification evidence.

8. User stories selected for a Sprint must be small enough to be completed within that Sprint. If a story is too large or unclear, split or refine it before Sprint Planning while preserving traceability.

9. A user story is completed only when all linked supporting PBIs required to satisfy its acceptance criteria are completed. A user-story issue does not require its own dedicated implementation PR/MR.

## Acceptance Criteria

1. Use `acceptance criteria` terminology for PBIs.

2. Acceptance criteria must be specific, observable, and testable.

3. Given/When/Then ([Gherkin](https://cucumber.io/docs/gherkin/reference)) is recommended for behavioral scenarios, but any clear and testable format is acceptable.

4. A PBI that is expected to be implemented, reviewed, or verified must not be treated as ready for execution without acceptance criteria appropriate to that work.

## Traceability and User-Story Index

1. Traceability means preserving the links between a requirement or user story and the downstream work and evidence that implement and verify it.

2. At minimum, maintain traceability between:

   * Stable user-story IDs
   * User-story issues
   * Linked supporting PBIs where applicable
   * Sprint assignment where applicable
   * Related PRs/MRs
   * Verification evidence

3. When a story is removed, split, superseded, or replaced, preserve its history and explain the change instead of deleting it.

4. When a team maintains a current user-story index such as `docs/user-stories.md`, that index is the authoritative registry of stable user-story IDs and current user-story membership, while the issue tracker remains the authoritative source for live issue details and execution state.

5. A current user-story index should mirror enough live issue metadata for quick traceability, such as the issue link, requirement status, current Work Status, and Sprint assignment where applicable.

## Sprint Cadence and Scrum Events

1. Use Sprints as the recurring container for development work, inspection, and adaptation.

2. Unless a TA explicitly approves another cadence, a Sprint runs from Monday to Sunday.

3. Each Sprint must have a Sprint Goal: a short value-focused statement describing what the team intends to deliver and why that Sprint is worthwhile.

4. Sprint Planning must produce:

   * A Sprint Goal
   * The selected Sprint PBIs
   * A workable plan to deliver them

5. The Sprint Backlog is the set of Sprint-selected PBIs plus the current delivery plan needed to achieve the Sprint Goal.

6. Use the Sprint milestone or equivalent Sprint container consistently so the Sprint Backlog remains inspectable.

7. Hold a Daily Scrum or equivalent daily developer coordination event during the Sprint to inspect progress toward the Sprint Goal and adapt the plan for the next day of work.

8. Conduct a Sprint Review with relevant stakeholders to inspect the delivered outcome, discuss what changed, collect feedback, and adapt the Product Backlog as needed.

9. Conduct a Sprint Retrospective after the Sprint Review and before the next Sprint Planning. Identify concrete improvements to quality, collaboration, tools, or process, and carry the most useful improvements into the next Sprint.

## Sprint Planning, Readiness, and Estimation

1. Estimate PBIs using Story Points and the Modified Fibonacci scale:

   ```text
   1, 2, 3, 5, 8, 13, 20, 40, 100
   ```

2. Estimation must be collaborative. Planning Poker is recommended, but another collaborative relative-estimation process is acceptable.

3. If the team cannot estimate a PBI confidently, split or clarify it before committing to it in a Sprint.

4. A Sprint-selected PBI must be sufficiently ready to start. At minimum, it must have:

   * A clear expected outcome
   * The required description and context
   * Acceptance criteria
   * An estimate
   * An implementer
   * A different reviewer

5. A Sprint-selected user story may require additional linked supporting PBIs before it is ready for execution.

6. A Sprint Goal should be a short value-focused statement that makes it possible to judge at the end of the Sprint whether the intended outcome was achieved.

## Sprint Roles and Evidence

1. Each Sprint-tracked PBI must name:

   * One implementer responsible for doing the work
   * One different reviewer responsible for reviewing the work and confirming it is ready for completion

2. If the selected platform does not provide a suitable native field for the reviewer, record that role in the issue template or issue body.

3. Each issue must describe the expected outcome clearly.

4. A linked PR/MR is the default implementation evidence for a change.

5. Explicit named deliverables are required only when the expected evidence is not obvious from a normal linked PR/MR, such as a design artifact, API specification, deployment artifact, or documentation artifact.

6. Implementation PRs/MRs normally link to the supporting PBIs that do the work. They do not need to link directly to the parent user-story issue when traceability is preserved through linked supporting PBIs, related PRs/MRs, and verification evidence.

## Work Status

Use the following Work Status values consistently wherever the course requires issue status tracking:

* `To Do` - the PBI remains in the Product Backlog and is not currently ready to start
* `Ready` - the PBI is selected for the current Sprint, assigned, estimated, has the required description and acceptance criteria, and can be started without major unanswered questions
* `In Progress` - work has started on the PBI
* `Review` - the current implementation is ready for review, and the issue-linked PR/MR is open or the review is actively happening
* `Done` - for a supporting or implementation PBI, the issue acceptance criteria are satisfied, the team Definition of Done is satisfied, the issue-linked PR/MR is merged into the protected default branch, and the PBI is complete for the Sprint; for a user-story issue, the story acceptance criteria are satisfied, the team Definition of Done is satisfied, and all linked supporting PBIs required to satisfy the story's acceptance criteria are reviewed, merged, verified, and marked `Done`

## Definition of Done

1. Every team must maintain a team-specific Definition of Done in:

   ```text
   docs/definition-of-done.md
   ```

2. The team Definition of Done defines the shared minimum completion standard for work in that repository.

3. A PBI may be marked `Done` only when both:

   * Its issue-specific acceptance criteria are satisfied
   * The team Definition of Done is satisfied

4. The team Definition of Done should, at minimum, require:

   * All issue acceptance criteria are satisfied
   * The work is reviewed by another team member
   * For user stories, the linked supporting PBIs provide the required implementation, review, and verification evidence
   * Required tests or checks pass
   * Verification evidence is preserved in the normal workflow artifacts

5. For supporting or implementation PBIs, `Done` normally also means the issue-linked PR/MR is merged into the protected default branch.

## Roadmap

1. Every team must maintain a current roadmap in:

   ```text
   docs/roadmap.md
   ```

2. `docs/roadmap.md` is the Sprint-by-Sprint delivery plan. It must not duplicate the full user-story index or repeat full mutable issue content that already lives in the issue tracker.

3. Structure the roadmap as a list of Sprints. For each Sprint, include:

   * Sprint name or number
   * Link to the corresponding Sprint milestone
   * Sprint start and finish dates
   * Sprint Goal
   * Short focus or expected outcome statement
   * Linked planned items for that Sprint, such as user stories and supporting PBIs

4. Keep the roadmap lightweight and traceable:

   * Use links to issues or other tracked PBIs instead of copying their full descriptions.
   * Summarize the intended Sprint outcome briefly instead of restating backlog metadata such as MoSCoW priority, Requirement status, Work Status, or full acceptance criteria.

5. Update the roadmap as the product direction and Product Backlog evolve.
