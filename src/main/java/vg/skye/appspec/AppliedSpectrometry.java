package vg.skye.appspec;

import appeng.api.behaviors.ContainerItemStrategies;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.StorageCellModels;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.upgrades.Upgrades;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.items.storage.BasicStorageCell;
import appeng.items.storage.StorageTier;
import appeng.parts.automation.StackWorldBehaviors;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AppliedSpectrometry {
	public static final String MOD_ID = "appspec";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Item INK_CELL_HOUSING = item(new Item(new Item.Properties()), "ink_cell_housing");

	public static final Item INK_CELL_1K = item(inkCell(StorageTier.SIZE_1K), "ink_storage_cell_1k");
	public static final Item INK_CELL_4K = item(inkCell(StorageTier.SIZE_4K), "ink_storage_cell_4k");
	public static final Item INK_CELL_16K = item(inkCell(StorageTier.SIZE_16K), "ink_storage_cell_16k");
	public static final Item INK_CELL_64K = item(inkCell(StorageTier.SIZE_64K), "ink_storage_cell_64k");
	public static final Item INK_CELL_256K = item(inkCell(StorageTier.SIZE_256K), "ink_storage_cell_256k");

	public static final Item PORTABLE_INK_CELL_1K = item(portableInkCell(StorageTier.SIZE_1K), "portable_ink_cell_1k");
	public static final Item PORTABLE_INK_CELL_4K = item(portableInkCell(StorageTier.SIZE_4K), "portable_ink_cell_4k");
	public static final Item PORTABLE_INK_CELL_16K = item(portableInkCell(StorageTier.SIZE_16K), "portable_ink_cell_16k");
	public static final Item PORTABLE_INK_CELL_64K = item(portableInkCell(StorageTier.SIZE_64K), "portable_ink_cell_64k");
	public static final Item PORTABLE_INK_CELL_256K = item(portableInkCell(StorageTier.SIZE_256K), "portable_ink_cell_256k");

	private static final ResourceLocation MODEL_CELL_INK_1K = id("block/drive/cells/1k_ink_cell");
	private static final ResourceLocation MODEL_CELL_INK_4K = id("block/drive/cells/4k_ink_cell");
	private static final ResourceLocation MODEL_CELL_INK_16K = id("block/drive/cells/16k_ink_cell");
	private static final ResourceLocation MODEL_CELL_INK_64K = id("block/drive/cells/64k_ink_cell");
	private static final ResourceLocation MODEL_CELL_INK_256K = id("block/drive/cells/256k_ink_cell");

	public static final CreativeModeTab CREATIVE_TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id("tab"),
			FabricItemGroup.builder()
					.icon(() -> new ItemStack(INK_CELL_256K))
					.title(Component.translatable("itemGroup.appspec.tab"))
					.displayItems((context, entries) -> {
						entries.accept(INK_CELL_HOUSING);
						entries.accept(INK_CELL_1K);
						entries.accept(INK_CELL_4K);
						entries.accept(INK_CELL_16K);
						entries.accept(INK_CELL_64K);
						entries.accept(INK_CELL_256K);
						entries.accept(PORTABLE_INK_CELL_1K);
						entries.accept(PORTABLE_INK_CELL_4K);
						entries.accept(PORTABLE_INK_CELL_16K);
						entries.accept(PORTABLE_INK_CELL_64K);
						entries.accept(PORTABLE_INK_CELL_256K);
					})
					.build());

	private static Item portableInkCell(StorageTier tier) {
		return new PortableInkCellItem(tier, new Item.Properties().stacksTo(1));
	}

	private static Item inkCell(StorageTier tier) {
		return new BasicStorageCell(new Item.Properties().stacksTo(1), tier.componentSupplier().get(), INK_CELL_HOUSING, tier.idleDrain(), tier.bytes() / 1024, tier.bytes() / 128, 16, InkKeyType.TYPE);
	}

	private static Item item(Item item, String id) {
		return Registry.register(BuiltInRegistries.ITEM, id(id), item);
	}

	public static void ae2Initialize() {
		Registry.register(BuiltInRegistries.MENU, AppEng.makeId("portable_ink_cell"), PortableInkCellItem.TYPE);
		AEKeyTypes.register(InkKeyType.TYPE);
		StackWorldBehaviors.registerImportStrategy(InkKeyType.TYPE, InkImportStrategy::new);
		StackWorldBehaviors.registerExportStrategy(InkKeyType.TYPE, InkExportStrategy::new);
		StackWorldBehaviors.registerExternalStorageStrategy(InkKeyType.TYPE, InkStorageStrategy::new);
		ContainerItemStrategies.register(InkKeyType.TYPE, InkKey.class, new InkItemStrategy());
		GenericSlotCapacities.register(InkKeyType.TYPE, 6400L);

		String storageCellGroup = GuiText.StorageCells.getTranslationKey();
		String portableCellGroup = GuiText.PortableCells.getTranslationKey();

		var itemCells = List.of(
				INK_CELL_1K,
				INK_CELL_4K,
				INK_CELL_16K,
				INK_CELL_64K,
				INK_CELL_256K
		);
		for (var itemCell : itemCells) {
			Upgrades.add(AEItems.FUZZY_CARD, itemCell, 1, storageCellGroup);
			Upgrades.add(AEItems.INVERTER_CARD, itemCell, 1, storageCellGroup);
			Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, itemCell, 1, storageCellGroup);
			Upgrades.add(AEItems.VOID_CARD, itemCell, 1, storageCellGroup);
		}

		var portableCells = List.of(
				PORTABLE_INK_CELL_1K,
				PORTABLE_INK_CELL_4K,
				PORTABLE_INK_CELL_16K,
				PORTABLE_INK_CELL_64K,
				PORTABLE_INK_CELL_256K
		);
		for (var portableCell : portableCells) {
			Upgrades.add(AEItems.FUZZY_CARD, portableCell, 1, portableCellGroup);
			Upgrades.add(AEItems.INVERTER_CARD, portableCell, 1, portableCellGroup);
			Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, portableCell, 1, portableCellGroup);
			Upgrades.add(AEItems.VOID_CARD, portableCell, 1, portableCellGroup);
			Upgrades.add(AEItems.ENERGY_CARD, portableCell, 2, portableCellGroup);
		}

		StorageCellModels.registerModel(INK_CELL_1K, MODEL_CELL_INK_1K);
		StorageCellModels.registerModel(INK_CELL_4K, MODEL_CELL_INK_4K);
		StorageCellModels.registerModel(INK_CELL_16K, MODEL_CELL_INK_16K);
		StorageCellModels.registerModel(INK_CELL_64K, MODEL_CELL_INK_64K);
		StorageCellModels.registerModel(INK_CELL_256K, MODEL_CELL_INK_256K);

		StorageCellModels.registerModel(PORTABLE_INK_CELL_1K, MODEL_CELL_INK_1K);
		StorageCellModels.registerModel(PORTABLE_INK_CELL_4K, MODEL_CELL_INK_4K);
		StorageCellModels.registerModel(PORTABLE_INK_CELL_16K, MODEL_CELL_INK_16K);
		StorageCellModels.registerModel(PORTABLE_INK_CELL_64K, MODEL_CELL_INK_64K);
		StorageCellModels.registerModel(PORTABLE_INK_CELL_256K, MODEL_CELL_INK_256K);
	}

	public static ResourceLocation id(String name) {
		return new ResourceLocation(MOD_ID, name);
	}
}