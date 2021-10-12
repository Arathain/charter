package net.arathain.charter;

import net.arathain.charter.item.ContractItem;
import net.arathain.charter.entity.CandleBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class Charter implements ModInitializer {
	public static String MODID = "charter";
	public static final Item CONTRACT = new ContractItem( new Item.Settings().maxCount(1).rarity(Rarity.RARE).group(ItemGroup.MISC));
	public static final BlockEntityType<CandleBlockEntity> CANDLE_ENTITY = FabricBlockEntityTypeBuilder.create(CandleBlockEntity::new, Blocks.CANDLE).build(null);

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier(MODID, "contract"), CONTRACT);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "contract"), CANDLE_ENTITY);
	}

	public static class DataTrackers {
		public static final TrackedData<Boolean> INDEBTED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	}
}
