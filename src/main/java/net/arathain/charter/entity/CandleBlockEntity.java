package net.arathain.charter.entity;


import net.arathain.charter.Charter;
import net.arathain.charter.item.ContractItem;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;


public class CandleBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    public static Optional<UUID> indebtedUUID;
    private static boolean full;
    public CandleBlockEntity(BlockPos pos, BlockState state) {
        super(Charter.CANDLE_ENTITY, pos, state);
    }
    @Override
    public void fromClientTag(NbtCompound tag) {
        indebtedUUID = Optional.ofNullable(tag.getUuid("IndebtedUUID"));
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        indebtedUUID.ifPresent(uuid -> tag.putUuid("IndebtedUUID", uuid));
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
    public static boolean isFilled(CandleBlockEntity entity) {
        return entity.full;
    }

    public void onUse(World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (!getCachedState().get(Properties.WATERLOGGED)) {
            ItemStack stack = player.getStackInHand(hand);
            if (getCachedState().get(Properties.LIT) && stack.getItem() instanceof ContractItem && ContractItem.isViable(stack) && !full) {
                indebtedUUID = Optional.ofNullable(ContractItem.getIndebtedUUID(stack));
                ((Bindable) player).setIndebted(false);
                full = true;
                ContractItem.removeDebt(stack);
            }
        }
        markDirty();
    }
    public static void tick(World world, BlockPos pos, BlockState state, CandleBlockEntity blockEntity) {
        if (world != null) {
            PlayerEntity indebted = world.getPlayerByUuid(indebtedUUID.get());
            if(!state.get(Properties.LIT) && full && indebtedUUID.isPresent() && indebted != null) {
                blockEntity.full = false;
               indebted.remove(Entity.RemovalReason.KILLED);
               indebtedUUID = Optional.empty();
            }
        }
    }

}
