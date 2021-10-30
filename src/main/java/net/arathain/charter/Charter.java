package net.arathain.charter;

import net.arathain.charter.block.PactPressBlock;
import net.arathain.charter.block.entity.PactPressBlockEntity;
import net.arathain.charter.item.ContractItem;
import net.arathain.charter.item.EternalSealItem;
import net.arathain.charter.item.MerchantCrestItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.util.Optional;
import java.util.UUID;

public class Charter implements ModInitializer {
	public static String MODID = "charter";
	public static final Item CONTRACT = new ContractItem( new Item.Settings().maxCount(1).rarity(Rarity.RARE).group(ItemGroup.COMBAT));
	public static final Item MERCHANT_CREST = new MerchantCrestItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).group(ItemGroup.COMBAT));
	public static final Item ETERNAL_SEAL = new EternalSealItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).group(ItemGroup.COMBAT));
	public static final StatusEffect ETERNAL_DEBT = new CharterStatusEffect(StatusEffectType.NEUTRAL, 0x4bf1f7);
	public static final Block PACT_PRESS = new PactPressBlock(FabricBlockSettings.copyOf(Blocks.CHISELED_DEEPSLATE).ticksRandomly());
	public static BlockEntityType<PactPressBlockEntity> PACT_PRESS_ENTITY;
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier(MODID, "contract"), CONTRACT);
		Registry.register(Registry.ITEM, new Identifier(MODID, "merchant_crest"), MERCHANT_CREST);
		Registry.register(Registry.ITEM, new Identifier(MODID, "eternal_seal"), ETERNAL_SEAL);
		Registry.register(Registry.ITEM, new Identifier(MODID, "pact_press"), new BlockItem(PACT_PRESS, new FabricItemSettings().group(ItemGroup.COMBAT)));
		Registry.register(Registry.BLOCK, new Identifier(MODID, "pact_press"), PACT_PRESS);
		PACT_PRESS_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "pact_press"), FabricBlockEntityTypeBuilder.create(PactPressBlockEntity::new, PACT_PRESS).build(null));
		Registry.register(Registry.STATUS_EFFECT, new Identifier(MODID, "eternal_debt"), ETERNAL_DEBT);
		LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, identifier, fabricLootSupplierBuilder, lootTableSetter) -> {
			Identifier bastion_treasure = new Identifier(MODID, "inject/bastion_treasure");
			if (LootTables.BASTION_TREASURE_CHEST.equals(identifier)) {
				fabricLootSupplierBuilder.withPool(LootPool.builder().with(LootTableEntry.builder(bastion_treasure).weight(2)).build());
			}
		});
	}
}
