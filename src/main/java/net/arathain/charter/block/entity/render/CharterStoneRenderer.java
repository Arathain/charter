package net.arathain.charter.block.entity.render;

import net.arathain.charter.block.entity.CharterStoneEntity;
import net.arathain.charter.block.entity.render.model.CharterStoneModel;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class CharterStoneRenderer extends GeoBlockRenderer<CharterStoneEntity>
{
    public CharterStoneRenderer()
    {
        super(new CharterStoneModel());
    }
}
