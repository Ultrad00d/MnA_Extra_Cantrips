# Mana and Artifice Forgotten Cantrips

This project is an add-on for the Minecraft Mana And Artifice mod, adding many new cantrips, such as Lightning, Spectral Bed and Force Consume.

[Project backlog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues)

[Changelog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/CHANGELOG.md)

[Public video demo link](https://youtu.be/J0YGRPP-3UY)

[Hosted documentation site](https://ultrad00d.github.io/MnA_Extra_Cantrips/)

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

Our [CONTRIBUTING.md](/CONTRIBUTING.md)

## This Sprint

Our [roadmap](/docs/roadmap.md) with plans for the current and the future sprint.

During this sprint we have decided to focus on customer-facing completion, trial handover, follow-up maintenance and preparation for transition of our project towards a stable demo for the Demo Day presentation.
We plan to do work on 2 new cantrips ([US-010: Colossus Oak](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/139), [US-013: Bubble Up](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/175), fix known bugs ([#223](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/223), [#224](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/224), [#225](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/225)), improve designs for existing cantrips ([#170](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/170), [#197](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/197), [#198](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/198))
during this sprint, after which, using consumer feedback, we plan to prepare the project for transition during week 7. This sprint is going to last from Monday, July 6th, 2026 to Sunday, July 12th, 2026. 

[Sprint Backlog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues?q=is%3Aissue%20milestone%3ASprint-Week6)

[Assignment 6/Sprint 4 milestone](https://github.com/Ultrad00d/MnA_Extra_Cantrips/milestone/4)

We estimate this sprint to be about 50 story points large.

## Delivered:

We finished Sprint 4, accomplishing most of what we planned.

[Semver Release v0.4](https://github.com/Ultrad00d/MnA_Extra_Cantrips/releases/tag/mvp-v3)

This release adds basic functionality for [US-013: Bubble Up](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/175) as well as a few design improvements and bug fixes.

The addon is uploaded to curseforge: https://www.curseforge.com/minecraft/mc-mods/forgotten-cantrips

## Customer feedback:


Customer review [transcript](customer-review-transcript.md) and [summary](customer-review-summary.md)

User trial: the customer found the mod satisfactory but proposed a few changes.

| Feedback point | Resulting PBI or issue | Status | Response |
|---|---|---|---|
| The customer... | [#0](...) | Done | ... |
| The customer... | [#0](...) | Not planned for this Sprint | ... |

[Customer handover document](/docs/customer-handover.md)

The customer was shown the repository documentation and found it clear and understandable.

## Transition readiness:

With this sprint, we've completed large parts of the remaining unimplemented cantrips, as well as improved on some existing ones. With the customer feedback from this week's review, we plan to transition our product towards a stable demo during week 7 by finishing up on the remaining cantrips and implementing customer feedback.

## Quality model and testing:

We test for Functionality separation (Modularity), Mod startup time (Time behavior), and CI validation (Faultlessness)


[Testing information](/docs/testing.md) and [user acceptance tests](/docs/user-acceptance-tests.md)

Our [quality requirements](/docs/quality-requirements.md) and [quality requirement tests](/docs/quality-requirement-tests.md)

Our [definition-of-done](/docs/definition-of-done.md)

[Link to the CI pipeline](https://github.com/Ultrad00d/MnA_Extra_Cantrips/actions/workflows/build.yml)


Link to [Branch protection for the protected default branch](https://github.com/Ultrad00d/MnA_Extra_Cantrips/rules?ref=refs%2Fheads%2Fmain).

Our [AGENTS.md](/AGENTS.md)

## Reflection

[Retrospective](retrospective.md) and [reflection](reflection.md) on Sprint 4.


Contribution traceability table mapping each team member to issues, PRs/MRs, review activity, testing, quality, automation, or documentation work : (TBA)
# Contribution traceability table
| Contributor             | Issues | PRs | Review Activity and Testing | Quality and automation | Documentation Work |
|:------------------------|:---------------------|:-|:-|:-|:-|
| **alexm-gh**            |  |   |  | - |  |
| **Dima280807**          |  |   |  | - |  |
| **dtamindarov5839**     |  |   |  | - | Documentation (README, roadmap, fixes) |
| **l1n0n**               |  |   |  | - | Documentation (Reflection, customer review summary, retrospective) |
| **notwindstone**        |  |   |  |   |  |
| **Ultrad00d**           |  |   |  | - |  |

Igor Naumov & Wizard dialogue system \\
Dmitry Mulianov & Empower visual effects and started making Spectral Slime \\
Linar Kasimov & Bubble Up cantrip logic \\
Aleksandr Mankov & Documentation \\
Dayan Tamindarov & Documentation \\
Aidar Suleimanov & CI \\

## Screenshots:

![Sprint milestone](https://github.com/Ultrad00d/MnA_Extra_Cantrips/tree/main/reports/week6/images/SprintMilestone.png)

![SemVer release](https://github.com/Ultrad00d/MnA_Extra_Cantrips/tree/main/reports/week6/images/semverRelease.png)

![Example reviewed issue-linked PR/MR](https://github.com/Ultrad00d/MnA_Extra_Cantrips/tree/main/reports/week6/images/ExampleReviewedPR.png)
