# Orchestration Dashboard Design

## Purpose

Future visual dashboard for `sisters.asslorde.com/orchestration-status` showing real-time RSMod content automation status.

## Design (Not Yet Implemented)

### Layout

```
+--------------------------------------------------+
|  RSMod Content Orchestration Dashboard            |
|  Updated: <date>                                  |
+--------------------------------------------------+
| [Latest Commit]  | [Latest Worklog]               |
| abc1234 feat...  | 2026-06-20: Ice Caves batch   |
+--------------------------------------------------+
|  Confidence Levels                                |
|  Drops: L5  Skills: ready  Shops: L1  Minigames: |
|  [████████] [████████] [██░░░░░░] [░░░░░░░░]    |
+--------------------------------------------------+
|  Zone Readiness                                   |
|  Lumbridge  [🏆]  Draynor    [🟡]  Varrock    [🟡] |
|  Al Kharid  [🟡]  Edgeville  [🟡]  Falador    [🟡] |
|  Port Sarim [🟡]  Ice Caves  [🟡]               |
+--------------------------------------------------+
|  Generator Status                                 |
|  G2 Drops    [Level 5]  G4 Shops     [Level 1]   |
|  G1 Zones    [Not started]  G3 Dialogue [Not yet] |
+--------------------------------------------------+
|  Kanban Queue                                     |
|  [Task]  [Priority]  [Assignee]  [Status]         |
+--------------------------------------------------+
|  Next Recommended Task                             |
|  <from latest worklog>                            |
+--------------------------------------------------+
```

### Data Sources

| Panel | Source |
|-------|--------|
| Latest commit | `git log -1` on CT 175 |
| Latest worklog | `docs/worklog/YYYY-MM/` via file listing |
| Confidence levels | `docs/roadmap/system-status-matrix.md` |
| Zone readiness | `docs/automation/zone-readiness-checklist.md` |
| Generator status | `docs/roadmap/system-status-matrix.md` |
| Kanban queue | Kanban board DB |
| Next task | Latest worklog entry |

### Implementation Notes

- Static page, regenerated on commit or via cron
- Python script reads markdown docs, renders to HTML
- No backend needed — pure file-to-HTML generation
- Deploy via Caddy on homelab
- Build only after orchestration system is stable
