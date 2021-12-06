package net.arathain.charter.util;

import net.arathain.charter.components.CharterComponent;
import net.arathain.charter.components.CharterComponents;
import net.minecraft.entity.player.PlayerEntity;
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
}
