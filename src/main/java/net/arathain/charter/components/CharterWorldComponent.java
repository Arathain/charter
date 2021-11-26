package net.arathain.charter.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.arathain.charter.Charter;
import net.arathain.charter.block.CharterStoneBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CharterWorldComponent implements AutoSyncedComponent {
    private final List<CharterComponent> charters = new ArrayList<>();
    private final World zaWarudo;

    public CharterWorldComponent(World world) {
        zaWarudo = world;
    }

    public void tick() {
        CharterComponents.CHARTERS.sync(zaWarudo);
        for(CharterComponent charter : charters) {
            charter.tick();
        }
        CharterComponents.CHARTERS.get(zaWarudo).getCharters().removeIf(charter -> !(zaWarudo.getBlockState(charter.getCharterStonePos()).getBlock() instanceof CharterStoneBlock));
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        NbtList list = tag.getList("charters", 10);

        list.forEach(charterCompound -> {
            UUID charterOwner = ((NbtCompound) charterCompound).getCompound(Charter.MODID).getUuid("CharterOwner");

            Optional<CharterComponent> charter = charters.stream().filter(existingCharter -> existingCharter.getCharterOwnerUuid() == charterOwner).findFirst();

            if (charter.isPresent()) {
                charter.get().readFromNbt((NbtCompound) charterCompound);
            } else {
                CharterComponent charterComponent = new CharterComponent(zaWarudo);

                charterComponent.readFromNbt((NbtCompound) charterCompound);

                charters.add(charterComponent);
            }
        });

    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList list = new NbtList();

        charters.forEach(charterComponent -> {
            NbtCompound charterNbt = new NbtCompound();

            charterComponent.writeToNbt(charterNbt);

            list.add(charterNbt);
        });

        tag.put("charters", list);

    }

    public List<CharterComponent> getCharters() {
        return charters;
    }

}
