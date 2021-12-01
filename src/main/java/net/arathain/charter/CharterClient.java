package net.arathain.charter;

import net.arathain.charter.block.entity.CharterSentinelEntity;
import net.arathain.charter.block.entity.render.CharterSentinelRenderer;
import net.arathain.charter.block.entity.render.CharterStoneRenderer;
import net.arathain.charter.block.entity.render.WaystoneRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public class CharterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(Charter.CHARTER_STONE_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new CharterStoneRenderer());
        BlockEntityRendererRegistry.INSTANCE.register(Charter.WAYSTONE_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new WaystoneRenderer());
        EntityRendererRegistry.INSTANCE.register((Charter.SENTINEL), CharterSentinelRenderer::new);
    }
}
