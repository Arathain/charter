package net.arathain.charter.components;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface SendHelpComponent {

    List<Box> area = new ArrayList<>();
    List<UUID> members = new ArrayList<>();

    BlockPos getCharterStonePos();
    UUID getCharterOwnerUuid();

}
