package net.arathain.charter.block.entity;

import net.arathain.charter.Charter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class CharterStoneEntity extends BlockEntity {
    public CharterStoneEntity(BlockPos pos, BlockState state) {
        super(Charter.CHARTER_STONE_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return super.writeNbt(nbt);
    }
}
