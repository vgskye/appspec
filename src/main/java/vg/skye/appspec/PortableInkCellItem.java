package vg.skye.appspec;

import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.items.storage.StorageTier;
import appeng.items.tools.powered.PortableCellItem;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageItem;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class PortableInkCellItem extends PortableCellItem implements InkStorageItem<BasicCellInventoryInkStorage> {
    public static final MenuType<MEStorageMenu> TYPE = MenuTypeBuilder
            .<MEStorageMenu, IPortableTerminal>create(MEStorageMenu::new, IPortableTerminal.class)
            .build("portable_ink_cell");

    public PortableInkCellItem(StorageTier tier, Properties props) {
        super(InkKeyType.TYPE, 16, TYPE, tier, props, 0x6ec9ff);
    }

    @Override
    public Drainability getDrainability() {
        return Drainability.ALWAYS;
    }

    @Override
    public BasicCellInventoryInkStorage getEnergyStorage(ItemStack itemStack) {
        return new BasicCellInventoryInkStorage(itemStack.copy());
    }

    @Override
    public void setEnergyStorage(ItemStack itemStack, InkStorage storage) {
        if (storage instanceof BasicCellInventoryInkStorage basicCellInventoryInkStorage) {
            itemStack.setTag(basicCellInventoryInkStorage.getStack().getTag());
        }
    }
}
