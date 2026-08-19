from pathlib import Path

root = Path("/home/ubuntu/Super-Nugget-Font-For-MCCRAFT")
src = root / "src/main/java"

def replace(path: str, old: str, new: str):
    file = src / path
    text = file.read_text(encoding="utf-8")
    if old not in text:
        raise SystemExit(f"expected source fragment not found in {path}")
    file.write_text(text.replace(old, new), encoding="utf-8")

replace("vazkii/psi/api/util/SimpleTier.java", """import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public record SimpleTier(TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float attackDamageBonus,
		int enchantmentValue, Supplier<Ingredient> repairIngredient) implements Tier {
	@Override
	public int getUses() {
		return uses;
	}

	@Override
	public float getSpeed() {
		return speed;
	}

	@Override
	public float getAttackDamageBonus() {
		return attackDamageBonus;
	}

	@Override
	public TagKey<Block> getIncorrectBlocksForDrops() {
		return incorrectBlocksForDrops;
	}

	@Override
	public int getEnchantmentValue() {
		return enchantmentValue;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return repairIngredient.get();
	}
}""", """import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

public final class SimpleTier {
	private SimpleTier() { }

	public static ToolMaterial create(TagKey<Block> incorrectBlocksForDrops, int durability, float speed,
			float attackDamageBonus, int enchantmentValue, TagKey<Item> repairItems) {
		return new ToolMaterial(incorrectBlocksForDrops, durability, speed, attackDamageBonus, enchantmentValue, repairItems);
	}
}""")

replace("vazkii/psi/api/PsiAPI.java", """import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;""", """import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;""")
replace("vazkii/psi/api/PsiAPI.java", """public static final Tier PSIMETAL_TOOL_MATERIAL = new SimpleTier(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 900, 7.8F, 2F, 12, () -> Ingredient.of(ModItems.psimetal.get()));""", """public static final TagKey<Item> PSIMETAL_REPAIR_ITEMS = TagKey.create(Registries.ITEM, PsiAPI.location("psimetal_repair_items"));
	public static final ToolMaterial PSIMETAL_TOOL_MATERIAL = SimpleTier.create(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 900, 7.8F, 2F, 12, PSIMETAL_REPAIR_ITEMS);""")

armor_material = """package vazkii.psi.api.material;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

import vazkii.psi.api.PsiAPI;

import java.util.EnumMap;

public final class PsimetalArmorMaterial {
	private PsimetalArmorMaterial() { }

	public static final ResourceKey<ArmorMaterial> PSIMETAL_ARMOR_KEY = ResourceKey.create(BuiltInRegistries.ARMOR_MATERIAL.key(), PsiAPI.location("psimetal"));
	public static final ArmorMaterial PSIMETAL_ARMOR_MATERIAL = new ArmorMaterial(
		18,
		Util.make(new EnumMap<>(ArmorType.class), map -> {
			map.put(ArmorType.BOOTS, 2);
			map.put(ArmorType.LEGGINGS, 5);
			map.put(ArmorType.CHESTPLATE, 6);
			map.put(ArmorType.HELMET, 2);
			map.put(ArmorType.BODY, 5);
		}),
		12,
		SoundEvents.ARMOR_EQUIP_IRON,
		0.0F,
		0.0F,
		PsiAPI.PSIMETAL_REPAIR_ITEMS,
		EquipmentAssets.IRON
	);

	static {
		Registry.register(BuiltInRegistries.ARMOR_MATERIAL, PsiAPI.location("psimetal"), PSIMETAL_ARMOR_MATERIAL);
	}
}
"""
(src / "vazkii/psi/api/material/PsimetalArmorMaterial.java").write_text(armor_material, encoding="utf-8")

