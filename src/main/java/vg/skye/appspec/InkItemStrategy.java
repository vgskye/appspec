package vg.skye.appspec;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import de.dafuqs.spectrum.api.energy.InkStorageItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InkItemStrategy implements ContainerItemStrategy<InkKey, ItemStack> {
    @Override
    public @Nullable GenericStack getContainedStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        if (!(stack.getItem() instanceof InkStorageItem<?> item)) {
            return null;
        }
        var colors = item.getEnergyStorage(stack).getEnergy();
        for (var entry : colors.entrySet()) {
            if (entry.getValue() != 0) {
                return new GenericStack(new InkKey(entry.getKey()), entry.getValue());
            }
        }
        return null;
    }

    @Override
    public @Nullable ItemStack findCarriedContext(Player player, AbstractContainerMenu menu) {
        var stack = menu.getCarried();
        if (stack.isEmpty() || stack.getCount() != 1) {
            return null;
        }
        if (!(stack.getItem() instanceof InkStorageItem<?>)) {
            return null;
        }
        return stack;
    }

    @Override
    public @Nullable ItemStack findPlayerSlotContext(Player player, int slot) {
        var stack = player.getSlot(slot).get();
        if (stack.isEmpty() || stack.getCount() != 1) {
            return null;
        }
        if (!(stack.getItem() instanceof InkStorageItem<?>)) {
            return null;
        }
        return stack;
    }

    @Override
    public long extract(ItemStack context, InkKey what, long amount, Actionable mode) {
        if (context.isEmpty() || context.getCount() != 1) {
            return 0;
        }
        if (!(context.getItem() instanceof InkStorageItem<?> item)) {
            return 0;
        }
        var colors = item.getEnergyStorage(context);
        if (mode == Actionable.SIMULATE) {
            return Math.min(colors.getEnergy(what.getColor()), amount);
        }
        var drained = colors.drainEnergy(what.getColor(), amount);
        item.setEnergyStorage(context, colors);
        return drained;
    }

    @Override
    public long insert(ItemStack context, InkKey what, long amount, Actionable mode) {
        if (context.isEmpty() || context.getCount() != 1) {
            return 0;
        }
        if (!(context.getItem() instanceof InkStorageItem<?> item)) {
            return 0;
        }
        var colors = item.getEnergyStorage(context);
        if (mode == Actionable.SIMULATE) {
            return Math.min(colors.getRoom(what.getColor()), amount);
        }
        var overflow = InkWorkaround.addEnergyBugfix(colors, what.getColor(), amount);
        item.setEnergyStorage(context, colors);
        return amount - overflow;
    }

    @Override
    public void playFillSound(Player player, InkKey what) {
    }

    @Override
    public void playEmptySound(Player player, InkKey what) {
    }

    @Override
    public @Nullable GenericStack getExtractableContent(ItemStack context) {
        return getContainedStack(context);
    }
}
