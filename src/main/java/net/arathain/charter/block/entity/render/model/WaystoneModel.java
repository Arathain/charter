package net.arathain.charter.block.entity.render.model;

import net.arathain.charter.Charter;
import net.arathain.charter.block.entity.WaystoneEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class WaystoneModel extends AnimatedGeoModel<WaystoneEntity> {
    private static final Identifier TEXTURE_IDENTIFIER = new Identifier(Charter.MODID, "textures/block/waystone.png");
    private static final Identifier MODEL_IDENTIFIER = new Identifier(Charter.MODID, "geo/waystone.geo.json");
    private static final Identifier ANIMATION_IDENTIFIER = new Identifier(Charter.MODID, "animations/waystone.animation.json");

    @Override
    public Identifier getModelResource(WaystoneEntity object) {
        return MODEL_IDENTIFIER;
    }

    @Override
    public Identifier getTextureResource(WaystoneEntity object) {
        return TEXTURE_IDENTIFIER;
    }

    @Override
    public Identifier getAnimationFileResource(WaystoneEntity animatable) {
        return ANIMATION_IDENTIFIER;
    }

}
