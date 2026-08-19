from pathlib import Path

root = Path("/home/ubuntu/Super-Nugget-Font-For-MCCRAFT/src/main/java/vazkii/psi/common/spell/trick/potion")
replacements = {
    "MOVEMENT_SPEED": "SPEED",
    "DIG_SPEED": "HASTE",
    "DAMAGE_BOOST": "STRENGTH",
    "JUMP": "JUMP_BOOST",
    "DAMAGE_RESISTANCE": "RESISTANCE",
    "MOVEMENT_SLOWDOWN": "SLOWNESS",
}
for path in root.glob("PieceTrick*.java"):
    text = path.read_text(encoding="utf-8")
    updated = text
    for old, new in replacements.items():
        updated = updated.replace(f"MobEffects.{old}", f"MobEffects.{new}")
    if updated != text:
        path.write_text(updated, encoding="utf-8")
        print(f"Updated {path.name}")