armor_item = """package vazkii.psi.common.item.armor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.capability.ICapabilityProvider;
import vazkii.psi.api.exosuit.IPsiEventArmor;
import vazkii.psi.api.exosuit.PsiArmorEvent;
import vazkii.psi.api.internal.TooltipHelper;
import vazkii.psi.api.material.PsimetalArmorMaterial;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.core.handler.PlayerDataHandler.PlayerData;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.base.ModDataComponents;
import vazkii.psi.common.item.tool.IPsimetalTool;
import vazkii.psi.common.item.tool.ToolSocketable;

import java.util.List;

public class ItemPsimetalArmor extends Item implements IPsimetalTool, IPsiEventArmor {
	protected final ArmorType armorType;

	public ItemPsimetalArmor(ArmorType type, Properties props) {
		super(props.humanoidArmor(PsimetalArmorMaterial.PSIMETAL_ARMOR_MATERIAL, type)
			.component(ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY));
		this.armorType = type;
	}

	public void setDamage(ItemStack stack, int damage) {
		stack.setDamageValue(Math.min(damage, stack.getMaxDamage()));
	}

	@NotNull
	@Override
	public String getDescriptionId(@NotNull ItemStack stack) {
		String name = super.getDescriptionId(stack);
		return IPsimetalTool.isEnabled(stack) ? name : name + ".broken";
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
		IPsimetalTool.regen(stack, entity);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new ArmorSocketable(stack, 3);
	}

	public void cast(ItemStack stack, PsiArmorEvent event) {
		PlayerData data = PlayerDataHandler.get(event.getEntity());
		ItemStack playerCad = PsiAPI.getPlayerCAD(event.getEntity());
		if(IPsimetalTool.isEnabled(stack) && !playerCad.isEmpty()) {
			int timesCast = stack.getOrDefault(ModDataComponents.TIMES_CAST.get(), 0);
			ItemStack bullet = ISocketable.socketable(stack).getSelectedBullet();
			ItemCAD.cast(event.getEntity().getCommandSenderWorld(), event.getEntity(), data, bullet, playerCad, getCastCooldown(stack), 0, getCastVolume(), context -> {
				context.tool = stack;
				context.attackingEntity = event.attacker;
				context.damageTaken = event.damage;
				context.loopcastIndex = timesCast;
			}, (int) (data.calculateDamageDeduction((float) event.damage) * 0.75));
			stack.set(ModDataComponents.TIMES_CAST.get(), timesCast + 1);
		}
	}

	@Override
	public void onEvent(ItemStack stack, PsiArmorEvent event) { if(event.type.equals(getEvent(stack))) cast(stack, event); }
	public String getEvent(ItemStack stack) { return PsiArmorEvent.NONE; }
	public int getCastCooldown(ItemStack stack) { return 5; }
	public float getCastVolume() { return 0.025F; }

	@Environment(EnvType.CLIENT)
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
		TooltipHelper.tooltipIfShift(tooltip, () -> {
			tooltip.add(Component.translatable("psimisc.spell_selected", ISocketable.getSocketedItemName(stack, "psimisc.none")));
			tooltip.add(Component.translatable(getEvent(stack)));
		});
	}

	public boolean isRepairable(@NotNull ItemStack stack) { return true; }
	@Environment(EnvType.CLIENT)
	public int getColor(@NotNull ItemStack stack) { return ICADColorizer.DEFAULT_SPELL_COLOR; }

	public static class ArmorSocketable extends ToolSocketable {
		public ArmorSocketable(ItemStack tool, int slots) { super(tool, slots); }
		@Override public void setSelectedSlot(int slot) { super.setSelectedSlot(slot); tool.set(ModDataComponents.TIMES_CAST.get(), 0); }
		@Override public void setBulletInSocket(int slot, ItemStack bullet) { super.setBulletInSocket(slot, bullet); tool.set(ModDataComponents.TIMES_CAST.get(), 0); }
	}
}
"""
(src / "vazkii/psi/common/item/armor/ItemPsimetalArmor.java").write_text(armor_item, encoding="utf-8")

for name in ["ItemPsimetalExosuitBoots.java", "ItemPsimetalExosuitChestplate.java", "ItemPsimetalExosuitHelmet.java", "ItemPsimetalExosuitLeggings.java"]:
    path = src / "vazkii/psi/common/item/armor" / name
    text = path.read_text(encoding="utf-8").replace("import net.minecraft.world.item.ArmorItem;", "import net.minecraft.world.item.equipment.ArmorType;")
    text = text.replace("ArmorItem.Type", "ArmorType")
    path.write_text(text, encoding="utf-8")

