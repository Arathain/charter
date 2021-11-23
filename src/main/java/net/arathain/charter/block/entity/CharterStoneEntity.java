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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.Optional;
import java.util.UUID;

public class CharterStoneEntity extends BlockEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private static CharterComponent charter;
    public static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("idle");
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

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 2, animationEvent -> {
            AnimationBuilder anime = IDLE;
            animationEvent.getController().setAnimation(anime);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
