package net.arathain.charter.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.arathain.charter.util.CharterUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class CharterOwnerComponent implements AutoSyncedComponent {
    private final PlayerEntity obj;
    private boolean shade = false;

    public CharterOwnerComponent(PlayerEntity obj) {
        this.obj = obj;
    }

    public boolean isShade() {
        return shade;
    }

    public void setShade(boolean shade) {
        if(CharterUtil.isCharterOwner(obj, obj.getWorld())) {
            this.shade = shade;
            CharterComponents.CHARTER_OWNER_COMPONENT.sync(obj);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        setShade(tag.getBoolean("shade"));
    }
    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("shade", isShade());
    }
}
