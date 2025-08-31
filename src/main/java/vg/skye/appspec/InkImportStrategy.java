package vg.skye.appspec;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageBlockEntity;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class InkImportStrategy implements StackImportStrategy {
    private final ServerLevel level;
    private final BlockPos fromPos;
    private final Direction fromSide;

    public InkImportStrategy(ServerLevel level,
                             BlockPos fromPos,
                             Direction fromSide) {
        this.level = level;
        this.fromPos = fromPos;
        this.fromSide = fromSide;
    }

    @Override
    public boolean transfer(StackTransferContext context) {
        if (!context.isKeyTypeEnabled(InkKeyType.TYPE)) {
            return false;
        }

        var maxTransfer = InkKeyType.TYPE.getAmountPerOperation() * (long) context.getOperationsRemaining();
        var transferred = 0L;
        var be = level.getBlockEntity(fromPos);
        if (!(be instanceof InkStorageBlockEntity<?>))
            return false;
        var storage = ((InkStorageBlockEntity<?>) be).getEnergyStorage();
        for (InkColor inkColor : storage.getEnergy().keySet()) {
            var key = new InkKey(inkColor);
            if (context.isInFilter(key) == context.isInverted()) {
                continue;
            }

            var extracted = storage.drainEnergy(inkColor, maxTransfer);
            var inserted = context.getInternalStorage().getInventory().insert(key, extracted, Actionable.MODULATE, context.getActionSource());
            var toRefund = extracted - inserted;
            var refunded = toRefund - InkWorkaround.addEnergyBugfix(storage, inkColor, toRefund);
            if (refunded != toRefund) {
                AppliedSpectrometry.LOGGER.error("Failed to correctly refund over-extracted ink? (Wanted to refund {}, refunded {})", toRefund, refunded);
            }
            maxTransfer -= inserted;
            transferred += inserted;
        }
        ((InkStorageBlockEntity<?>) be).setInkDirty();
        be.setChanged();

        var opsUsed = Math.max(1, transferred / InkKeyType.TYPE.getAmountPerOperation());
        context.reduceOperationsRemaining(opsUsed);
        return true;
    }
}
