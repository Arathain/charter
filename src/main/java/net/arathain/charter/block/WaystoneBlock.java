package net.arathain.charter.block;

import net.arathain.charter.block.entity.WaystoneEntity;
import net.arathain.charter.components.CharterComponent;
import net.arathain.charter.components.CharterComponents;
import net.arathain.charter.util.CharterUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WaystoneBlock extends Block implements BlockEntityProvider {
    public static final VoxelShape SHAPE = createCuboidShape(2, 0, 2, 14, 16, 14);
    public WaystoneBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.LIT, false));
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WaystoneEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof WaystoneEntity && placer instanceof PlayerEntity) {
            CharterComponent charter = CharterUtil.getCharterAtPos(pos, world);

            if(charter != null) {
                charter.addWaystone(pos);
                world.setBlockState(pos, state.with(Properties.LIT, true));
            } else {
                world.setBlockState(pos, state.with(Properties.LIT, false));
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        List<CharterComponent> charters = new ArrayList<>(CharterComponents.CHARTERS.get(world).getCharters());
        for (CharterComponent potentialComponent : charters) {
            List<Box> boxes = new ArrayList<>(potentialComponent.getAreas());
            for (Box box : boxes) {
                if (box.getCenter().x == pos.getX() && box.getCenter().y == pos.getY() && box.getCenter().z == pos.getZ()) {
                    potentialComponent.getAreas().remove(box);
                }
            }
        }
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        super.onDestroyedByExplosion(world, pos, explosion);
        List<CharterComponent> charters = new ArrayList<>(CharterComponents.CHARTERS.get(world).getCharters());
        for (CharterComponent potentialComponent : charters) {
            List<Box> boxes = new ArrayList<>(potentialComponent.getAreas());
            for (Box box : boxes) {
                if (box.getCenter().x == pos.getX() && box.getCenter().y == pos.getY() && box.getCenter().z == pos.getZ()) {
                    potentialComponent.getAreas().remove(box);
                }
            }
        }

    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.LIT));
    }
}
