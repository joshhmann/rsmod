# Doc Authority Matrix

Last updated: 2026-02-26.

This file defines which docs are authoritative for execution. If docs conflict, follow this file plus `AGENTS.md` truth-order rules.
Revision source anchor: OpenRS2 `runescape/2293` (build major `233`, built `2025-09-10T16:47:47Z`).

## Global Execution Truth

1. `agent-tasks` registry (task status, ownership, blockers)
2. `README.md` (definitive operational playbook)
3. `docs/AGENTS.md` (ownership tables, blockers, role scopes, DoD)
4. `docs/NEXT_STEPS.md` (active sequencing)
5. `docs/CONTENT_AUDIT.md` (feature status)
6. `docs/MASTER_ROADMAP.md` (long-horizon scope only)

## Agent-Scoped Primary Docs

### All Agents
- `README.md`
- `docs/AGENTS.md` (canonical)
- `AGENTS.md` (shim that points to canonical)
- `docs/COORDINATOR_SOP.md` (required for coordinators/delegators)
- `docs/DOC_AUTHORITY.md` (this file)
- `docs/NEXT_STEPS.md`
- `docs/CONTENT_AUDIT.md`
- `docs/REV_LOCK_POLICY.md`
- `docs/CODEBASE_AUDIT_2026-02-26.md` (known issues, doc gaps, agent failure patterns)

### Claude (Content + Testing)
- `CLAUDE.md`
- `docs/TRANSLATION_CHEATSHEET.md`
- `docs/CORE_SYSTEMS_GUIDE.md`
- `docs/LEGACY_IMPLEMENTATION_PLAYBOOK.md`
- `docs/LLM_TESTING_GUIDE.md`
- `docs/SYM_NAMING_GUIDE.md`
- `docs/OSRS_MECHANICS_REFERENCE.md`

### Gemini (Engine + API + Infra)
- `GEMINI.md` (also at `rsmod/GEMINI.md`)
- `docs/CORE_SYSTEMS_GUIDE.md`
- `docs/REV_LOCK_POLICY.md`
- `docs/TRANSLATION_CHEATSHEET.md` (when API changes affect content agents)

### Kimi / OpenCode (Research + Full-Stack Content)
- `docs/RUNESERVER_NOTES.md`
- `docs/NPC_DATA_TOOLS.md`
- `docs/MCP_OSRS_SETUP.md`
- `wiki-data/` schemas and existing files
- `rsmod/.data/symbols/` (rev 233 symbol truth)

---

## Tier 2 — Useful Reference Docs (Consult As Needed)

These are accurate and useful but not required reading before every session.

| Doc | Purpose |
|-----|---------|
| `docs/MASTER_ROADMAP.md` | Full Rev 233 parity feature list |
| `docs/OSRS_MECHANICS_REFERENCE.md` | Wiki-accurate formulas (combat, prayer, run energy, aggression) |
| `docs/SYM_NAMING_GUIDE.md` | 80+ sym name quirks (obj.sym focus) |
| `docs/MCP_OSRS_SETUP.md` | MCP osrs-cache tool reference |
| `docs/NPC_DATA_TOOLS.md` | Python tools for NPC data extraction |
| `docs/RUNESERVER_NOTES.md` | RuneServer research (combat formulas, ticks) |
| `docs/DTX_DROP_TABLE_GUIDE.md` | Drop table DSL reference |
| `docs/QUESTING_AREA_GUIDELINES.md` | Quest/area implementation patterns |

---

## Testing Docs Priority

1. `docs/LLM_TESTING_GUIDE.md` (primary)
2. `docs/testing/QUICKSTART.md` (fast procedure)
3. `docs/testing/MASTER_QA_PLAYBOOK.md` (full QA workflow)

## NPC Drops Docs Priority

1. `AGENTS.md` section: `NPC Drops File Ownership (Required)`
2. `rsmod/content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcdrops/tables/`
3. Legacy DTX file is reference only:
   `rsmod/content/other/npc-drops/src/main/kotlin/org/rsmod/content/other/npcrops/scripts/F2pDropTables.kt`

---

## Reference-Only / Historical (Do Not Use As Execution Truth)

Already in `docs/archive/`:
- `REV233_COMPLETION_ROADMAP.md`
- `MASTER_IMPLEMENTATION_ROADMAP.md`
- `WORK_PLAN.md`
- `F2P_PLAN.md`
- `F2P_WORK_PLAN.md`
- `HANDOFF_TO_GEMINI.md`
- `LEGACY_PLAYBOOK.md` (superseded by `LEGACY_IMPLEMENTATION_PLAYBOOK.md`)

### Archive Candidates (Still in `docs/` — Should Be Moved)

