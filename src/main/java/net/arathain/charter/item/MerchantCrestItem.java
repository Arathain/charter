package net.arathain.charter.item;

import net.arathain.charter.Charter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class MerchantCrestItem extends Item {

    public MerchantCrestItem(Settings settings) {
        super(settings);
    }

    public static ItemStack setTeleported(ItemStack stack, boolean teleported) {
        assert stack.getOrCreateNbt() != null;
        stack.getOrCreateNbt().putBoolean("teleported", teleported);
        return stack;
    }
    public static boolean getTeleported(ItemStack stack) {
        assert stack.getNbt() != null;
        return stack.getNbt().getBoolean("teleported");
    }

    public static ItemStack putTeleported(ItemStack stack, float x, float y, float z) {
        NbtCompound poss = new NbtCompound();
        assert stack.getOrCreateNbt() != null;
        poss.putFloat("X", x);
        poss.putFloat("Y", y);
        poss.putFloat("Z", z);
        stack.getOrCreateNbt().put("prevPos", poss);
        return stack;
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        ItemStack otherStack = user.getStackInHand(Hand.OFF_HAND);
        if(hand == Hand.OFF_HAND) {
            otherStack = user.getStackInHand(Hand.MAIN_HAND);
        }
        if(stack.getNbt() == null) {
            user.setStackInHand(hand, setTeleported(stack, false));
        }
        if(ContractItem.isViable(otherStack) && !user.getItemCooldownManager().isCoolingDown(stack.getItem())) {
            PlayerEntity player = world.getPlayerByUuid(ContractItem.getIndebtedUUID(otherStack));
            if(player != null) {
                if(!getTeleported(stack)) {
                user.setStackInHand(hand, putTeleported(stack, (float) user.getPos().x, (float) user.getPos().y, (float) user.getPos().z));
                user.setStackInHand(hand, setTeleported(stack, true));
                user.setPosition(player.getPos());
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 200, 0, false, false));
                } else {
                    assert stack.getNbt() != null;
                    NbtCompound poss = stack.getNbt().getCompound("prevPos");
                    float possX = poss.getFloat("X");
                    float possY = poss.getFloat("Y");
                    float possZ = poss.getFloat("Z");
                    user.setPos(possX, possY, possZ);
                    user.setStackInHand(hand, setTeleported(stack, false));
                    stack.getOrCreateNbt().remove("prevPos");
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 30, 0, false, false));
                }
                player.getItemCooldownManager().set(this, 40);
                return TypedActionResult.success(user.getStackInHand(hand));
            } else {
                return TypedActionResult.fail(user.getStackInHand(hand));
            }
        } else {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if(!world.isClient && entity instanceof ServerPlayerEntity)
        {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;


            for (ItemStack itemStack : player.getInventory().main) {
                if(!itemStack.isEmpty() && ContractItem.isViable(itemStack)) {
                    Vec3d pos = player.getPos();
                    List<PlayerEntity> entities = player.getEntityWorld().getEntitiesByClass(
                            PlayerEntity.class,
                            new Box(
                                    pos.getX() - 4, pos.getY() - 4, pos.getZ() - 4,
                                    pos.getX() + 4, pos.getY() + 4, pos.getZ() + 4
                            ), (ArrowEntity) -> true
                    );

                    for (PlayerEntity nearbyEntity : entities) {
                        if(nearbyEntity.getUuid() == ContractItem.getIndebtedUUID(itemStack) && player.getHealth() < player.getMaxHealth() && nearbyEntity.getHealth() > 1) {
                            player.heal(1);
                            nearbyEntity.damage(DamageSource.STARVE, 1);
                        }
                    }
                }
            }

        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
