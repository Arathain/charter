package net.arathain.charter.entity;


import net.arathain.charter.Charter;
import net.arathain.charter.item.ContractItem;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;


public class CandleBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private static UUID indebted = null;
    private static boolean full;
    public CandleBlockEntity(BlockPos pos, BlockState state) {
        super(Charter.CANDLE_ENTITY, pos, state);
    }


    public static UUID getIndebted() {
        return indebted;
    }
    public void setIndebted(UUID indebted) {
        CandleBlockEntity.indebted = indebted;
    }
    public static boolean getFull() {
        return indebted != null;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        if(tag.containsUuid("Indebted")) {
            indebted = tag.getUuid("Indebted");
        }
        if(tag.contains("Full")) {
            full = tag.getBoolean("Full");
        }

    }

    public static void spawnCandleParticles(World world, Vec3d vec3d, Random random) {
        float f = random.nextFloat();
        if (f < 0.3F) {
            world.addParticle(ParticleTypes.SMOKE, vec3d.x, vec3d.y, vec3d.z, 0.0D, 0.0D, 0.0D);
            if (f < 0.17F) {
                world.playSound(vec3d.x + 0.5D, vec3d.y + 0.5D, vec3d.z + 0.5D, SoundEvents.BLOCK_CANDLE_AMBIENT, SoundCategory.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }
        }
        if(getFull()) {
        world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, vec3d.x, vec3d.y, vec3d.z, 0.0D, 0.0D, 0.0D);
        } else {
            world.addParticle(ParticleTypes.SMALL_FLAME, vec3d.x, vec3d.y, vec3d.z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        if(getIndebted() != null) {
            tag.putUuid("IndebtedUUID", getIndebted());
        }
        tag.putBoolean("Full", full);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        fromClientTag(nbt);
        super.readNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return super.writeNbt(toClientTag(nbt));
    }

    public void onUse(World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (!getCachedState().get(Properties.WATERLOGGED)) {
            ItemStack stack = player.getStackInHand(hand);
            if (getCachedState().get(Properties.LIT) && stack.getItem() instanceof ContractItem && ContractItem.isViable(stack) && !full) {
                setIndebted(ContractItem.getIndebtedUUID(stack));
                ((Bindable) player).setIndebted(false);
                full = true;
                ContractItem.removeDebt(stack);
            }
        }
        markDirty();
    }
    public static void tick(World world, BlockPos pos, BlockState state, CandleBlockEntity blockEntity) {
        if (world != null && getIndebted() != null) {
            PlayerEntity indebt = world.getPlayerByUuid(indebted);
            if(!state.get(Properties.LIT) && full && indebt != null) {
               full = false;
               indebt.kill();
               indebt = null;
            }
        }
    }

}
