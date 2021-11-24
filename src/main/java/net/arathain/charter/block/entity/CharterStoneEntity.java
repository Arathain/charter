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
    public static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("animation.model.idle");
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

    public static void tick(World tickerWorld, BlockPos pos, BlockState tickerState, CharterStoneEntity blockEntity) {

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
