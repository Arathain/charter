package net.arathain.charter.components.packet;

import io.netty.buffer.Unpooled;
import net.arathain.charter.Charter;
import net.arathain.charter.components.CharterComponents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class UpdateShadePacket {
    public static final Identifier ID = new Identifier(Charter.MODID, "charter_owner");

    public static void send(boolean shade) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(shade);
        ClientPlayNetworking.send(ID, buf);
    }

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
        boolean shade = buf.readBoolean();
        server.execute(() -> {
            CharterComponents.CHARTER_OWNER_COMPONENT.get(player).setShade(shade);
        });
    }
}
