from pathlib import Path
from zipfile import ZipFile
import re

project = Path("/home/ubuntu/Super-Nugget-Font-For-MCCRAFT")
jar = Path("/home/ubuntu/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged/1.21.11-loom.mappings.1_21_11.layered+hash.2198-v2/minecraft-merged-1.21.11-loom.mappings.1_21_11.layered+hash.2198-v2.jar")
imports = re.compile(r"^import\s+(net\.minecraft\.[\w.]+);", re.MULTILINE)

with ZipFile(jar) as archive:
    entries = {name[:-6].replace("/", ".") for name in archive.namelist() if name.endswith(".class")}

missing = {}
for source in (project / "src/main/java").rglob("*.java"):
    for imported in imports.findall(source.read_text(encoding="utf-8")):
        if imported not in entries:
            simple = imported.rsplit(".", 1)[-1]
            candidates = sorted(name for name in entries if name.rsplit(".", 1)[-1] == simple)
            missing.setdefault(imported, {"sources": [], "candidates": candidates})["sources"].append(str(source.relative_to(project)))

for imported, data in sorted(missing.items()):
    print(f"MISSING {imported}")
    print("  candidates: " + (", ".join(data["candidates"][:8]) if data["candidates"] else "<none>"))
    print("  used-by: " + ", ".join(data["sources"][:5]) + (" …" if len(data["sources"]) > 5 else ""))
