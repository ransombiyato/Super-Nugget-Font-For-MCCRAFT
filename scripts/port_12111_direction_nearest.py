from pathlib import Path

replacements = {
    "src/main/java/vazkii/psi/common/spell/operator/entity/PieceOperatorEntityAxialLook.java": [
        ("Direction.getNearest((float) look.x, (float) look.y, (float) look.z)", "Direction.getNearest((int) Math.signum(look.x), (int) Math.signum(look.y), (int) Math.signum(look.z), Direction.NORTH)"),
    ],
    "src/main/java/vazkii/psi/common/spell/trick/block/PieceTrickPlaceBlock.java": [
        ("Direction.getNearest(directionVal.x, directionVal.y, directionVal.z)", "Direction.getNearest((int) Math.signum(directionVal.x), (int) Math.signum(directionVal.y), (int) Math.signum(directionVal.z), Direction.NORTH)"),
        ("Direction.getNearest(directionVal.x, 0.0, directionVal.z)", "Direction.getNearest((int) Math.signum(directionVal.x), 0, (int) Math.signum(directionVal.z), Direction.NORTH)"),
    ],
    "src/main/java/vazkii/psi/common/spell/trick/block/PieceTrickPlaceInSequence.java": [
        ("Direction.getNearest(directionVal.x, directionVal.y, directionVal.z)", "Direction.getNearest((int) Math.signum(directionVal.x), (int) Math.signum(directionVal.y), (int) Math.signum(directionVal.z), Direction.NORTH)"),
        ("Direction.getNearest(directionVal.x, 0.0, directionVal.z)", "Direction.getNearest((int) Math.signum(directionVal.x), 0, (int) Math.signum(directionVal.z), Direction.NORTH)"),
    ],
}

root = Path("/home/ubuntu/Super-Nugget-Font-For-MCCRAFT")
for relative, pairs in replacements.items():
    path = root / relative
    text = path.read_text(encoding="utf-8")
    updated = text
    for old, new in pairs:
        updated = updated.replace(old, new)
    if updated != text:
        path.write_text(updated, encoding="utf-8")
        print(f"Updated {relative}")
