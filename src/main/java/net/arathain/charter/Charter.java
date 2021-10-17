package net.arathain.charter;

import net.arathain.charter.item.ContractItem;
import net.arathain.charter.item.EternalSealItem;
import net.arathain.charter.item.MerchantCrestItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class Charter implements ModInitializer {
	public static String MODID = "charter";
	public static final Item CONTRACT = new ContractItem( new Item.Settings().maxCount(1).rarity(Rarity.RARE).group(ItemGroup.MISC));
	public static final Item MERCHANT_CREST = new MerchantCrestItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).group(ItemGroup.MISC));
	public static final Item ETERNAL_SEAL = new EternalSealItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).group(ItemGroup.MISC));
	public static final StatusEffect ETERNAL_DEBT = new EternalDebtStatusEffect(StatusEffectType.NEUTRAL, 0x4bf1f7);
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier(MODID, "contract"), CONTRACT);
		Registry.register(Registry.ITEM, new Identifier(MODID, "merchant_crest"), MERCHANT_CREST);
		Registry.register(Registry.ITEM, new Identifier(MODID, "eternal_seal"), ETERNAL_SEAL);
		Registry.register(Registry.STATUS_EFFECT, new Identifier(MODID, "eternal_debt"), ETERNAL_DEBT);
	}

	public static class DataTrackers {
		public static final TrackedData<Boolean> INDEBTED = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	}
}
