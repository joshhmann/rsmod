# Regression Policy

## Prevention Rules
1. Stage before promote
2. Compare before replace
3. Compile gate - every change must compile
4. Raw-ID gate - every change must pass scan
5. Doc gate - every batch updates status
6. Skip durability - every skip recorded in registry

## Recovery
If regression found: revert -> document -> update workflow -> retest
