package net.arathain.charter.block.entity.render.model;

import net.arathain.charter.Charter;
import net.arathain.charter.block.WaystoneBlock;
import net.arathain.charter.block.entity.CharterSentinelEntity;
import net.arathain.charter.block.entity.CharterStoneEntity;
import net.arathain.charter.block.entity.WaystoneEntity;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CharterSentinelModel extends AnimatedGeoModel<CharterSentinelEntity> {
    private static final Identifier TEXTURE_IDENTIFIER = new Identifier(Charter.MODID, "textures/entity/charter_sentinel.png");
    private static final Identifier MODEL_IDENTIFIER = new Identifier(Charter.MODID, "geo/charter_sentinel.geo.json");
    private static final Identifier ANIMATION_IDENTIFIER = new Identifier(Charter.MODID, "animations/charter_sentinel.animation.json");
    @Override
    public Identifier getModelLocation(CharterSentinelEntity object) {
        return MODEL_IDENTIFIER;
    }

    @Override
    public Identifier getTextureLocation(CharterSentinelEntity object) {
        return TEXTURE_IDENTIFIER;
    }

    @Override
    public Identifier getAnimationFileLocation(CharterSentinelEntity animatable) {
        return ANIMATION_IDENTIFIER;
    }


    @Override
    public void setLivingAnimations(CharterSentinelEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone bone = this.getAnimationProcessor().getBone("bone");
        IBone eye = this.getAnimationProcessor().getBone("eye");
        PlayerEntity player = entity.getEntityWorld().getClosestPlayer(entity, 33);
        if (player != null) {
            Vector3d target = new Vector3d(player.getX() - entity.getX(), player.getY() - entity.getY(), player.getZ() - entity.getZ());
            entity.setPitch((float) Math.asin(-target.y));
            eye.setRotationY((float) MathHelper.lerp(eye.getRotationY(), (((float) Math.atan2(target.x, target.z)) + Math.toRadians(90)), 0.5f));
        }
    }

}
