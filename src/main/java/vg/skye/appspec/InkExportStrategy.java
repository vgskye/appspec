package vg.skye.appspec;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import de.dafuqs.spectrum.api.energy.InkStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class InkExportStrategy implements StackExportStrategy {
    private final ServerLevel level;
    private final BlockPos fromPos;
    private final Direction fromSide;

    public InkExportStrategy(ServerLevel level,
                             BlockPos fromPos,
                             Direction fromSide) {
        this.level = level;
        this.fromPos = fromPos;
        this.fromSide = fromSide;
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long maxAmount) {
        if (!(what instanceof InkKey))
            return 0;

        var be = level.getBlockEntity(fromPos);
        if (!(be instanceof InkStorageBlockEntity<?>))
            return 0;
        var storage = ((InkStorageBlockEntity<?>) be).getEnergyStorage();
        var color = ((InkKey) what).getColor();
        if (!storage.accepts(color))
            return 0;

        var remainingCapacity = storage.getRoom(color);
        var insertable = Math.min(maxAmount, remainingCapacity);
        var extracted = StorageHelper.poweredExtraction(context.getEnergySource(),
                context.getInternalStorage().getInventory(), what, insertable, context.getActionSource(),
                Actionable.MODULATE);

        if (extracted == 0) {
            return 0;
        }

        var overflow = InkWorkaround.addEnergyBugfix(storage, color, extracted);
        var inserted = extracted - overflow;
        if (extracted != inserted) {
            AppliedSpectrometry.LOGGER.error("Ink over-extracted despite checking capacity? (Extracted {}, inserted {})", extracted, inserted);
        }
        ((InkStorageBlockEntity<?>) be).setInkDirty();
        be.setChanged();
        return extracted;
    }

    @Override
    public long push(AEKey what, long maxAmount, Actionable mode) {
        if (!(what instanceof InkKey))
            return 0;

        var be = level.getBlockEntity(fromPos);
        if (!(be instanceof InkStorageBlockEntity<?>))
            return 0;
        var storage = ((InkStorageBlockEntity<?>) be).getEnergyStorage();
        var color = ((InkKey) what).getColor();
        if (!storage.accepts(color))
            return 0;

        if (mode == Actionable.SIMULATE) {
            var capacity = storage.getRoom(color);
            return Math.min(maxAmount, capacity);
        }

        var overflow = InkWorkaround.addEnergyBugfix(storage, color, maxAmount);
        var inserted = maxAmount - overflow;
        ((InkStorageBlockEntity<?>) be).setInkDirty();
        be.setChanged();
        return inserted;
    }
}
