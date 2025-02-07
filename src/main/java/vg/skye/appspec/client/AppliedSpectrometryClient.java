package vg.skye.appspec.client;

import appeng.api.client.AEKeyRendering;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.init.client.InitScreens;
import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.PortableCellItem;
import appeng.menu.me.common.MEStorageMenu;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import vg.skye.appspec.AppliedSpectrometry;
import vg.skye.appspec.InkKey;
import vg.skye.appspec.InkKeyType;
import vg.skye.appspec.PortableInkCellItem;

public class AppliedSpectrometryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.ITEM.register(PortableCellItem::getColor,
                AppliedSpectrometry.PORTABLE_INK_CELL_1K,
                AppliedSpectrometry.PORTABLE_INK_CELL_4K,
                AppliedSpectrometry.PORTABLE_INK_CELL_16K,
                AppliedSpectrometry.PORTABLE_INK_CELL_64K,
                AppliedSpectrometry.PORTABLE_INK_CELL_256K);

        ColorProviderRegistry.ITEM.register(BasicStorageCell::getColor,
                AppliedSpectrometry.INK_CELL_1K,
                AppliedSpectrometry.INK_CELL_4K,
                AppliedSpectrometry.INK_CELL_16K,
                AppliedSpectrometry.INK_CELL_64K,
                AppliedSpectrometry.INK_CELL_256K);
    }

    public static void ae2Initialize() {
        InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(
                PortableInkCellItem.TYPE,
                MEStorageScreen::new,
                "/screens/terminals/portable_ink_cell.json");

        AEKeyRendering.register(InkKeyType.TYPE, InkKey.class, new InkRenderer());
    }
}
