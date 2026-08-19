from pathlib import Path

root = Path("/home/ubuntu/Super-Nugget-Font-For-MCCRAFT/src/main/java")
replacements = {
    "net.minecraft.client.renderer.RenderType": "net.minecraft.client.renderer.rendertype.RenderType",
    "net.minecraft.world.item.ArmorMaterial": "net.minecraft.world.item.equipment.ArmorMaterial",
    "net.minecraft.world.entity.animal.Pig": "net.minecraft.world.entity.animal.pig.Pig",
    "net.minecraft.world.entity.animal.WaterAnimal": "net.minecraft.world.entity.animal.fish.WaterAnimal",
    "net.minecraft.world.entity.animal.horse.AbstractHorse": "net.minecraft.world.entity.animal.equine.AbstractHorse",
    "net.minecraft.world.entity.vehicle.Boat": "net.minecraft.world.entity.vehicle.boat.Boat",
    "net.minecraft.world.entity.vehicle.Minecart": "net.minecraft.world.entity.vehicle.minecart.Minecart",
    "net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents": "net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents",
    "net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext": "net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext",
    "net.minecraft.util.FastColor": "net.minecraft.util.ARGB",
    "FastColor.ARGB32.opaque": "ARGB.opaque",
}

changed = []
for path in root.rglob("*.java"):
    source = path.read_text(encoding="utf-8")
    updated = source
    for before, after in replacements.items():
        updated = updated.replace(before, after)
    if updated != source:
        path.write_text(updated, encoding="utf-8")
        changed.append(path.relative_to(root))

print(f"Updated {len(changed)} Java sources")
for path in changed:
    print(path)
