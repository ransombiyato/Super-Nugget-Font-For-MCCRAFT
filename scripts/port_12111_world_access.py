from pathlib import Path
import re

root = Path("/home/ubuntu/Super-Nugget-Font-For-MCCRAFT/src/main/java")
changed = []

for path in root.rglob("*.java"):
    text = path.read_text(encoding="utf-8")
    updated = re.sub(r"\.isClientSide\b(?!\s*\()", ".isClientSide()", text)
    updated = updated.replace(".getCommandSenderWorld()", ".level()")
    if updated != text:
        path.write_text(updated, encoding="utf-8")
        changed.append(path.relative_to(root).as_posix())

print(f"Migrated world access in {len(changed)} source files")
for path in changed:
    print(path)
