# Mana and Artifice Forgotten Cantrips

This project is an add-on for the Minecraft Mana And Artifice mod, adding many new cantrips, such as Lightning, Spectral Bed and Force Consume.

[Project backlog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues)

[Changelog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/CHANGELOG.md)

[Video demo link](https://youtu.be/2wzCpGCGy8U) - OLD

[Presentation slides](/reports/week6/presentation.pdf)

## Run and build instructions (as per root [README.md](/README.md)):


**To run this mod locally:**

- Have Minecraft 1.20.1 installed
- Have the Forge modloader 1.20.1-v47.4.20 (latest) installed
- Have the [Mana And Artifice Mod](https://www.curseforge.com/minecraft/mc-mods/mana-and-artifice) (version 3.1.11 (latest) for 1.20.1-forge) installed, including dependencies:  
  - Have the [Geckolib](https://www.curseforge.com/minecraft/mc-mods/geckolib) Mod (version 4.2.2 or newer for 1.20.1-forge) installed
  - Have the [Curios API](https://www.curseforge.com/minecraft/mc-mods/curios) Mod (version 5.14.1 or newer for 1.20.1-forge) installed

1. Locate your Minecraft installation folder (usually `%Appdata%\.minecraft`)
2. Paste the forgotten_cantrips-v%version%.jar into the `mods` folder
3. Launch the game from your desired launcher
---

**To build this mod locally:**

Using IntelliJ IDEA:
1. Clone the repository
2. In IntelliJ, select **File | Open** -> **Project from Existing Sources**
3. Select the directory containing the project and click **OK**
4. To compile, run "./gradlew build"

## This Sprint

Our [roadmap](/docs/roadmap.md) with plans for the current and the future sprint.

During this sprint we have decided to focus on customer-facing completion, trial handover, follow-up maintenance and preparation for transition of our project towards a stable demo for the Demo Day presentation.
We plan to implement about 2 new cantrips ([US-010: Colossus Oak](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/139), [US-013: Bubble Up](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/175), fix known bugs ([#223](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/223), [#224](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/224), [#225](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/225)), improve designs for existing cantrips ([#170](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/170), [#197](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/197), [#198](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/198))
during this sprint. This sprint is going to last from Monday, July 6th, 2026 to Sunday, July 12th, 2026. 

[Sprint Backlog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues?q=is%3Aissue%20milestone%3ASprint-Week6)

[Assignment 6/Sprint 4 milestone](https://github.com/Ultrad00d/MnA_Extra_Cantrips/milestone/4)

## Delivered:

We finished Sprint 4, completing most of the planned features.

[Semver Release v0.4](https://github.com/Ultrad00d/MnA_Extra_Cantrips/releases/tag/mvp-v3)

The addon is uploaded to curseforge: https://www.curseforge.com/minecraft/mc-mods/forgotten-cantrips

## Customer feedback:


Customer review [transcript](customer-review-transcript.md) and [summary](customer-review-summary.md)

User trial: the user found the mod intuitive 
[UAT](/docs/user-acceptance-tests.md) results summary: (TBA)

| Feedback point | Resulting PBI or issue | Status | Response |
|---|---|---|---|
| The customer... | [#0](...) | Done | ... |
| The customer... | [#0](...) | Not planned for this Sprint | ... |


## Quality model and testing:

We test for Functionality separation (Modularity), Mod startup time (Time behavior), and CI validation (Faultlessness)


[Testing information](/docs/testing.md) and [user acceptance tests](/docs/user-acceptance-tests.md)

Our [quality requirements](/docs/quality-requirements.md) and [quality requirement tests](/docs/quality-requirement-tests.md)

Our [definition-of-done](/docs/definition-of-done.md)

[Link to the CI pipeline](https://github.com/Ultrad00d/MnA_Extra_Cantrips/actions/workflows/build.yml)


Link to [Branch protection for the protected default branch](https://github.com/Ultrad00d/MnA_Extra_Cantrips/rules?ref=refs%2Fheads%2Fmain).

## Reflection

[Retrospective](retrospective.md) and [reflection](reflection.md) on Sprint 2.


Contribution traceability table mapping each team member to issues, PRs/MRs, review activity, testing, quality, automation, or documentation work : (TBA)
# Contribution traceability table
| Contributor             | Issues | PRs | Review Activity and Testing | Quality and automation | Documentation Work |
|:------------------------|:---------------------|:-|:-|:-|:-|
| **alexm-gh**            | Work on [#20 Force Consume](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/20), including [8 sub-issues](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues?q=is%3Aissue%20assignee%3Aalexm-gh%20milestone%3ASprint-Week4%20no%3Asub-issue) | [11 PRs for sub-issues of #20](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pulls?q=is%3Apr+author%3Aalexm-gh+milestone%3ASprint-Week4+) | [Reviews for 4 PRs](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pulls?q=is%3Apr+reviewed-by%3Aalexm-gh+milestone%3ASprint-Week4) | - | Changelog updates, reflection |
| **Dima280807**          | Continued work on [#25 Spectral Boat](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/25), including [1 sub-issue](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/61) | [PR for sub-issue of #25](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/94) | [Review for "fix: spectral donkey saddle drop on death"](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/125) | - | - |
| **dtamindarov5839**     | [Bug-fix](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/112) for [#24 Spectral Donkey](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/24) and [#25 Spectral Boat](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/25), [bug-fix](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/124) for [#24 Spectral Donkey](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/24), [week 4 documentation](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/109) | [2 PRs](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pulls?q=is%3Apr+author%3Adtamindarov5839+milestone%3ASprint-Week4+) for bug-fixes and a PR for documentation | [Review for Spectral bed summon fix](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/117) | - | [Week 4 project delivery index and other documentation changes](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/109) |
| **l1n0n**               |  |  [CHANGELOG fix](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/121) | [Review for Spectral Donkey](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/110) | - | [Week 4 presentation slides](/reports/week4/presentation.pdf), retrospective and roadmap |
| **notwindstone**        | [#77 Implement a CI action to build the mod on commits](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/77) and [#78 Re-design icons](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/78) | [PR for #77](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/83), [PR for #78](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/132), and [PR for #86](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/126) | [Review for Force Consume Basic Functionality](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/96), [review for Spectral Donkey Cantrip Icon](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/133) | [CI action to build the mod on commits](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/77), quality tests | documentation, changelog, Moodle PDF report |
| **Ultrad00d**           | Work on [#24 Spectral Donkey](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/24), including 3 sub-issues ([#79](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/79), [#80](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/80), [#81](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/81)); Work on #25 Spectral Boat, including [1 sub-issue](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/63); [Bug-fix](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/116) for [#22 Spectral Bed](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/22)  | [Spectral Donkey](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/110), [Spectral bed summon fix](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pull/117) | [Reviews for 4 PRs](https://github.com/Ultrad00d/MnA_Extra_Cantrips/pulls?q=is%3Apr+reviewed-by%3Aultrad00d+milestone%3ASprint-Week4+) | - | documentation |


## Screenshots:

![Sprint milestone](https://github.com/Ultrad00d/MnA_Extra_Cantrips/blob/screenshot-fix/reports/week4/images/SprintMilestone.png)

![Latest protected-default-branch CI run and test report](https://github.com/Ultrad00d/MnA_Extra_Cantrips/blob/screenshot-fix/reports/week4/images/LatestCIRunAndTestResult.png)

![Branch protection or rules evidence](https://github.com/Ultrad00d/MnA_Extra_Cantrips/blob/screenshot-fix/reports/week4/images/mainBranchProtection.png)

![SemVer release](https://github.com/Ultrad00d/MnA_Extra_Cantrips/blob/screenshot-fix/reports/week4/images/semverRelease.png)

![Example reviewed issue-linked PR/MR](https://github.com/Ultrad00d/MnA_Extra_Cantrips/blob/screenshot-fix/reports/week4/images/ExampleReviewedPR.png)
