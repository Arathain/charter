package net.arathain.charter.block.entity;

import net.arathain.charter.Charter;
import net.arathain.charter.components.CharterComponent;
import net.arathain.charter.components.CharterComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class CharterStoneEntity extends BlockEntity {
    private static CharterComponent charter;
    public CharterStoneEntity(BlockPos pos, BlockState state) {
        super(Charter.CHARTER_STONE_ENTITY, pos, state);
    }

    public CharterComponent getCharter() {
        return charter;
    }

    public boolean isActive() {
        return charter != null;
    }
    public void setCharter(CharterComponent newCharter) {
        charter = newCharter;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return super.writeNbt(nbt);
    }

    public static void tick(World tickerWorld, BlockPos pos, BlockState tickerState, CharterStoneEntity blockEntity) {
        CharterComponents.CHARTERS.get(tickerWorld).getCharters().forEach(charterComponent -> {
            if (charterComponent.getCharterOwnerUuid() == charter.getCharterOwnerUuid()) {
                charter = charterComponent;
            }
        });

    }

}
