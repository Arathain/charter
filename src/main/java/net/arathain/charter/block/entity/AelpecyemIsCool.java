package net.arathain.charter.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface AelpecyemIsCool extends Inventory {
    static AelpecyemIsCool of(DefaultedList<ItemStack> items) {
        return () -> items;
    }

    static AelpecyemIsCool ofSize(int size) {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
    }

    DefaultedList<ItemStack> getItems();

    @Override
    default int size() {
        return getItems().size();
    }

    @Override
    default boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the item in the slot.- Aelpecyem
     */
    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    @Override
    default void markDirty() {

    }

    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    /**
     * Removes the current stack in the {@code slot} and returns it. - Aelpecyem
     */
    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    /**
     * Replaces the current stack in the {@code slot} with the provided stack.
     * <p>If the stack is too big for this inventory ({@link Inventory#getMaxCountPerStack()}),
     * it gets resized to this inventory's maximum amount. - Aelpecyem
     */
    @Override
    default void setStack(int slot, ItemStack stack) {
        if (isValid(slot, stack)) {
            getItems().set(slot, stack.split(getMaxCountPerStack()));
        }
        markDirty();
    }

    /**
     * Clears {@linkplain #getItems() the item list}}. - Aelpecyem
     */
    @Override
    default void clear() {
        for (int i = 0; i < size(); i++) {
            setStack(i, ItemStack.EMPTY);
        }
        markDirty();
    }

    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    default ItemStack getStack(Item item) {
        for (int i = 0; i < size(); i++) {
            if (item.equals(getStack(i).getItem())) {
                return getStack(i);
            }
        }
        return ItemStack.EMPTY;
    }

}
