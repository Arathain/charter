package net.arathain.charter.mixin;

import net.arathain.charter.entity.CandleBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Random;

@Mixin(CandleBlock.class)
public abstract class CandleBlockMixin extends AbstractCandleBlock implements Waterloggable, BlockEntityProvider {
    protected CandleBlockMixin(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CandleBlockEntity(pos, state);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if ((Boolean)state.get(LIT)) {
            if( CandleBlockEntity.isFilled((CandleBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos)))) {
                this.getParticleOffsets(state).forEach((offset) -> {
                    spawnCandleParticles(world, offset.add((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), random, ParticleTypes.SOUL_FIRE_FLAME);
                });
            } else {
                this.getParticleOffsets(state).forEach((offset) -> {
                    spawnCandleParticles(world, offset.add((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), random, ParticleTypes.SMALL_FLAME);
                });
            }
        }
    }

    private static void spawnCandleParticles(World world, Vec3d vec3d, Random random, ParticleEffect type) {
        float f = random.nextFloat();
        if (f < 0.3F) {
            world.addParticle(ParticleTypes.SMOKE, vec3d.x, vec3d.y, vec3d.z, 0.0D, 0.0D, 0.0D);
            if (f < 0.17F) {
                world.playSound(vec3d.x + 0.5D, vec3d.y + 0.5D, vec3d.z + 0.5D, SoundEvents.BLOCK_CANDLE_AMBIENT, SoundCategory.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
            }
        }

        world.addParticle(type, vec3d.x, vec3d.y, vec3d.z, 0.0D, 0.0D, 0.0D);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (tickerWorld, pos, tickerState, blockEntity) -> CandleBlockEntity.tick(tickerWorld, pos, tickerState, (CandleBlockEntity) blockEntity);
    }

    @Inject(method = "onUse", at = @At("HEAD"))
    public void youse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        boolean client = world.isClient;
        if (!client) {
            ((CandleBlockEntity) world.getBlockEntity(pos)).onUse(world, pos, player, hand);
        }

    }
}
