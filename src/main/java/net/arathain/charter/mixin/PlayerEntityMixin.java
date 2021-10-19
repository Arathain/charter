package net.arathain.charter.mixin;

import net.arathain.charter.Charter;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
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
