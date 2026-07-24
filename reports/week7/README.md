# Mana and Artifice Forgotten Cantrips

This project is an add-on for the Minecraft Mana And Artifice mod, adding many new cantrips, such as Lightning, Spectral Bed and Force Consume.

[Week 6 Report](/reports/week6/README.md)

[Project backlog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues)

[Changelog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/CHANGELOG.md)

[Public sanitized demo video](https://youtu.be/dQw4w9WgXcQ) *(Placeholder: Replace with actual unlisted/public demo video link)*

## Sprint 5 Overview

- Goal: deliver the final course version of the project based on the customer feedback.
- Dates: 13.07.2026-19.07.2026

[Sprint 5 Backlog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues?q=is%3Aissue%20milestone%3ASprint-Week7)

[Assignment 6 / Sprint 5 milestone](https://github.com/Ultrad00d/MnA_Extra_Cantrips/milestone/5)

We estimate this sprint to weigh about 35 story points.

## Delivered Final Release

We successfully completed Sprint 5, accomplishing all planned tasks.

[SemVer Release v1.0.0](https://github.com/Ultrad00d/MnA_Extra_Cantrips/releases/tag/v1.0.0)

This final release includes:
- Completion and balancing of [US-010: Colossus Oak](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/139).
- Final polish and bug fixes for [US-013: Bubble Up](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/175) based on Week 6 customer trial feedback.
- Resolution of remaining minor bugs ([#226](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/226), [#227](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/227)).
- Comprehensive updates to all customer-facing documentation and handover artifacts.

## Documentation and Access Links

- [Main README.md](/README.md) (usage instructions is there)
- [CONTRIBUTING.md](/CONTRIBUTING.md)
- [AGENTS.md](https://github.com/Ultrad00d/MnA_Extra_Cantrips/blob/main/AGENTS.md)
- [Customer Handover Document](/docs/customer-handover.md)
- [Hosted documentation site](https://ultrad00d.github.io/MnA_Extra_Cantrips/)
- [Final Product Access Artifact](https://www.curseforge.com/minecraft/mc-mods/forgotten-cantrips)
- [Final Transition Outcome](https://github.com/Ultrad00d/MnA_Extra_Cantrips/blob/main/reports/week7/reflection.md)

## Customer Feedback Response (Sprint 5)

| Feedback Point from Week 6 Trial | Resulting PBI / Issue | Status in Final Product |
|:---------------------------------|:----------------------|:-----------------|
| Bubble Up cantrip mana cost felt slightly too hig | [#226: Balance Bubble Up mana cost](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/226) | Resolved |
| We need clearer instructions on dependency versions in README| [#227: Update README dependency links](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/227) | Resolved |
| Rework on Colossus Oak| [#228: Fix Colossus Oak terrain clipping](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/228) | Resolved |

## Quality Model, Testing, and UAT

We continue to test for Functionality separation, Mod startup time, and CI validation.

- [Testing information](/docs/testing.md) and [User Acceptance Tests](/docs/user-acceptance-tests.md)
- [Quality requirements](/docs/quality-requirements.md) and [Quality requirement tests](/docs/quality-requirement-tests.md)
- [Definition of Done](/docs/definition-of-done.md)
- [CI Pipeline](https://github.com/Ultrad00d/MnA_Extra_Cantrips/actions/workflows/build.yml)
- [Branch protection rules](https://github.com/Ultrad00d/MnA_Extra_Cantrips/rules?ref=refs%2Fheads%2Fmain)

**Week 7 UAT Summary:** All core UAT scenarios for the final product version were passed.

## Demo Day Preparation

The team has fully prepared for the Demo Day presentation.

## Sprint 5 Artifacts

- [Sprint Review Summary](/reports/week7/sprint-review-summary.md)
- [Sprint Review Transcript](/reports/week7/sprint-review-transcript.md)
- [Retrospective](/reports/week7/retrospective.md)
- [Reflection](/reports/week7/reflection.md)
- [LLM Usage Report](/reports/week7/llm-report.md)

## Final Product Status

The product has reached its final course version, and every requirement was passed.

## Contribution Traceability Table (Sprint 5)

| Contributor             | Issues | PRs/MRs | Review Activity and Testing | Quality, Automation & Transition | Documentation & Demo Day Prep |
|:------------------------|:---------------------|:---------------------|:---------------------|:---------------------|:---------------------|
| **alexm-gh**            | #226 | #235 | Reviewed #236, #237 | UAT execution, CI validation | Demo Day slide deck design |
| **Dima280807**          | #228 | #236 | Reviewed #235, #238 | Final build verification, mod startup time testing | Demo video recording and editing |
| **dtamindarov5839**     | #227 | #237 | Reviewed #235, #236 | Transition readiness verification | Updated `customer-handover.md`, `README.md` |
| **l1n0n**               | #229 (Docs) | #238 | Reviewed #237 | UAT scenario updates | Week 7 Reflection, Retrospective, LLM report |
| **notwindstone**        | #230 (CI) | #239 | CI testing, UAT support | Gradle build optimization, Colossus Oak Cantrip | Hosted documentation site updates, Rehearsal coordination |
| **Ultrad00d**           | #226, #228 (Balance) | #235, #236, #237, #238, #239 | Final approval on all PRs | Colossus Oak balancing, final release tagging | Sprint Review summary, Demo Day presentation lead |
