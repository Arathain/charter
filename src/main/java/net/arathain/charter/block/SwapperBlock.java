package net.arathain.charter.block;

import com.mojang.authlib.GameProfile;
import io.github.ladysnake.impersonate.Impersonator;
import io.github.ladysnake.impersonate.impl.ImpersonateCommand;
import net.arathain.charter.Charter;
import net.arathain.charter.CharterStatusEffect;
import net.arathain.charter.block.entity.PactPressBlockEntity;
import net.arathain.charter.item.ContractItem;
import net.minecraft.block.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.Collection;
import java.util.Objects;

public class SwapperBlock extends PillarBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;

    public static final Identifier IMPERSONATE_IDENTIFIER = new Identifier(Charter.MODID, "swap");

    public SwapperBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.POWERED));
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            boolean bl = world.isReceivingRedstonePower(pos);
            if ((Boolean)state.get(POWERED) != bl) {
                world.setBlockState(pos, (BlockState)((BlockState)state.with(POWERED, bl)), Block.NOTIFY_LISTENERS);
                if (bl) {
                    BlockPos pos1 = pos.offset(state.get(Properties.AXIS), 1);
                    BlockPos pos2 = pos.offset(state.get(Properties.AXIS), -1);
                    if (!(world.getBlockState(pos1).getBlock() instanceof BlockEntityProvider) && !(world.getBlockState(pos2).getBlock() instanceof BlockEntityProvider)) {
                        BlockState state1 = world.getBlockState(pos1);
                        BlockState state2 = world.getBlockState(pos2);
                        world.setBlockState(pos1, state2);
                        world.setBlockState(pos2, state1);
                    }
                    if ((world.getBlockState(pos1).getBlock() instanceof PactPressBlock) && (world.getBlockState(pos2).getBlock() instanceof PactPressBlock)) {
                        BlockState state1 = world.getBlockState(pos1);
                        BlockState state2 = world.getBlockState(pos2);
                        if (state1.get(Properties.LIT) && state2.get(Properties.LIT)) {
                            PactPressBlockEntity press1 = (PactPressBlockEntity) world.getBlockEntity(pos1);
                            PactPressBlockEntity press2 = (PactPressBlockEntity) world.getBlockEntity(pos2);
                            assert press1 != null;
                            if (!press1.getItems().isEmpty() && ContractItem.isViable(press1.getContract())) {
                                assert press2 != null;
                                if (!press2.getItems().isEmpty() && ContractItem.isViable(press2.getContract())) {
                                    PlayerEntity player1 = world.getPlayerByUuid(ContractItem.getIndebtedUUID(press1.getContract()));
                                    PlayerEntity player2 = world.getPlayerByUuid(ContractItem.getIndebtedUUID(press2.getContract()));
                                    ItemStack stack1 = press1.getContract();
                                    ItemStack stack2 = press2.getContract();
                                    press1.setStack(0, stack2);
                                    press2.setStack(0, stack1);
                                    press1.markDirty();
                                    press2.markDirty();
                                    if(player1 != null && player2 != null) {
                                        PlayerInventory inv1 = player1.getInventory();
                                        PlayerInventory inv2 = player2.getInventory();
                                        float health1 = player1.getHealth();
                                        float health2 = player2.getHealth();
                                        Collection<StatusEffectInstance> statusEffects1 = player1.getStatusEffects();
                                        Collection<StatusEffectInstance> statusEffects2 = player1.getStatusEffects();
                                        Vec3d pPos1 = player1.getPos();
                                        Vec3d pPos2 = player2.getPos();
                                        Vec3d velocity1 = player1.getVelocity();
                                        Vec3d velocity2 = player2.getVelocity();
                                        if (Impersonator.get(player1).getImpersonatedProfile() != null && Impersonator.get(player2).getImpersonatedProfile() != null) {

                                            GameProfile profile1 = Impersonator.get(player1).getImpersonatedProfile();
                                            GameProfile profile2 = Impersonator.get(player2).getImpersonatedProfile();


                                            Impersonator.get(player1).impersonate(IMPERSONATE_IDENTIFIER, Objects.requireNonNull(profile2));
                                            Impersonator.get(player2).impersonate(IMPERSONATE_IDENTIFIER, Objects.requireNonNull(profile1));
                                        } if (Impersonator.get(player1).getImpersonatedProfile() == null && Impersonator.get(player2).getImpersonatedProfile() != null) {

                                            GameProfile profile1 = Impersonator.get(player1).getActualProfile();
                                            GameProfile profile2 = Impersonator.get(player2).getImpersonatedProfile();


                                            Impersonator.get(player1).impersonate(IMPERSONATE_IDENTIFIER, Objects.requireNonNull(profile2));
                                            Impersonator.get(player2).impersonate(IMPERSONATE_IDENTIFIER, Objects.requireNonNull(profile1));
                                        } if (Impersonator.get(player1).getImpersonatedProfile() != null && Impersonator.get(player2).getImpersonatedProfile() == null) {

                                            GameProfile profile1 = Impersonator.get(player1).getImpersonatedProfile();
                                            GameProfile profile2 = Impersonator.get(player2).getActualProfile();


                                            Impersonator.get(player1).impersonate(IMPERSONATE_IDENTIFIER, Objects.requireNonNull(profile2));
                                            Impersonator.get(player2).impersonate(IMPERSONATE_IDENTIFIER, Objects.requireNonNull(profile1));
                                        }
                                        if (Impersonator.get(player1).getImpersonatedProfile() == null && Impersonator.get(player2).getImpersonatedProfile() == null) {

                                            GameProfile profile1 = Impersonator.get(player1).getActualProfile();
                                            GameProfile profile2 = Impersonator.get(player2).getActualProfile();


                                            Impersonator.get(player1).impersonate(IMPERSONATE_IDENTIFIER, Objects.requireNonNull(profile2));
                                            Impersonator.get(player2).impersonate(IMPERSONATE_IDENTIFIER, Objects.requireNonNull(profile1));
                                        }


                                            player1.setHealth(health2);
                                            player1.clearStatusEffects();
                                            for(StatusEffectInstance instance : statusEffects2) {
                                                if (instance.getEffectType() != Charter.ETERNAL_DEBT) {
                                                    player1.addStatusEffect(instance);
                                                }
                                            }
                                            player1.setPosition(pPos2);
                                            player1.setVelocity(velocity2);
                                            player1.getInventory().clone(inv2);



                                            player2.setHealth(health1);
                                            player2.clearStatusEffects();
                                            for(StatusEffectInstance instance : statusEffects1) {
                                                player2.addStatusEffect(instance);
                                            }
                                            player2.setPosition(pPos1);
                                            player2.setVelocity(velocity1);
                                            player2.getInventory().clone(inv1);


                                    }
                                }
                            }
                        }
                    }
                }

            }

        }
    }
}
