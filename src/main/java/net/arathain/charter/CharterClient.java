package net.arathain.charter;

import net.arathain.charter.block.entity.render.CharterStoneRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public class CharterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(Charter.CHARTER_STONE_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new CharterStoneRenderer());
    }
}
