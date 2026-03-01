# DOCS — Project Documentation

## OVERVIEW
Project documentation directory. Shared ownership — coordinate before rewriting.

## KEY DOCUMENTS

| File | Purpose | Owner |
|------|---------|-------|
| CONTENT_AUDIT.md | Skill status, module completion tracking | All |
| REV_LOCK_POLICY.md | Rev 233 lock rules and migration control | All |
| TRANSLATION_CHEATSHEET.md | Alter v1 → RSMod v2 API mapping | All |
| NEXT_STEPS.md | Current priorities, workflow | All |
| LLM_TESTING_GUIDE.md | AgentBridge state schema, bot testing | Claude |
| RUNESERVER_NOTES.md | RuneServer best practices (fill in) | Kimi |
| WORK_PLAN.md | Active work planning | All |

## CONVENTIONS
- Update CONTENT_AUDIT.md when a skill completes
- Check module ownership in root AGENTS.md before editing
- Use wiki-data/ for data tables, not inline in docs
- Research findings → docs/agent-notes/<agent>.md
- Session handoffs → docs/handoffs/ or root /handoff
- Use one git branch per task (`agent/<name>/<task-id>-<slug>`) and include branch+commit in task completion notes

## DIRECTORY STRUCTURE
```
docs/
├── agent-notes/    # Per-agent session notes
├── handoffs/       # Session handoff summaries
├── *-GUIDE.md      # How-to documentation
├── *-ROADMAP.md    # Planning documents
└── *-_NOTES.md     # Research findings
```

## ANTI-PATTERNS
- Edit without checking ownership table
- Delete or move core docs (CONTENT_AUDIT, TRANSLATION_CHEATSHEET)
- Leave stale status in CONTENT_AUDIT.md
- Duplicate wiki data inline instead of using wiki-data/

