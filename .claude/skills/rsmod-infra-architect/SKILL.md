---
name: rsmod-infra-architect
description: Specialist in RSMod v2 infrastructure, Rev 233 revision alignment, Gradle build systems, and server-side setup (RSA, networking, MCP integration).
---

# RSMod Infra Architect Skill

## Task Coordination — Do This First

Multiple agents (Claude, OpenCode, Kimi, Codex) work this codebase simultaneously.
Before writing a single line of code, coordinate via the `agent-tasks` MCP server:

1. `list_tasks({ status: "pending" })` — find available work
2. `get_task("TASK-ID")` — read the full task description
3. `claim_task("TASK-ID", "your-agent-name")` — atomically claim it (fails if taken)
4. `check_conflicts(["path/file"])` — verify no one else is editing your files
5. `lock_file("path/file", "your-agent-name", "TASK-ID")` — lock every file before editing
6. Implement.
7. `complete_task("TASK-ID", "your-agent-name", "what I built")` — releases locks, marks done

If blocked: `block_task("TASK-ID", "your-agent-name", "exact reason")` so others know.
See `START_HERE.md` for the full project orientation.

Use this skill to configure, optimize, and troubleshoot the core RSMod v2 environment targeting Revision 233.

## Infrastructure Checklist (Rev 233)

1.  **RSA Key Generation**: Essential for client-banker handshake.
    -   Command: `cd rsmod && gradlew.bat generateRsa`
2.  **Cache Alignment**: Ensure `rsmod/.data/cache` matches Revision 233 symbols.
3.  **Port Configuration**:
    -   Game Server: `43594`
    -   AgentBridge (MCP): `43595`
4.  **Gradle Optimization**:
    -   Use `--daemon` for persistent workers.
    -   Use `gradlew :module:build` for targeted compilation.

## System Infrastructure Setup Workflow

### Rev 233 Opcode Alignment
If packets are being misread:
1.  Open `rsmod/engine/protocol/`
2.  Cross-reference with `rsinf_233` (Deobfuscated client) `ClientGameProt` and `ServerGameProt`.
3.  Update the `PacketDecoder` or `PacketEncoder` in RSMod to match.

### MCP Integration
Ensure the AgentBridge is active in `content/other/agent-bridge`.
-   Verify WebSocket connectivity on port 43595.
-   Check `mcp/.mcp.json` for auto-discovery settings.

## Troubleshooting Guide

-   **RSA Exception**: Run `generateRsa`. Check `game.key` presence in `rsmod/`.
-   **Packet Drop/Timeout**: Use `rsprox` to sniff the connection. Check if it's a protocol mismatch or a firewall issue.
-   **Build Failures**: Run `gradlew build --scan` (if available) or `diagnose.bat` to check JDK/Environment compatibility.

## Reference Files
-   `AGENTS.md`: Ownership and blocker list.
-   `scripts/diagnose.bat`: Environment health check.
-   `scripts/start-server.bat`: Standard startup sequence.
