package net.arathain.charter.item;

import net.arathain.charter.Charter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EternalSealItem extends Item {
    public EternalSealItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;

            for(int i = 0; i < player.getInventory().size(); i++)
            {
                ItemStack stack2 = player.getInventory().getStack(i);
                if(!stack2.isEmpty() && ContractItem.isViable(stack))
                {
                    PlayerEntity indebted = world.getPlayerByUuid(ContractItem.getIndebtedUUID(stack));
                    indebted.addStatusEffect(new StatusEffectInstance(Charter.ETERNAL_DEBT, 160, 0));
                    break;

                }
            }

        }
    }
}
