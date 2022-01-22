package net.arathain.charter.block;

import net.arathain.charter.block.entity.CharterStoneEntity;
import net.arathain.charter.block.particle.BindingAmbienceParticleEffect;
import net.arathain.charter.components.CharterComponent;
import net.arathain.charter.components.CharterComponents;
import net.arathain.charter.util.CharterUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CharterStoneBlock extends Block implements Waterloggable, BlockEntityProvider {
        public static final VoxelShape SHAPE = createCuboidShape(2, 0, 2, 14, 32, 14);
    public CharterStoneBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.WATERLOGGED, false));
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if(CharterUtil.getCharterAtPos(pos, (World) world) != null) {
            return false;
        } else {
            return super.canPlaceAt(state, world, pos);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof CharterStoneEntity && placer instanceof PlayerEntity) {
            Optional<CharterComponent> charter = CharterComponents.CHARTERS.get(world).getCharters().stream().filter(existingCharter -> existingCharter.getCharterOwnerUuid() == placer.getUuid()).findFirst();

            if (charter.isPresent()) {
                world.breakBlock(pos, true);
            }
            else {
                if (!CharterUtil.isInCharter((PlayerEntity) placer, world)) {
                    CharterComponents.CHARTERS.get(world).getCharters().add(new CharterComponent(pos, (PlayerEntity) placer, world));
                } else {
                    world.breakBlock(pos, true);
                }
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }


    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(Properties.WATERLOGGED, true), 3);
                world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            }
            return true;
        }
        return false;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        final BlockState state = this.getDefaultState();
        if (state.contains(Properties.WATERLOGGED)) {
            final FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
            final boolean source = fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8;
            return state.with(Properties.WATERLOGGED, source);
        }
        return state;
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
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int l = 0; l < 64; ++l) {
            mutable.set(i + MathHelper.nextInt(random, -10, 10), j - random.nextInt(10), k + MathHelper.nextInt(random, -10, 10));
            BlockState blockState = world.getBlockState(mutable);
            if (blockState.isFullCube(world, mutable)) continue;
            world.addParticle(new BindingAmbienceParticleEffect(1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f), (double)mutable.getX() + (random.nextFloat() - 0.5) * 32, (double)mutable.getY() + 1 + (random.nextFloat() - 0.5) * 22, (double)mutable.getZ() + (random.nextFloat() - 0.5) * 32, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CharterStoneEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.WATERLOGGED));
    }

}
