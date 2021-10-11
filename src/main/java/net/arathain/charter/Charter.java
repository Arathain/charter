package net.arathain.charter;

import net.arathain.charter.item.ContractItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Charter implements ModInitializer {
	public static String MODID = "charter";
	public static final Item CONTRACT = new ContractItem( new Item.Settings().maxCount(1).rarity(Rarity.RARE).group(ItemGroup.MISC));

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier(MODID, "contract"), CONTRACT);
	}
}
