package net.arathain.charter.block;

import net.arathain.charter.block.entity.AelpecyemIsCool;
import net.arathain.charter.block.entity.PactPressBlockEntity;
import net.arathain.charter.item.ContractItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PactPressBlock extends Block implements BlockEntityProvider {
    public PactPressBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.LIT, false).with(Properties.FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return createCuboidShape(4, 0, 4, 12, 16, 12);
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
                world.setBlockState(pos, state.with(Properties.LIT, true));
                world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1, 1);
                press.setStack(0, stack);
                press.markDirty();
                return ActionResult.CONSUME;
            } else if (!press.getItems().isEmpty() && stack.isEmpty() && ContractItem.isViable(press.getContract())) {
                assert press.getContract() != null;
                world.spawnEntity(new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), press.getContract()));
                ((AelpecyemIsCool) press).clear();
                press.removeStack(0);
                world.setBlockState(pos, state.with(Properties.LIT, false));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.success(client);
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return (tickerWorld, pos, tickerState, blockEntity) -> PactPressBlockEntity.tick(tickerWorld, pos, tickerState, (PactPressBlockEntity) blockEntity);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(Properties.FACING, ctx.getPlayerFacing());
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        PactPressBlockEntity press = (PactPressBlockEntity) world.getBlockEntity(pos);
        if(press != null && press.getContract().getItem() instanceof ContractItem && world.getPlayerByUuid(ContractItem.getIndebtedUUID(press.getContract())) != null) {
            Objects.requireNonNull(world.getPlayerByUuid(ContractItem.getIndebtedUUID(press.getContract()))).kill();
        }
        super.onBroken(world, pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.LIT, Properties.FACING));
    }

}
