from pathlib import Path
import re

root = Path("/home/ubuntu/Super-Nugget-Font-For-MCCRAFT/src/main/java/vazkii/psi")
items = [
    "common/item/ItemCAD.java",
    "common/item/ItemDetonator.java",
    "common/item/ItemExosuitController.java",
    "common/item/ItemFlashRing.java",
    "common/item/ItemSpellBullet.java",
    "common/item/ItemSpellDrive.java",
]

for relative in items:
    path = root / relative
    text = path.read_text(encoding="utf-8")
    if "InteractionResultHolder" not in text:
        raise SystemExit(f"legacy result API not present in {relative}")
    text = text.replace("import net.minecraft.world.InteractionResultHolder;\n", "")
    text = text.replace("InteractionResultHolder<ItemStack> use(", "InteractionResult use(")
    text = re.sub(
        r"new InteractionResultHolder<>\(([^\n]+?),\s*(?:itemStackIn|held)\)",
        r"\1",
        text,
    )
    if "InteractionResultHolder" in text:
        raise SystemExit(f"incomplete migration in {relative}")
    path.write_text(text, encoding="utf-8")

programmer = root / "common/block/BlockProgrammer.java"
text = programmer.read_text(encoding="utf-8")
if "ItemInteractionResult" not in text:
    raise SystemExit("legacy programmer block result API not present")
text = text.replace("import net.minecraft.world.ItemInteractionResult;\n", "")
text = text.replace("ItemInteractionResult useItemOn(", "InteractionResult useItemOn(")
text = text.replace("ItemInteractionResult.SUCCESS", "InteractionResult.SUCCESS")
programmer.write_text(text, encoding="utf-8")

print("Migrated 6 item use methods and programmer block useItemOn result type")
