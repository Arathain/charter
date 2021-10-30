package net.arathain.charter.mixin;

import net.arathain.charter.Charter;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
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
