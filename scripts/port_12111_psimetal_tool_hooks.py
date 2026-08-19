from pathlib import Path

root = Path("/home/ubuntu/Super-Nugget-Font-For-MCCRAFT/src/main/java/vazkii/psi/common/item/tool")
for name in ["ItemPsimetalAxe.java", "ItemPsimetalPickaxe.java", "ItemPsimetalShovel.java"]:
    path = root / name
    text = path.read_text(encoding="utf-8")
    text = text.replace("\t@NotNull\n\t@Override\n\tpublic String getDescriptionId(@NotNull ItemStack stack)", "\t@NotNull\n\tpublic String getDescriptionId(@NotNull ItemStack stack)")
    text = text.replace("super.getDescriptionId(stack)", "super.getDescriptionId()")
    text = text.replace("\n\t@Override\n\tpublic void inventoryTick", "\n\tpublic void inventoryTick")
    text = text.replace("\n\t@Environment(EnvType.CLIENT)\n\t@Override\n\tpublic void appendHoverText", "\n\t@Environment(EnvType.CLIENT)\n\tpublic void appendHoverText")
    text = text.replace("\n\t@Nullable\n\t@Override\n\tpublic ICapabilityProvider initCapabilities", "\n\t@Nullable\n\tpublic ICapabilityProvider initCapabilities")
    path.write_text(text, encoding="utf-8")
    print(f"Updated {name}")
