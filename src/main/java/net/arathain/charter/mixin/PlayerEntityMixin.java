package net.arathain.charter.mixin;

import net.arathain.charter.Charter;
import net.arathain.charter.block.CharterStoneBlock;
import net.arathain.charter.block.WaystoneBlock;
import net.arathain.charter.components.CharterComponent;
import net.arathain.charter.components.CharterComponents;
import net.arathain.charter.util.CharterUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

    @Shadow protected HungerManager hungerManager;

    @Inject(method = "tick()V", at = @At("TAIL"))
    public void tik(CallbackInfo info) {
        if (this.hasStatusEffect(Charter.ETERNAL_DEBT) && this.world.isClient) {
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + random.nextGaussian() / 16, this.getY(), this.getZ() + random.nextGaussian() / 16, random.nextGaussian() / 30, 0.01, random.nextGaussian() / 30);
        }
        if (this.hasStatusEffect(Charter.ETERNAL_DEBT) && !this.world.isClient) {
            hungerManager.setSaturationLevel(0);
            if (this.isInsideWaterOrBubbleColumn()) {
                if (this.getEntityWorld().getBiomeKey(this.getBlockPos()).equals(Optional.of(BiomeKeys.RIVER))) {
                    this.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 60, 2, false, false));
                }
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 40, 1, false, false));
            }
        }
    }

    @Inject(method= "onDeath", at = @At("HEAD"))
    private void death(DamageSource source, CallbackInfo ci) {
        CharterComponent charter = CharterUtil.getCharterAtPos(this.getPos(), this.world);
        if(charter != null) {
            if (this.getUuid().equals(charter.getCharterOwnerUuid())) {
                List<Box> boxes = new ArrayList<>(charter.getAreas());
                boxes.forEach(area -> {
                    if (area.contains(this.getPos())) {
                        BlockState state = world.getBlockState(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z));
                        if(state.getBlock() instanceof CharterStoneBlock) {
                            charter.killCharter();
                        }
                        if(state.getBlock() instanceof WaystoneBlock) {
                            charter.decrementUses(-1000);
                            BlockPos pos = new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z);
                            world.breakBlock(pos, true);
                            charter.getAreas().remove(area);
                            world.setBlockState(pos, Charter.BROKEN_WAYSTONE.getDefaultState());
                        }
                    }
                });
            } else {
                charter.getMembers().forEach(member -> {
                    if(member.equals(uuid)) {
                        List<Box> boxes = new ArrayList<>(charter.getAreas());
                        boxes.forEach(area -> {
                            if (area.contains(this.getPos())) {
                                BlockState state = world.getBlockState(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z));
                                if(state.getBlock() instanceof WaystoneBlock) {
                                    charter.decrementUses(-250);
                                    BlockPos pos = new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z);
                                    world.breakBlock(pos, true);
                                    world.setBlockState(pos, Charter.BROKEN_WAYSTONE.getDefaultState());
                                    charter.getAreas().remove(area);
                                }
                            }
                        });
                    }
                });

            }
        }
    }



    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void dmg(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity attacker) {
            List<CharterComponent> charters = new ArrayList<>(CharterComponents.CHARTERS.get(this.world).getCharters());
            charters.forEach(charterComponent -> {
                if(charterComponent.getCharterOwnerUuid().equals(this.getUuid())) {
                    List<UUID> members = new ArrayList<>(charterComponent.getMembers());
                    members.forEach(member -> {
                        if(member.equals(attacker.getUuid())) {
                            cir.setReturnValue(false);
                        }
                    });
                }
            });
        }
        if (this.hasStatusEffect(Charter.SOUL_STRAIN) && !this.world.isClient) {
            source.setUsesMagic();
            amount = amount * 2;
            if (this.isInvulnerableTo(source)) {
                cir.setReturnValue(false);
            } else {
                this.despawnCounter = 0;
                if (this.isDead()) {
                    cir.setReturnValue(false);
                } else {
                    if (source.isScaledWithDifficulty()) {
                        if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
                            amount = 0.0F;
                        }

                        if (this.world.getDifficulty() == Difficulty.EASY) {
                            amount = Math.min(amount / 2.0F + 1.0F, amount);
                        }

                        if (this.world.getDifficulty() == Difficulty.HARD) {
                            amount = amount * 3.0F / 2.0F;
                        }
                    }

                    cir.setReturnValue(amount != 0.0F && super.damage(source, amount));
                }
            }
        }

    }

    @Override
    protected boolean tryUseTotem(DamageSource source) {
        if (this.hasStatusEffect(Charter.ETERNAL_DEBT) && source.isUnblockable()) {
            this.setHealth(1.0F);
            if(this.hasStatusEffect(StatusEffects.WEAKNESS) && Objects.requireNonNull(this.getStatusEffect(StatusEffects.WEAKNESS)).getAmplifier() > 1) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 300, 4));
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 300, 4));
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 600, 1));
            }
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 300, 2));
            return true;
        } else {
            return super.tryUseTotem(source);
        }
    }

}
