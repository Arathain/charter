package net.arathain.charter.block.entity.render;

import net.arathain.charter.block.entity.CharterSentinelEntity;
import net.arathain.charter.block.entity.render.model.CharterSentinelModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CharterSentinelRenderer extends GeoEntityRenderer<CharterSentinelEntity> {
    public CharterSentinelRenderer(EntityRendererFactory.Context context) {
        super(context, new CharterSentinelModel());
    }
    @Override
    protected int getBlockLight(CharterSentinelEntity sentinel, BlockPos blockPos) {
        return 15;
    }

    @Override
    public RenderLayer getRenderType(CharterSentinelEntity animatable, float partialTicks, MatrixStack stack, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(this.getTexture(animatable));
    }

    @Override
    protected float getDeathMaxRotation(CharterSentinelEntity entityLivingBaseIn) {
        return 0f;
    }

    @Override
    protected int getSkyLight(CharterSentinelEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(CharterSentinelEntity entity, float entityYaw, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, 15728880);
    }
}