These files are one-off research, outdated analysis, or superseded content. They should NOT be consulted for current execution decisions. Move to `docs/archive/` when convenient.

| File | Reason |
|------|--------|
| `AGENTS.md` (project root) | Shim only — canonical policy lives at `docs/AGENTS.md` |
| `AGENTBRIDGE_IRONMAN.md` | One-off research |
| `AGENTBRIDGE_PORCELAIN.md` | One-off research |
| `BULK_SCRAPER_GUIDE.md` | Tool-specific guide, not execution |
| `CACHE_REVISION_INVESTIGATION.md` | Historical investigation |
| `CACHE_SOURCE_FOUND.md` | Historical finding |
| `CACHE_SYMBOL_QUICKREF.md` | Superseded by SYM_NAMING_GUIDE.md |
| `COMPLETE_CONTENT_DEPENDENCY_MAP.md` | Superseded by CONTENT_AUDIT + agent-tasks |
| `CORE_SYSTEMS_AUDIT.md` | Superseded by CORE_SYSTEMS_GUIDE.md |
| `EMERGENCY_CACHE_FIX.md` | One-off fix record |
| `F2P_CONTENT_AUDIT.md` | Superseded by CONTENT_AUDIT.md |
| `F2P_CONTENT_COMPLETENESS_GUIDE.md` | Superseded by CONTENT_AUDIT.md |
| `F2P_CRITICAL_PATH.md` | Superseded by NEXT_STEPS.md |
| `F2P_FLAX_RESEARCH.md` | One-off research |
| `F2P_RSMOD_IMPLEMENTATION_PLAN.md` | Superseded by NEXT_STEPS + MASTER_ROADMAP |
| `F2P_TASK_REGISTRY_SUMMARY.md` | Superseded by agent-tasks registry |
| `FEATURE_COMPLETENESS_MATRIX.md` | Superseded by CONTENT_AUDIT.md |
| `IMPLEMENTATION_BACKLOG.md` | Superseded by agent-tasks registry |
| `IMPLEMENTATION_PLAN_TEMPLATE.md` | Template — low reference value |
| `MCP_DEPLOYMENT.md` | One-off setup guide |
| `MCP_GUIDE.md` | Superseded by MCP_OSRS_SETUP.md |
| `MCP_IDE_CONFIGURATION.md` | One-off setup |
| `MICRO_TASK_GUIDE.md` | Historical process doc |
| `NPC_DATA_METHODS.md` | Superseded by NPC_DATA_TOOLS.md |
| `NPC_DATA_PIPELINE_PROPOSAL.md` | Historical proposal |
| `OPENRUNE_RSMOD_CODE_COMPARISON.md` | One-off analysis |
| `OPENRUNE_VS_RSMOD_ARCHITECTURE.md` | One-off analysis |
| `OSRS_PACKET_REFERENCE.md` | Reference — keep if useful for Gemini |
| `PLAYBOOK_AUDIT_2026-02-26.md` | Superseded by CODEBASE_AUDIT_2026-02-26.md |
| `REV233_DATA_GUIDE.md` | Superseded by SYM_NAMING_GUIDE + MCP_OSRS_SETUP |
| `REV233_DATE_CLARIFICATION.md` | One-off clarification |
| `REVISION_DIFFERENCES_233_vs_236.md` | Historical comparison |
| `RSMOD_ACTUAL_STATE_ANALYSIS.md` | Historical analysis |
| `RSMOD_MODULE_STRUCTURE_GUIDE.md` | Superseded by START_HERE.md module section |
| `RSMOD_REVISION_233_ANALYSIS_SYNTHESIS.md` | Historical analysis |
| `RSMOD_VS_ALTER_PATTERN_ANALYSIS.md` | Superseded by TRANSLATION_CHEATSHEET.md |
| `RUNE_SERVER_RESOURCES.md` | Link collection — low value |
| `SKILL_DEPENDENCY_RESEARCH.md` | One-off research |
| `SYMBOL_SYSTEM_EXPLAINED.md` | Superseded by SYM_NAMING_GUIDE.md |
| `TEST_PROMPT_CODEX.md` | One-off test prompt |
| `TEST_PROMPT_GEMINI.md` | One-off test prompt |
| `TOOL_IMPROVEMENTS.md` | Historical proposal |
| `TOOL_VERIFICATION_TASK.md` | One-off task |
| `TRUE_OSRS_EMULATION.md` | Historical philosophy doc |
| `UB3R_REV201_PORTING_MATRIX.md` | Historical analysis |
| `WHAT_YOU_CAN_BUILD.md` | Superseded by CONTENT_AUDIT + MASTER_ROADMAP |
| `WIKI_SCRAPER_REV233_GUIDE.md` | Tool-specific guide |
| `agent-pings.md` | Operational log — low reference value |
