package net.arathain.charter.item;

import net.arathain.charter.Charter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Optional;

public class EternalSealItem extends Item {
    public EternalSealItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;

            for (ItemStack itemStack : player.getInventory().main) {
                if(!itemStack.isEmpty() && ContractItem.isViable(itemStack)) {
                     if(world.getPlayerByUuid(ContractItem.getIndebtedUUID(itemStack)) != null && world.getPlayerByUuid(ContractItem.getIndebtedUUID(itemStack)) != player) {
                         PlayerEntity indebted = world.getPlayerByUuid(ContractItem.getIndebtedUUID(itemStack));
                         assert indebted != null;
                         indebted.addStatusEffect(new StatusEffectInstance(Charter.ETERNAL_DEBT, 1600, 0));
                     }
                }
            }

        }
    }
}
