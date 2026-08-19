from pathlib import Path
import re

root = Path("/home/ubuntu/Super-Nugget-Font-For-MCCRAFT/src/main/java/vazkii/psi/common/crafting/recipe")
targets = [
    "AssemblyScavengeRecipe.java",
    "BulletToDriveRecipe.java",
    "ColorizerChangeRecipe.java",
    "DriveDuplicateRecipe.java",
    "SensorAttachRecipe.java",
    "SensorRemoveRecipe.java",
]

for name in targets:
    path = root / name
    text = path.read_text(encoding="utf-8")
    updated = re.sub(
        r"\n\t@Override\n\tpublic @NotNull RecipeType<\?> getType\(\) \{.*?\n\t}\n",
        "\n",
        text,
        flags=re.DOTALL,
    )
    updated = updated.replace("public RecipeSerializer<?> getSerializer()", "public RecipeSerializer<? extends CustomRecipe> getSerializer()")
    updated = updated.replace("public @NotNull RecipeSerializer<?> getSerializer()", "public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer()")
    updated = re.sub(r"\n\t@Override\n(\tpublic boolean canCraftInDimensions\()", r"\n\1", updated)
    if updated != text:
        path.write_text(updated, encoding="utf-8")
        print(f"Updated {name}")
