package net.arathain.charter.block;

import net.arathain.charter.block.entity.AelpecyemIsCool;
import net.arathain.charter.block.entity.PactPressBlockEntity;
import net.arathain.charter.components.CharterComponent;
import net.arathain.charter.components.CharterComponents;
import net.arathain.charter.item.ContractItem;
import net.arathain.charter.util.CharterUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PactPressBlock extends Block implements BlockEntityProvider, Waterloggable {
    public PactPressBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.LIT, false).with(Properties.FACING, Direction.NORTH).with(Properties.WATERLOGGED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return createCuboidShape(6, 0, 6, 10, 14, 10);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PactPressBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        boolean client = world.isClient;
        if (!client) {
            ItemStack stack = player.getStackInHand(hand);
            PactPressBlockEntity press = (PactPressBlockEntity) world.getBlockEntity(pos);
            assert press != null;
            if(press.getContract() == ItemStack.EMPTY && ContractItem.isViable(stack)) {
                if(getCharterStoneComponent(pos, world) != null && !CharterUtil.isCharterOwner(world.getPlayerByUuid(ContractItem.getIndebtedUUID(stack)), world)) {
                    Objects.requireNonNull(getCharterStoneComponent(pos, world)).getMembers().add(ContractItem.getIndebtedUUID(stack));
                }
                world.setBlockState(pos, state.with(Properties.LIT, true));
                world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1, 1);
                press.setStack(0, stack);
                press.markDirty();
                return ActionResult.CONSUME;
            } else if (!press.getItems().isEmpty() && stack.isEmpty() && ContractItem.isViable(press.getContract())) {
                assert press.getContract() != null;
                world.spawnEntity(new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), press.getContract()));
                if(getCharterStoneComponent(pos, world) != null) {
                    Objects.requireNonNull(getCharterStoneComponent(pos, world)).getMembers().removeIf(member -> member.equals(ContractItem.getIndebtedUUID(press.getContract())) && !member.equals(getCharterStoneComponent(pos,world).getCharterOwnerUuid()));
                }
                ((AelpecyemIsCool) press).clear();
                press.removeStack(0);
                world.setBlockState(pos, state.with(Properties.LIT, false));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.success(client);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
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
    public FluidState getFluidState(BlockState state) {
        return state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (tickerWorld, pos, tickerState, blockEntity) -> PactPressBlockEntity.tick(tickerWorld, pos, tickerState, (PactPressBlockEntity) blockEntity);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        final BlockState state = this.getDefaultState().with(Properties.FACING, ctx.getPlayerFacing());
        if (state.contains(Properties.WATERLOGGED)) {
            final FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
            final boolean source = fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8;
            return state.with(Properties.WATERLOGGED, source);
        }
        return state;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        PactPressBlockEntity press = (PactPressBlockEntity) blockEntity;
        if(press != null && press.getContract().getItem() instanceof ContractItem && world.getPlayerByUuid(ContractItem.getIndebtedUUID(press.getContract())) != null) {
            Objects.requireNonNull(world.getPlayerByUuid(ContractItem.getIndebtedUUID(press.getContract()))).kill();
            if(getCharterStoneComponent(pos, world) != null) {
                Objects.requireNonNull(getCharterStoneComponent(pos, world)).getMembers().removeIf(member -> member.equals(ContractItem.getIndebtedUUID(press.getContract())) && !member.equals(getCharterStoneComponent(pos,world).getCharterOwnerUuid()));
            }
        }

        super.afterBreak(world, player, pos, state, blockEntity, stack);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.LIT, Properties.FACING, Properties.WATERLOGGED));
    }

    @Nullable
    public static CharterComponent getCharterStoneComponent(BlockPos blockPos, World world) {
        CharterComponent component = null;
        List<CharterComponent> charters = new ArrayList<>(CharterComponents.CHARTERS.get(world).getCharters());
        for (CharterComponent potentialComponent : charters) {
            List<Box> boxes = new ArrayList<>(potentialComponent.getAreas());
            for (Box box : boxes) {
                if (box.contains(blockPos.getX(), blockPos.getY(), blockPos.getZ()) && box.getCenter().equals(new Vec3d(potentialComponent.getCharterStonePos().getX(), potentialComponent.getCharterStonePos().getY(), potentialComponent.getCharterStonePos().getZ()))) {
                    component = potentialComponent;
                }
            }
        }
        return component;
    }

}
