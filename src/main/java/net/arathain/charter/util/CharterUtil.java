package net.arathain.charter.util;

import net.arathain.charter.components.CharterComponent;
import net.arathain.charter.components.CharterComponents;
import net.arathain.charter.entity.SlowFallEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class CharterUtil {

    public static CharterComponent getCharterAtPos(Vec3d pos, World world) {
        CharterComponent component = null;
        List<CharterComponent> charters = CharterComponents.CHARTERS.get(world).getCharters();
        for (CharterComponent potentialComponent : charters) {
            List<Box> boxes = potentialComponent.getAreas();
            for (Box box : boxes) {
                if (box.contains(pos)) {
                    component = potentialComponent;
                }
            }
        }
        return component;
    }

    public static CharterComponent getCharterAtPos(BlockPos blockPos, World world) {
        CharterComponent component = null;
        List<CharterComponent> charters = new ArrayList<>(CharterComponents.CHARTERS.get(world).getCharters());
        for (CharterComponent potentialComponent : charters) {
            List<Box> boxes = new ArrayList<>(potentialComponent.getAreas());
            for (Box box : boxes) {
                if (box.contains(blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
                    component = potentialComponent;
                }
            }
        }
        return component;
    }
    public static boolean isInCharter(PlayerEntity player, World world) {
        List<CharterComponent> charters = CharterComponents.CHARTERS.get(world).getCharters();
        for (CharterComponent potentialComponent : charters) {
            List<UUID> memberList2 = new ArrayList<>(potentialComponent.getMembers());
            for(UUID uuid : memberList2) {
                if(uuid.equals(player.getUuid())) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isCharterOwner(LivingEntity player, World world) {
        if(player instanceof PlayerEntity) {
            List<CharterComponent> charters = CharterComponents.CHARTERS.get(world).getCharters();
            for (CharterComponent potentialComponent : charters) {
                if (potentialComponent.getCharterOwnerUuid().equals(player.getUuid())) {
                    return true;
                }
            }
        }
        return false;
    }
    public static void applySpeed(PlayerEntity player) {
        ((SlowFallEntity) player).setSlowFalling(false);
        Vec3d rotation = player.getRotationVector();
        Vec3d velocity = player.getVelocity();
        float speed = (0.02f * (player.getPitch() < -75 && player.getPitch() > -105 ? 3F : 1.5F));

        player.setVelocity(velocity.add(rotation.x * speed + (rotation.x * 1.5D - velocity.x) * speed,
                rotation.y * speed + (rotation.y * 1.5D - velocity.y) * speed,
                rotation.z * speed + (rotation.z * 1.5D - velocity.z) * speed));
    }
    public static void stopFlying(PlayerEntity player) {
        ((SlowFallEntity) player).setSlowFalling(true);

        if(player.getPitch() < -90 || player.getPitch() > 90) {
            float offset = (player.getPitch() < -90 ? player.getPitch() + 180 : player.getPitch() - 180) * 2;
            player.setPitch((player.getPitch() < -90 ? 180 + offset : -180 - offset) + player.getPitch());
            player.setYaw(180 + player.getYaw());
        }

        player.stopFallFlying();
    }

}
