package net.arathain.charter.block.entity.render;

import net.arathain.charter.block.entity.CharterStoneEntity;
import net.arathain.charter.block.entity.render.model.CharterMarksModel;
import net.arathain.charter.block.entity.render.model.CharterStoneModel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import java.awt.*;

public class CharterStoneRenderer extends GeoBlockRenderer<CharterStoneEntity>
{
    AnimatedGeoModel<CharterStoneEntity> stone = new CharterStoneModel();
    public CharterStoneRenderer()
    {
        super(new CharterMarksModel());
    }

    //this is a terrible idea. this is not how this should be done. too bad I can't be bothered to work on this @*^&#$ anymore
    @Override
    public void render(CharterStoneEntity tile, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn) {
        super.render(tile, partialTicks, stack, bufferIn, 15728880);

        GeoModel model = stone.getModel(stone.getModelLocation(tile));
        stack.push();
        stack.translate(0, 0.01f, 0);
        stack.translate(0.5, 0, 0.5);

        MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(tile));
        Color renderColor = getRenderColor(tile, partialTicks, stack, bufferIn, null, packedLightIn);
        RenderLayer renderType = getRenderType(tile, partialTicks, stack, bufferIn, null, packedLightIn,
                getTextureLocation(tile));
        render(model, tile, partialTicks, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV,
                (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        stack.pop();
    }
}
