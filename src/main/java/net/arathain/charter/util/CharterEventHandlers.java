package net.arathain.charter.util;

import net.arathain.charter.components.CharterComponent;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CharterEventHandlers {
    public static void init() {
        registerBreakBlockCallback();
        registerInteractBlockCallback();
    }

    private static void registerInteractBlockCallback() {
        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            CharterComponent component = CharterUtil.getCharterAtPos(blockHitResult.getPos(), world);
            return checkCharter(component, playerEntity);
        });

        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            CharterComponent component = CharterUtil.getCharterAtPos(blockHitResult.getPos(), world);
            return checkCharter(component, playerEntity);
        });
    }

    private static void registerBreakBlockCallback() {
        AttackBlockCallback.EVENT.register((playerEntity, world, hand, blockPos, direction) -> {
            CharterComponent component = CharterUtil.getCharterAtPos(blockPos, world);
            return checkCharter(component, playerEntity);
        });
        PlayerBlockBreakEvents.BEFORE.register((world, playerEntity, blockPos, state, blockEntity) -> {
            CharterComponent component = CharterUtil.getCharterAtPos(blockPos, world);
            ActionResult result = checkCharter(component, playerEntity);
            return !result.equals(ActionResult.FAIL);
        });
    }
    private static ActionResult checkCharter(CharterComponent charter, PlayerEntity player) {
        if(charter != null) {
            List<UUID> memberList2 = new ArrayList<>(charter.getMembers());
            for(UUID uuid : memberList2) {
                if(uuid.equals(player.getUuid())) {
                    return ActionResult.PASS;
                }
            }
            return ActionResult.FAIL;
        } else {
            return ActionResult.PASS;
        }

    }
}
