package net.arathain.charter.mixin;

import com.mojang.authlib.GameProfile;
import net.arathain.charter.block.CharterStoneBlock;
import net.arathain.charter.block.WaystoneBlock;
import net.arathain.charter.components.CharterComponent;
import net.arathain.charter.entity.Indebted;
import net.arathain.charter.util.CharterUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements Indebted {
    private UUID CharterUUID;
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }
    @Inject(method= "onDeath", at = @At("HEAD"))
    private void death(DamageSource source, CallbackInfo ci) {
        CharterComponent charter = CharterUtil.getCharterAtPos(this.getPos(), this.world);
        System.out.println("runs");
        if(charter != null) {
            if (this.getUuid().equals(charter.getCharterOwnerUuid())) {
                System.out.println("check 1 passed");
                charter.getAreas().forEach(area -> {
                    if (area.contains(this.getPos())) {
                        BlockState state = world.getBlockState(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z));
                        if(state.getBlock() instanceof WaystoneBlock) {
                            world.breakBlock(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z), false);
                            System.out.println("waystone passed");
                        }
                        if(state.getBlock() instanceof CharterStoneBlock) {
                            charter.killCharter();
                            System.out.println("kill passed");
                        }
                    }
                });
            } else {
                charter.getMembers().forEach(member -> {
                    if(member.equals(uuid)) {
                        charter.getAreas().forEach(area -> {
                            if (area.contains(this.getPos())) {
                                BlockState state = world.getBlockState(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z));
                                if(state.getBlock() instanceof WaystoneBlock) {
                                    world.breakBlock(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z), false);
                                }
                            }
                        });
                    }
                });

            }
        }
    }

    @Nullable
    @Override
    public UUID getCharterUUID() {
        return CharterUUID;
    }

    @Override
    public void setCharterUUID(UUID charterUUID) {
        CharterUUID = charterUUID;
    }
}
