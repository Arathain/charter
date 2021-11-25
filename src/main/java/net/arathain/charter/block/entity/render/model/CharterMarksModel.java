package net.arathain.charter.block.entity.render.model;

import net.arathain.charter.Charter;
import net.arathain.charter.block.entity.CharterStoneEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CharterMarksModel extends AnimatedGeoModel<CharterStoneEntity> {
    private static final Identifier TEXTURE_IDENTIFIER = new Identifier(Charter.MODID, "textures/block/charter_stone.png");
    private static final Identifier MODEL_IDENTIFIER = new Identifier(Charter.MODID, "geo/charter_marks.geo.json");
    private static final Identifier ANIMATION_IDENTIFIER = new Identifier(Charter.MODID, "animations/charter_marks.animation.json");
    @Override
    public Identifier getModelLocation(CharterStoneEntity object) {
        return MODEL_IDENTIFIER;
    }

    @Override
    public Identifier getTextureLocation(CharterStoneEntity object) {
        return TEXTURE_IDENTIFIER;
    }

    @Override
    public Identifier getAnimationFileLocation(CharterStoneEntity animatable) {
        return ANIMATION_IDENTIFIER;
    }

}