mod_items = src / "vazkii/psi/common/item/base/ModItems.java"
text = mod_items.read_text(encoding="utf-8").replace("ArmorItem.Type", "ArmorType")
if "import net.minecraft.world.item.equipment.ArmorType;" not in text:
    text = text.replace("import net.minecraft.world.item.Item;", "import net.minecraft.world.item.Item;\nimport net.minecraft.world.item.equipment.ArmorType;")
mod_items.write_text(text, encoding="utf-8")

for name, old, new in [
    ("ItemPsimetalAxe.java", "super(PsiAPI.PSIMETAL_TOOL_MATERIAL, properties.attributes(AxeItem.createAttributes(PsiAPI.PSIMETAL_TOOL_MATERIAL, 5.0F, -3.0F)).component(ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY));", "super(PsiAPI.PSIMETAL_TOOL_MATERIAL, 5.0F, -3.0F, properties.component(ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY));"),
    ("ItemPsimetalShovel.java", "super(PsiAPI.PSIMETAL_TOOL_MATERIAL, properties.attributes(AxeItem.createAttributes(PsiAPI.PSIMETAL_TOOL_MATERIAL, 1.5F, -3.0F)).component(ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY));", "super(PsiAPI.PSIMETAL_TOOL_MATERIAL, 1.5F, -3.0F, properties.component(ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY));"),
]:
    replace(f"vazkii/psi/common/item/tool/{name}", old, new)

for name, old_ext, prop in [
    ("ItemPsimetalPickaxe.java", "PickaxeItem", "pickaxe(PsiAPI.PSIMETAL_TOOL_MATERIAL, 1, -2.8F)"),
    ("ItemPsimetalSword.java", "SwordItem", "sword(PsiAPI.PSIMETAL_TOOL_MATERIAL, 3, -2.4F)"),
]:
    path = src / "vazkii/psi/common/item/tool" / name
    text = path.read_text(encoding="utf-8").replace(f"extends {old_ext}", "extends Item")
    old_ctor = "super(PsiAPI.PSIMETAL_TOOL_MATERIAL, properties.attributes(AxeItem.createAttributes(PsiAPI.PSIMETAL_TOOL_MATERIAL, 1, -2.8F)).component(ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY));" if "Pickaxe" in name else "super(PsiAPI.PSIMETAL_TOOL_MATERIAL, properties.attributes(AxeItem.createAttributes(PsiAPI.PSIMETAL_TOOL_MATERIAL, 3, -2.4F)).component(ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY));"
    new_ctor = f"super(properties.{prop}.component(ModDataComponents.BULLETS.get(), ItemContainerContents.EMPTY));"
    if old_ctor not in text: raise SystemExit(f"constructor not found in {name}")
    path.write_text(text.replace(old_ctor, new_ctor), encoding="utf-8")

container = src / "vazkii/psi/common/block/tile/container/ContainerCADAssembler.java"
text = container.read_text(encoding="utf-8").replace("import net.minecraft.world.item.ArmorItem;\n", "")
old_branch = """} else if(stackInSlot.getItem() instanceof ArmorItem armor) {
					int armorSlot = armorStart + armor.getType().ordinal() - 1;
					if(!moveItemStackTo(stackInSlot, armorSlot, armorSlot + 1, true) &&
							!moveItemStackTo(stackInSlot, playerStart, hotbarEnd, true)) {
						return ItemStack.EMPTY;
					}
				} else if(!moveItemStackTo(stackInSlot, playerStart, hotbarEnd, true)) {"""
new_branch = """} else if(!moveItemStackTo(stackInSlot, playerStart, hotbarEnd, true)) {"""
if old_branch not in text: raise SystemExit("armor transfer branch not found")
container.write_text(text.replace(old_branch, new_branch), encoding="utf-8")
repair_tag = root / "src/main/resources/data/psi/tags/item/psimetal_repair_items.json"
repair_tag.parent.mkdir(parents=True, exist_ok=True)
repair_tag.write_text('{\n  "replace": false,\n  "values": ["psi:psimetal"]\n}\n', encoding="utf-8")

print("Migrated tool material and component-based armor sources")
