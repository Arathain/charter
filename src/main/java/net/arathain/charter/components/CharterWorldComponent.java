package net.arathain.charter.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.arathain.charter.Charter;
import net.arathain.charter.block.CharterStoneBlock;
import net.arathain.charter.block.WaystoneBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
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
        charters.forEach(CharterComponent::tick);
        CharterComponents.CHARTERS.get(zaWarudo).getCharters().forEach(charter -> {
            if(zaWarudo.getBlockState(charter.getCharterStonePos()).getBlock() != Charter.CHARTER_STONE) {
                charter.getAreas().forEach(area -> {
                    BlockState state = zaWarudo.getBlockState(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z));
                    if(state.getBlock() instanceof WaystoneBlock) {
                        zaWarudo.breakBlock(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z), false);
                        zaWarudo.setBlockState(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z), Charter.BROKEN_WAYSTONE.getDefaultState());
                    }
                    if(state.getBlock() instanceof CharterStoneBlock) {
                        zaWarudo.breakBlock(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z), false);
                        zaWarudo.setBlockState(new BlockPos(area.getCenter().x, area.getCenter().y, area.getCenter().z), Charter.BROKEN_CHARTER_STONE.getDefaultState());
                    }
                });
                charter.getMembers().forEach(member -> {
                    PlayerEntity player = zaWarudo.getPlayerByUuid(member);
                    if(player != null) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 5));
                    }
                });
            }

                });
        CharterComponents.CHARTERS.get(zaWarudo).getCharters().removeIf(charter -> zaWarudo.getBlockState(charter.getCharterStonePos()).getBlock() != Charter.CHARTER_STONE);

        CharterComponents.CHARTERS.sync(zaWarudo);
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
