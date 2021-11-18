package net.arathain.charter.block.entity;

import net.arathain.charter.Charter;
import net.arathain.charter.item.ContractItem;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PactPressBlockEntity extends BlockEntity implements BlockEntityClientSerializable, AelpecyemIsCool {
    private final DefaultedList<ItemStack> ITEMS = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public PactPressBlockEntity(BlockPos pos, BlockState state) {
        super(Charter.PACT_PRESS_ENTITY, pos, state);
    }
    public PactPressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    public static void tick(World tickerWorld, BlockPos pos, BlockState tickerState, PactPressBlockEntity blockEntity) {
        if (tickerWorld != null) {
            if (tickerState.get(Properties.LIT) && blockEntity.isValid(0, blockEntity.getContract())) {
                if(!tickerWorld.isClient() && tickerWorld.getPlayerByUuid(ContractItem.getIndebtedUUID(blockEntity.getContract())) != null) {
                    PlayerEntity player = tickerWorld.getPlayerByUuid(ContractItem.getIndebtedUUID(blockEntity.getContract()));
                    assert player != null;

                }
            }
        }
    }


    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        return super.writeNbt(toClientTag(tag));
    }
    @Override
    public void fromClientTag(NbtCompound tag) {
        ITEMS.clear();
        Inventories.readNbt(tag, ITEMS);
    }
    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        Inventories.writeNbt(tag, ITEMS);
        return tag;
    }


    @Override
    public void readNbt(NbtCompound nbt) {
        fromClientTag(nbt);
        super.readNbt(nbt);
    }

    @Override
    public void markDirty() {
        if (world != null && !world.isClient) {
            sync();
        }
        super.markDirty();
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return ITEMS;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return ContractItem.isViable(stack);
    }

    public ItemStack getContract() {
        return getStack(0);
    }



}
