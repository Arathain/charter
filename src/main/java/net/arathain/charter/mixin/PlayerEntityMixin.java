package net.arathain.charter.mixin;

import net.arathain.charter.Charter;
import net.arathain.charter.entity.Bindable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        if(this.hasStatusEffect(Charter.SOUL_STRAIN)) {
            amount = amount * 2;
        }
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if(getIndebted() && !hasStatusEffect(StatusEffects.UNLUCK)) {
            addStatusEffect(new StatusEffectInstance(Charter.SOUL_STRAIN, 160, 0));
        }
    }

    public void setIndebted(boolean indebted) {
        dataTracker.set(INDEBTED, indebted);
    }

    public boolean getIndebted() {
        return dataTracker.get(INDEBTED);
    }
}
