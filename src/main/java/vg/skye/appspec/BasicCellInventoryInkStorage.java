package vg.skye.appspec;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.me.cells.BasicCellInventory;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.color.InkColors;
import de.dafuqs.spectrum.registries.SpectrumRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import vg.skye.appspec.mixin.BasicCellInventoryMixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicCellInventoryInkStorage implements InkStorage {
    private final ItemStack stack;
    private final BasicCellInventory inventory;

    public BasicCellInventoryInkStorage(ItemStack stack) {
        this.stack = stack;
        this.inventory = BasicCellInventory.createInventory(stack, null);
    }

    @Override
    public boolean accepts(InkColor color) {
        return true;
    }

    @Override
    public long addEnergy(InkColor color, long amount) {
        var inserted = inventory.insert(new InkKey(color), amount, Actionable.MODULATE, IActionSource.empty());
        return amount - inserted;
    }

    @Override
    public long drainEnergy(InkColor color, long requestedAmount) {
        return inventory.extract(new InkKey(color), requestedAmount, Actionable.MODULATE, IActionSource.empty());
    }

    @Override
    public boolean requestEnergy(InkColor color, long requestedAmount) {
        var extractable = inventory.extract(new InkKey(color), requestedAmount, Actionable.SIMULATE, IActionSource.empty());
        if (extractable == requestedAmount) {
            inventory.extract(new InkKey(color), requestedAmount, Actionable.MODULATE, IActionSource.empty());
            return true;
        }
        return false;
    }

    @Override
    public long getEnergy(InkColor color) {
        var stacks = inventory.getAvailableStacks();
        return stacks.get(new InkKey(color));
    }

    @Override
    public Map<InkColor, Long> getEnergy() {
        var stacks = inventory.getAvailableStacks();
        var result = new HashMap<InkColor, Long>(stacks.size());
        for (var entry : stacks) {
            if (entry.getKey() instanceof InkKey inkKey) {
                result.put(inkKey.getColor(), entry.getLongValue());
            }
        }
        return result;
    }

    @Override
    public void setEnergy(Map<InkColor, Long> colors, long total) {
        var cellItems = ((BasicCellInventoryMixin) inventory).callGetCellItems();
        cellItems.clear();
        for (var entry : colors.entrySet()) {
            cellItems.put(new InkKey(entry.getKey()), (long) entry.getValue());
        }
        ((BasicCellInventoryMixin) inventory).callSaveChanges();
    }

    @Override
    public long getMaxPerColor() {
        return inventory.getTotalBytes() * InkKeyType.TYPE.getAmountPerByte();
    }

    @Override
    public long getMaxTotal() {
        return inventory.getTotalBytes() * InkKeyType.TYPE.getAmountPerByte();
    }

    @Override
    public long getCurrentTotal() {
        return inventory.getStoredItemCount();
    }

    @Override
    public boolean isEmpty() {
        return inventory.getStoredItemCount() == 0;
    }

    @Override
    public boolean isFull() {
        return inventory.getRemainingItemCount() == 0;
    }

    @Override
    public void fillCompletely() {
        var cellItems = ((BasicCellInventoryMixin) inventory).callGetCellItems();
        cellItems.clear();
        long inkColorCount = SpectrumRegistries.INK_COLORS.size();
        long usableBytes = inventory.getTotalBytes() - ((long) inventory.getBytesPerType() * inkColorCount);
        long energyPerColor = usableBytes / inkColorCount;
        for (InkColor color : InkColors.all()) {
            cellItems.put(new InkKey(color), energyPerColor);
        }
        ((BasicCellInventoryMixin) inventory).callSaveChanges();
    }

    @Override
    public void clearContent() {
        var cellItems = ((BasicCellInventoryMixin) inventory).callGetCellItems();
        cellItems.clear();
        ((BasicCellInventoryMixin) inventory).callSaveChanges();
    }

    @Override
    public void addTooltip(List<Component> tooltip) {
    }

    @Override
    public long getRoom(InkColor color) {
        return inventory.getRemainingItemCount();
    }

    public ItemStack getStack() {
        return stack;
    }
}
