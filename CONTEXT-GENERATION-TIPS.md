# Context Generation Tips

How the `CLAUDE.md` / `AGENT-USAGE.md` docs in this repo were created, and guidance for doing the same in other repos. This captures the original prompt, the refinements made along the way, and practical lessons.

## The original prompt

> We're going to create comprehensive and concise CLAUDE.md context for this repo. Create the overall CLAUDE.md with basic purpose information about the gradle modules, then for each module, create more detailed CLAUDE.md files explaining the code and how to edit it. In the root of the project, create an AGENT-USAGE.md doc explaining how an agent should use the library, and the purpose of each module. For code editing, include our overall KMP guidance:
> - prefer interfaces over expect/actual classes wherever possible
> - use expect/actual factory functions for defaults, but always structure them in a way that allows for specific usage configuration

## The document structure this produces

Three layers, each with a distinct audience and altitude:

1. **Root `CLAUDE.md`** — orientation for an agent landing in the repo: what the project is, a module table with one-line purposes and the dependency chain, build/test commands and their gotchas (lint gates, binary-compatibility validation, publish-to-mavenLocal-before-samples), and the team's cross-cutting code guidance (the KMP rules above).
2. **Per-module `CLAUDE.md`** — for an agent *editing* that module: key files with actual API signatures, source-set / expect-actual structure, non-obvious design decisions that must be preserved (e.g. kermit-io's IO-error recovery, the `os_log.def` cinterop constraints, kermit-simple's manual source-set wiring), and module-specific editing rules.
3. **Root `AGENT-USAGE.md`** — for an agent *consuming* the library from another project: which artifact to depend on for which need, core concepts, working code examples, and per-module usage notes including platform restrictions.

Keep maintainer docs (CLAUDE.md) and consumer docs (AGENT-USAGE.md) separate. An agent editing this repo and an agent adding Kermit to an app need different information, and mixing them dilutes both.

## Purpose of the sub-module guidance (refinements made after the first pass)

Two clarifications from the initial review are worth applying everywhere:

- **Don't repeat repo-wide guidance in sub-module files.** Claude reads *all* CLAUDE.md files along the path from the root to wherever it's working, so root-level policy (like the KMP rules) is already in context when editing a module. Per-module files should contain only *module-specific* facts and constraints. It's fine for a module doc to show how a repo-wide pattern manifests locally ("this module has zero expect/actual — platform variation is injected via constructor; keep it that way"), but not to restate the rule itself.
- **Capture intended extension points explicitly, with links.** Example added here: developers who want Kermit's core but their own logging API should build on `kermit-core` (extending `BaseLogger`) rather than wrapping `kermit` — see <https://touchlab.co/kermit-custom-logger>. This kind of guidance belongs in three places: the consumer doc (how to do it), the root module table (that the option exists), and the relevant module's CLAUDE.md (as a constraint on maintainers — keep that use case working).

## Practical guidance for generating context in a repo

**Explore before writing.**
- Start from the build system, not the source tree: `settings.gradle.kts` (or workspace manifest) gives the authoritative module list, including remapped/disabled modules.
- Fan out exploration in parallel — one agent per module group (core modules, extensions, build/CI/samples) — and have each report concrete file paths, public API signatures, source-set structure, expect/actual inventories, and build configuration. Write nothing until the reports are in.
- Explore the *whole* stack: convention plugins, CI workflows, release process, and samples all produce doc content (build commands, gates, gotchas) that pure source reading misses.

**Verify, don't trust summaries.**
- Spot-check every API signature, parameter name, and default value cited in the docs against the actual source before finishing (a quick grep of constructors/signatures is enough). Summaries drift; docs with wrong parameter names are worse than no docs.
- Treat existing informal docs as narrative intent, not fact. This repo's `TEMP_OVERVIEW.md` used an outdated parameter name; the source is the truth. When you find stale docs, flag them for deletion rather than propagating their content.
- Recent git history is a source of "why" documentation: the IO-exception handling in kermit-io and the kermit-ktor target expansion were both explained by recent commits/PRs, and that intent went into the docs.

**What to put in the docs.**
- Lead every file with purpose: what the module is *for* and what it publishes, before any detail.
- Document the non-obvious and the load-bearing: things an agent would get wrong without being told (ordinal-ordered enums used in comparisons, inline functions that must stay inline, mirror modules that must be edited in pairs, empty `.api` files that are expected, targets deliberately excluded and why).
- Include the failure modes of the build: commands that must run after certain changes (`apiDump` after public API changes), ordering requirements (publish to mavenLocal before building samples), and platform-specific CI paths.
- State editing *constraints*, not just descriptions: "preserve the recover-don't-crash behavior", "new `:kermit` API may need a non-inline mirror in `:kermit-simple`", "check the sibling module before and after editing".
- Comprehensive and concise are compatible: be selective about *what* to include (drop anything an agent can trivially discover or that doesn't change what it would do), but write what you keep in full sentences with real names — no shorthand the reader has to decode.

**Placement rules.**
- Repo-wide policy → root `CLAUDE.md` only.
- Module facts and constraints → that module's `CLAUDE.md`.
- Consumer/usage information → `AGENT-USAGE.md` (or the module README for humans).
- If a piece of guidance is both a consumer option and a maintainer constraint (like the kermit-core extension point), put the appropriate *facet* of it in each place rather than duplicating the whole thing.
