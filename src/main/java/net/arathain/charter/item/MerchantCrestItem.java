package net.arathain.charter.item;

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

public class MerchantCrestItem extends Item {
    public MerchantCrestItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if(!world.isClient && entity instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;

                for(int i = 0; i < player.getInventory().size(); i++)
                {
                    ItemStack stack2 = player.getInventory().getStack(i);
                    if(!stack2.isEmpty() && stack2.getItem() instanceof ContractItem && ContractItem.isViable(stack) && player.getRandom().nextInt(40) == 34)
                    {
                        Vec3d pos = player.getPos();
                            List<PlayerEntity> entities = player.getEntityWorld().getEntitiesByClass(
                                    PlayerEntity.class,
                                    new Box(
                                            pos.getX() - 4, pos.getY() - 4, pos.getZ() - 4,
                                            pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4
                                    ), (ArrowEntity) -> true
                            );

                            for (PlayerEntity nearbyEntity : entities) {
                                if(nearbyEntity.getUuid() == ContractItem.getIndebtedUUID(stack2) && player.getHealth() < player.getMaxHealth() && nearbyEntity.getHealth() > 1) {
                                    player.heal(1);
                                    nearbyEntity.damage(DamageSource.MAGIC, 1);
                                    nearbyEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.UNLUCK, 160, 0));
                                }
                            }

                    }
                }

        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
