package net.arathain.charter.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface SendHelpComponent extends AutoSyncedComponent {

    List<Box> area = new ArrayList<>();
    List<UUID> members = new ArrayList<>();

    BlockPos getCharterStonePos();
    UUID getCharterOwnerUuid();

}
