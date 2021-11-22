package net.arathain.charter.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.arathain.charter.Charter;
import net.minecraft.util.Identifier;

public class CharterComponents implements WorldComponentInitializer {
    public static final ComponentKey<CharterComponent> CHARTER_COMPONENT_KEY =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(Charter.MODID, "charter_key"), CharterComponent.class);
    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(CHARTER_COMPONENT_KEY, CharterComponent::new);
    }
}
