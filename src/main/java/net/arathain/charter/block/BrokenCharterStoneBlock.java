package net.arathain.charter.block;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BrokenCharterStoneBlock extends Block {
    public static final VoxelShape SHAPE = createCuboidShape(2, 0, 2, 14, 32, 14);
    public BrokenCharterStoneBlock(Settings settings) {
        super(settings);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        double d = (double)i + random.nextDouble();
        double e = (double)j + 0.7;
        double f = (double)k + random.nextDouble();
        world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, f, 0.0, 0.0, 0.0);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int l = 0; l < 14; ++l) {
            mutable.set(i + MathHelper.nextInt(random, -10, 10), j - random.nextInt(10), k + MathHelper.nextInt(random, -10, 10));
            BlockState blockState = world.getBlockState(mutable);
            if (blockState.isFullCube(world, mutable)) continue;
            world.addParticle(ParticleTypes.LARGE_SMOKE, (double)mutable.getX() + random.nextDouble(), (double)mutable.getY() + 4 + random.nextDouble(), (double)mutable.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        List<LivingEntity> entities = world.getEntitiesByClass(
                LivingEntity.class,
                new Box(
                        pos.getX() - 10, pos.getY() - 10, pos.getZ() - 10,
                        pos.getX() + 10, pos.getY() + 10, pos.getZ() + 10
                ), (entity) -> true
        );
        if (!world.isClient) {
            for (LivingEntity entity : entities) {
                if(entity instanceof HostileEntity) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 640, 2));
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20, 1));
                }
                if(entity instanceof PlayerEntity) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 640, 2));
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 640, 5));
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 120, 1));
                }
            }
        }

    }
}
