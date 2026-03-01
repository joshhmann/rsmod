# Playbook Audit — 2026-02-26

Purpose: remove high-risk documentation conflicts that caused symbol/tooling errors and establish one definitive execution playbook.

---

## Scope Audited

- `README.md`
- `AGENTS.md`
- `START_HERE.md`
- `CLAUDE.md`
- `GEMINI.md`
- `docs/DOC_AUTHORITY.md`
- `docs/NEXT_STEPS.md`
- `docs/TRANSLATION_CHEATSHEET.md`
- `docs/MICRO_TASK_GUIDE.md`
- `docs/NPC_DATA_METHODS.md`
- MCP docs with high operator visibility:
  - `docs/MCP_GUIDE.md`
  - `docs/MCP_IDE_CONFIGURATION.md`
  - `docs/MCP_DEPLOYMENT.md`
  - `MCP_QUICKSTART.md`
  - `DEPLOYMENT_SUMMARY.md`

---

## Critical Conflicts Found

1. Authority drift:
- Multiple docs had different "read first" and truth-order guidance.

2. Symbol-risk examples:
- Active docs still showed `find("name", hash)` style examples, which directly contributed to hash/symbol mismatch behavior.

3. Tool policy drift:
- Active implementation docs still suggested `get_npc_rev233`/`get_item_rev233` workflows despite current execution policy to not block on `osrs-wiki-rev233`.

4. Legacy doc naming drift:
- References to `LEGACY_PLAYBOOK.md` remained in active paths even though `LEGACY_IMPLEMENTATION_PLAYBOOK.md` is the current doc.

---

## Changes Applied

1. Definitive playbook consolidation:
- `README.md` set as definitive operational playbook with explicit precedence and one-line delegation prompt.
- `AGENTS.md`, `START_HERE.md`, `CLAUDE.md`, `GEMINI.md`, and `docs/DOC_AUTHORITY.md` aligned to README-first operation.

2. Symbol-safety corrections:
- Removed active examples using hash fallbacks in `START_HERE.md` and replaced with `find("name")` patterns.
- Kept fallback-ID examples only in explicit WRONG/TRAP sections.

3. Data source policy alignment:
- Updated active implementation docs to prefer `osrs-cache` + `.sym` verification.
- Replaced active `get_npc_rev233` usage in:
  - `docs/TRANSLATION_CHEATSHEET.md`
  - `docs/MICRO_TASK_GUIDE.md`
  - `docs/NPC_DATA_METHODS.md`
  - `START_HERE.md` data lookup sections

4. Legacy filename alignment:
- Replaced active references to `LEGACY_PLAYBOOK.md` with `LEGACY_IMPLEMENTATION_PLAYBOOK.md` where applicable.

5. MCP docs disambiguation:
- Added operational-policy notes to MCP setup/deployment docs so installability is not mistaken for task-execution priority.

---

## Residual Mentions (Intentional)

These remain by design and are not execution blockers:

1. `docs/MCP_GUIDE.md` mentions `get_npc_rev233*` and related aliases as server capability documentation.
2. `docs/DOC_AUTHORITY.md` references `LEGACY_PLAYBOOK.md` only as an archived/superseded file.
3. `AGENTS.md` mentions `get_npc_rev233*` only in the context of "do not block on availability."

---

## Operational Result

After this audit:
- Agents can be instructed to read `README.md` and start with consistent behavior.
- Symbol-related doc traps in active onboarding/implementation flow were removed.
- Kotlin tooling and build gate flow are consistently defined across primary docs.

