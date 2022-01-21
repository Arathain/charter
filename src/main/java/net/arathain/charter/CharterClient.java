package net.arathain.charter;

import com.mojang.serialization.Codec;
import net.arathain.charter.block.entity.render.CharterStoneRenderer;
import net.arathain.charter.block.entity.render.WaystoneRenderer;
import net.arathain.charter.block.particle.BindingAmbienceParticle;
import net.arathain.charter.block.particle.BindingAmbienceParticleEffect;
import net.arathain.charter.components.packet.UpdateShadePacket;
import net.arathain.charter.entity.SlowFallEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

public class CharterClient implements ClientModInitializer {
    public static ParticleType<BindingAmbienceParticleEffect> BINDING;
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(Charter.CHARTER_STONE_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new CharterStoneRenderer());
        BlockEntityRendererRegistry.INSTANCE.register(Charter.WAYSTONE_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new WaystoneRenderer());
        BINDING = Registry.register(Registry.PARTICLE_TYPE, "charter:bound", new ParticleType<BindingAmbienceParticleEffect>(true, BindingAmbienceParticleEffect.PARAMETERS_FACTORY) {
            @Override
            public Codec<BindingAmbienceParticleEffect> getCodec() {
                return BindingAmbienceParticleEffect.CODEC;
            }
        });
        ParticleFactoryRegistry.getInstance().register(BINDING, BindingAmbienceParticle.Factory::new);
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                UpdateShadePacket.send(player.getPose() == EntityPose.FALL_FLYING || ((SlowFallEntity) player).isSlowFalling());

            }
        });
    }
}
