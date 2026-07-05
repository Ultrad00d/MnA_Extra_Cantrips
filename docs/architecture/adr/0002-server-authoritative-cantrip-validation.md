# ADR 0002: Server-Authoritative Cantrip Validation and Delayed Execution

## Status
Accepted

## Context
Cantrips like the "Spectral Bed" mutate the game world. If the client updates its local state or triggers block placement before the server validates it, world state desynchronization occurs. Desynchronization leads to visual glitches, block duplication exploits, or server-side exceptions.

## Decision
We implement a strictly server-authoritative state mutation loop. The client captures input and dispatches the cantrip request to the Mana and Artifice framework. The mod defers structural validation and execution until the request reaches the logical server thread. Invalid casts fail gracefully on the server, sending error feedback to the client without mutating the world state.

## Consequences
* **Positive:** Prevents client-side world corruption and structural hacking/exploits.
* **Positive:** Standardizes block and entity interaction workflows across all current and future cantrips.
* **Negative:** Adds a slight delay equal to the round-trip network latency before the player sees block placement.

## Quality Requirements Tracing
* Addresses **QR-002 (Delayed Runtime Work)**: Defers state changes safely until proper server validation occurs.
