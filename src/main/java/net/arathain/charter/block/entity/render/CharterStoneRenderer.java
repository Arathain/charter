package net.arathain.charter.block.entity.render;

import net.arathain.charter.block.entity.CharterStoneEntity;
import net.arathain.charter.block.entity.render.model.CharterMarksModel;
import net.arathain.charter.block.entity.render.model.CharterStoneModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import java.awt.*;

public class CharterStoneRenderer extends GeoBlockRenderer<CharterStoneEntity> {
    AnimatedGeoModel<CharterStoneEntity> stone = new CharterStoneModel();
    public CharterStoneRenderer()
    {
        super(new CharterMarksModel());
    }

    //this is a terrible idea. this is not how this should be done. too bad I can't be bothered to work on this @*^&#$ anymore
    @Override
    public void render(CharterStoneEntity tile, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn) {
        GeoModel modelMarks = getGeoModelProvider().getModel(getGeoModelProvider().getModelResource(tile));
        getGeoModelProvider().setLivingAnimations(tile, this.getUniqueID(tile));
        stack.push();
        stack.translate(0, 0.01f, 0);
        stack.translate(0.5, 0, 0.5);

        MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureResource(tile));
        RenderLayer marksRenderType = getRenderType(tile, partialTicks, stack, bufferIn, null, 15728880,
                getTextureResource(tile));
        render(modelMarks, tile, partialTicks, marksRenderType, stack, bufferIn, null, 15728880, OverlayTexture.DEFAULT_UV,
                1, 1,
                1, 1);
        stack.pop();

        GeoModel model = stone.getModel(stone.getModelResource(tile));
        stack.push();
        stack.translate(0, 0.01f, 0);
        stack.translate(0.5, 0, 0.5);

        MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureResource(tile));
        Color renderColor = getRenderColor(tile, partialTicks, stack, bufferIn, null, packedLightIn);
        RenderLayer renderType = getRenderType(tile, partialTicks, stack, bufferIn, null, packedLightIn,
                getTextureResource(tile));
        render(model, tile, partialTicks, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV,
                (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        stack.pop();
    }

    @Override
    public RenderLayer getRenderType(CharterStoneEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureResource(animatable));
    }
}
