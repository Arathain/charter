package net.arathain.charter.mixin;

import net.arathain.charter.Charter;
import net.arathain.charter.entity.Bindable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static net.arathain.charter.Charter.DataTrackers.INDEBTED;


@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Bindable {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readNbt(NbtCompound tag, CallbackInfo info) {

        dataTracker.set(INDEBTED, tag.getBoolean("indebted"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeNbt(NbtCompound tag, CallbackInfo info) {
        NbtCompound rootTag = new NbtCompound();
        rootTag.putBoolean("indebted", dataTracker.get(INDEBTED));
    }

    @Inject(method = "initDataTracker", at = @At("HEAD"))
    public void initTracker(CallbackInfo info) {
        dataTracker.startTracking(INDEBTED, false);
    }

    @Inject(method = "damage", at = @At("HEAD"))
    public void dammage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if(this.hasStatusEffect(Charter.ETERNAL_DEBT)) {

        }
    }

    @Override
    protected boolean tryUseTotem(DamageSource source) {
        if (this.hasStatusEffect(Charter.ETERNAL_DEBT)) {
            this.setHealth(1.0F);
            if(this.hasStatusEffect(StatusEffects.WEAKNESS) && Objects.requireNonNull(this.getStatusEffect(StatusEffects.WEAKNESS)).getAmplifier() > 1) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 300, 4));
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 600, 1));
            }
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 300, 2));
            return true;
        } else {
            return super.tryUseTotem(source);
        }
    }

    public void setIndebted(boolean indebted) {
        dataTracker.set(INDEBTED, indebted);
    }

    public boolean getIndebted() {
        return dataTracker.get(INDEBTED);
    }
}
