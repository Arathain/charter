package net.arathain.charter.block.entity.render.model;

import net.arathain.charter.Charter;
import net.arathain.charter.block.WaystoneBlock;
import net.arathain.charter.block.entity.WaystoneEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.Objects;

public class WaystoneMarksModel extends AnimatedGeoModel<WaystoneEntity> {
private static final Identifier TEXTURE_IDENTIFIER = new Identifier(Charter.MODID, "textures/block/waystone.png");
private static final Identifier MODEL_IDENTIFIER = new Identifier(Charter.MODID, "geo/waystone_marks.geo.json");
private static final Identifier ANIMATION_IDENTIFIER = new Identifier(Charter.MODID, "animations/waystone.animation.json");

@Override
public Identifier getModelResource(WaystoneEntity object) {
        return MODEL_IDENTIFIER;
        }

@Override
public Identifier getTextureLocation(WaystoneEntity object) {
        return TEXTURE_IDENTIFIER;
        }

@Override
public Identifier getAnimationFileLocation(WaystoneEntity animatable) {
        return ANIMATION_IDENTIFIER;
        }

        @Override
        public void setLivingAnimations(WaystoneEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
                IBone marks4 = this.getAnimationProcessor().getBone("marks4");
                IBone marks5 = this.getAnimationProcessor().getBone("marks5");

                if(entity.getWorld() != null && entity.getWorld().getBlockState(entity.getPos()) != null && entity.getWorld().getBlockState(entity.getPos()).getBlock() instanceof WaystoneBlock && entity.getWorld().getBlockState(entity.getPos()).get(Properties.LIT)) {
                        super.setLivingAnimations(entity, uniqueID, customPredicate);
                        marks4.setHidden(false);
                        marks5.setHidden(false);
                } else {
                        marks4.setHidden(true);
                        marks5.setHidden(true);
                }
        }
}
