package vg.skye.appspec.mixin;

import appeng.api.stacks.AEKey;
import appeng.me.cells.BasicCellInventory;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BasicCellInventory.class)
public interface BasicCellInventoryMixin {
    @Invoker
    Object2LongMap<AEKey> callGetCellItems();
    @Invoker
    void callSaveChanges();
}
