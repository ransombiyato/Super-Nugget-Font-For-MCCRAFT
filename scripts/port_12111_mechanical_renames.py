from pathlib import Path

root = Path("/home/ubuntu/Super-Nugget-Font-For-MCCRAFT/src/main")
replacements = {
    "net.minecraft.resources.ResourceLocation": "net.minecraft.resources.Identifier",
    "ResourceLocationException": "IdentifierException",
    "ResourceLocationArgument": "IdentifierArgument",
    "ResourceLocation": "Identifier",
    "net.minecraft.Util": "net.minecraft.util.Util",
    "net.minecraft.BlockUtil": "net.minecraft.util.BlockUtil",
    "net.minecraft.FileUtil": "net.minecraft.util.FileUtil",
    "net.minecraft.advancements.critereon": "net.minecraft.advancements.criterion",
    "readResourceLocation": "readIdentifier",
    "writeResourceLocation": "writeIdentifier",
}

changed = []
for path in root.rglob("*.java"):
    text = path.read_text(encoding="utf-8")
    updated = text
    for before, after in replacements.items():
        updated = updated.replace(before, after)
    if updated != text:
        path.write_text(updated, encoding="utf-8")
        changed.append(path.relative_to(root))

print(f"Updated {len(changed)} Java sources")
for path in changed:
    print(path)
