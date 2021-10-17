package net.arathain.charter.item;

import net.arathain.charter.entity.Bindable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class ContractItem extends Item {

    public ContractItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack stack = user.getStackInHand(hand);
        if(!isViable(stack)) {
            user.setStackInHand(hand, putContract(stack, user));
            ((Bindable) user).setIndebted(true);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (isViable(stack)) {
            tooltip.add(new LiteralText(getIndebtedName(stack)).formatted(Formatting.GRAY));

        }
    }

    public static ItemStack putContract(ItemStack stack, PlayerEntity entity) {
        stack.getOrCreateNbt().putUuid("IndebtedUUID", entity.getUuid());
        stack.getOrCreateNbt().putString("IndebtedName", entity.getDisplayName().getString());
        return stack;
    }

    public static ItemStack copyTo(ItemStack from, ItemStack to) {
        if (isViable(from)) {
            to.getOrCreateNbt().putUuid("IndebtedUUID", from.getOrCreateNbt().getUuid("IndebtedUUID"));
            to.getOrCreateNbt().putString("IndebtedName", from.getOrCreateNbt().getString("IndebtedName"));
        }
        return to;
    }

    public static boolean isViable(ItemStack stack) {
        return stack.hasNbt() && stack.getOrCreateNbt().contains("IndebtedUUID");
    }

    public static void removeDebt(ItemStack stack) {
        if (stack.hasNbt()) {
            stack.getOrCreateNbt().remove("IndebtedUUID");
            stack.getOrCreateNbt().remove("IndebtedName");
        }
    }

    public static UUID getIndebtedUUID(ItemStack stack) {
        if (isViable(stack)) {
            return stack.getOrCreateNbt().getUuid("IndebtedUUID");
        }
        return null;
    }

    public static String getIndebtedName(ItemStack stack) {
        if (isViable(stack)) {
            return stack.getOrCreateNbt().getString("IndebtedName");
        }
        return "";
    }

}
