## Learning points:

- We improved our understanding about trunk-based workflow approach by merging small changes to the branch; we utilized the approach for both `main` and its subbranch `20-force-consume`.
- We extended our knowledge about game structure: we got know how to implement effects, items, command line logic, config files, and so on.

## Validated assumptions:

- [x] Receiving a PR review on small changes is very difficult and high-costly
- [x] The "Force Consume" cantrip will indeed provide a range of thoughts for new ideas

## Friction and gaps:

The approach, where all subtasks are reviewed, turned out to be unviable as small PR took much time for a feedback. Then, a trunk-based workflow over `20-force-consume` was applied, which accelerated the development, however, many remarks appeared when it came to the final PR.

The implementation of some subtasks led to surprises as their actual complexity was a way higher.

The time management remains a big trouble for the team as the paperwork is usually done in a hurry.

## Planned response:

We shall establish a team agreement on review turnaround time so subtask PRs get timely feedback.
We shall discuss and formally decide the Force Consume cantrip renaming to Devour with the team lead and customer.
We shall push for clear task ownership — know exactly what each team member develops and what each team member reviews each sprint.
We shall revise Daily Scrums so that tasks and blockers become visible early.
We shall set a workflow for customer feedback: suggestions go through team lead for approval before affecting implementation.