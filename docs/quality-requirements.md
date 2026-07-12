# Quality Requirements

## QR-001: Functionality separation

**ISO/IEC 25010 sub-characteristic:** Modularity

**Scenario:** Logic for different features of the addon is separated into different `.java` files.

**Why this matters:** This ensures that the mod is easier to modify and maintain in the future.

**Linked quality requirement tests:** N/A, TA informed

**Related ADR:** [ADR 0001: Mod Modularity and Registry Separation](architecture/adr/0001-mod-modularity-and-registry-separation.md)


## QR-002: Mod startup time

**ISO/IEC 25010 sub-characteristic:** Time behaviour

**Scenario:** When a user with an average desktop computer starts up Minecraft with the Mana And Artifice (+dependencies) and this addon installed, the addon shouldn't add more than 30 seconds to the total loading time.

**Why this matters:** The loading time added by this addon must be low to improve user experience and reduce frustration.

**Linked quality requirement tests:** N/A, environment with required capabilities isn't readily available, TA informed

**Related ADR:** [ADR 0002: Server-Authoritative Cantrip Validation and Delayed Execution](architecture/adr/0002-server-authoritative-cantrip-validation.md)


## QR-003: CI validation

**ISO/IEC 25010 sub-characteristic:** Faultlessness

**Scenario:** After every commit, a CI pipeline builds the addon into a valid `.jar` file that is up-to-date with the latest changes to ensure that the build process can proceed without error.

**Why this matters:** This helps catch defects early, improving user confidence in this project and reducing development costs.
https://github.com/Ultrad00d/MnA_Extra_Cantrips/edit/109-week4-documentation/docs/quality-requirement-tests.md#qr-003-ci-validation

**Linked quality requirement tests:** [QRT-003](quality-requirement-tests.md#qr-003-ci-validation)

**Related ADR:** [ADR 0003: Automated CI/CD and Strict Toolchain Versioning](architecture/adr/0003-automated-ci-cd-and-strict-toolchain-versioning.md)
