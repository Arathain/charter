package net.arathain.charter.util;

import net.arathain.charter.components.CharterComponent;
import net.arathain.charter.components.CharterComponents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class CharterUtil {

    @Nullable
    public static CharterComponent getCharterAtPos(Vec3d pos, World world) {
        CharterComponent component = null;
        for(CharterComponent charterComponent : CharterComponents.CHARTERS.get(world).getCharters()) {
            for (Box box : charterComponent.getAreas()) {
                if(box.contains(pos)) {
                    component = charterComponent;
                }
            }
        }
        return component;
    }

    @Nullable
    public static CharterComponent getCharterAtPos(BlockPos blockPos, World world) {
        CharterComponent component = null;
        for(CharterComponent charterComponent : CharterComponents.CHARTERS.get(world).getCharters()) {
            for (Box box : charterComponent.getAreas()) {
                if(box.contains(blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
                    component = charterComponent;
                }
            }
        }
        return component;
    }
}
