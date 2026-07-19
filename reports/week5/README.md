# Mana and Artifice Forgotten Cantrips

This project is an add-on for the Minecraft Mana And Artifice mod, adding many new cantrips, such as Lightning, Spectral Bed and Force Consume.

[Project backlog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues)

[Changelog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/CHANGELOG.md)

[Public video demo link](youtu.be/2wzCpGCGy8U)

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
We plan to do work on 2 new cantrips ([US-007: Villager Mind-Wiping](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/26), [US-008: Spectral Armor](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/27)), created designs for Old Wizard Model ([#142](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/142), [#158](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/158)).
during this sprint, after which, using consumer feedback, we plan to prepare the project for transition during week 6. This sprint is going to last from Monday, June 29th, 2026 to Sunday, July 5th, 2026. 

[Sprint Backlog](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues?q=is%3Aissue%20milestone%3ASprint-Week5)

[Assignment 5/Sprint 3 milestone](https://github.com/Ultrad00d/MnA_Extra_Cantrips/milestone/3)

We estimate this sprint to be about 50 story points large.

## Delivered:

We finished Sprint 3, accomplishing most of what we planned.

[Semver Release v0.2](https://github.com/Ultrad00d/MnA_Extra_Cantrips/releases/tag/mvp-v2)

This release adds basic functionality for [US-001: Bubble Up](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/20) as well as bug fixes for another cantrips.

The addon is uploaded to curseforge: https://www.curseforge.com/minecraft/mc-mods/forgotten-cantrips

## Customer feedback:


Customer review [transcript](customer-review-transcript.md) and [summary](customer-review-summary.md)

User trial: the customer found the mod satisfactory but proposed a few changes.

[Customer handover document](/docs/customer-handover.md)

The customer was shown the repository documentation and found it clear and understandable.

## Transition readiness:

With this sprint, we've completed large parts of the remaining unimplemented cantrips, as well as improved on some existing ones. With the customer feedback from this week's review, we plan to finish up on the remaining cantrips and implementing customer feedback.

## Quality model and testing:

We test for Functionality separation (Modularity), Mod startup time (Time behavior), and CI validation (Faultlessness)


[Testing information](/docs/testing.md) and [user acceptance tests](/docs/user-acceptance-tests.md)

Our [quality requirements](/docs/quality-requirements.md) and [quality requirement tests](/docs/quality-requirement-tests.md)

Our [definition-of-done](/docs/definition-of-done.md) and [development-process](/docs/development-process.md)

Our [architecture/README](/docs/architecture/README.md), [static-view-artifact](/docs/architecture/static-view/component-diagram.puml), [dynamic-view-artifact](/docs/architecture/dynamic-view/spectral-bed-sequence.puml), [deployment-view-artifact](/docs/architecture/deployment-view/deployment-diagram.puml) and [ADR](/docs/architecture/adr/)

[Link to the CI pipeline](https://github.com/Ultrad00d/MnA_Extra_Cantrips/actions/workflows/build.yml)

Link to [Branch protection for the protected default branch](https://github.com/Ultrad00d/MnA_Extra_Cantrips/rules?ref=refs%2Fheads%2Fmain).

Our [AGENTS.md](/AGENTS.md)

## Reflection

[Retrospective](retrospective.md) and [reflection](reflection.md) on Sprint 3.


Contribution traceability table mapping each team member to issues, PRs/MRs, review activity, testing, quality, automation, or documentation work :
# Contribution traceability table
| Contributor             | Issues | PRs | Review Activity and Testing | Quality and automation | Documentation Work |
|:------------------------|:---------------------|:-|:-|:-|:-|
| **alexm-gh**            | [#143](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/143), [#145](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/145), [#147](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/147) | - | - | - |  |
| **Dima280807**          | [#127](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/127), [#128](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/128), [#129](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/129), [#130](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/130) | - |  | - |  |
| **dtamindarov5839**     | [#155](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/155), [#164](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/164), [#165](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/165) | - | - | - | Documentation (README, roadmap, fixes) |
| **l1n0n**               | [#149](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/149), [#151](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/151), [#185](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/185) | - | - | - | Documentation (Reflection, customer review summary, retrospective) |
| **notwindstone**        | [#180](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/180) |   | CI testing | Gradle building | The website one (`./vitepress/` |
| **Ultrad00d**           | [#142](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/142), [#158](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/158), [#171](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/171), [#178](https://github.com/Ultrad00d/MnA_Extra_Cantrips/issues/178) |   |  | - | Documentation (Project backlog) |

## Screenshots:

///скрины надо сделать
