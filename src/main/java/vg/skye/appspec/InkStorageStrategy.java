package vg.skye.appspec;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.core.localization.GuiText;
import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.InkStorageBlockEntity;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class InkStorageStrategy implements ExternalStorageStrategy {
    private final ServerLevel level;
    private final BlockPos fromPos;
    private final Direction fromSide;

    public InkStorageStrategy(ServerLevel level,
                             BlockPos fromPos,
                             Direction fromSide) {
        this.level = level;
        this.fromPos = fromPos;
        this.fromSide = fromSide;
    }

    @Override
    public @Nullable MEStorage createWrapper(boolean extractableOnly, Runnable injectOrExtractCallback) {
        var be = level.getBlockEntity(fromPos);
        if (be instanceof InkStorageBlockEntity<?> isbe) {
            return new InkStorageAdapter(isbe, injectOrExtractCallback);
        }
        return null;
    }

    private record InkStorageAdapter(InkStorageBlockEntity<? extends InkStorage> storage, Runnable injectOrExtractCallback) implements MEStorage {
        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (!(what instanceof InkKey))
                return 0;
            if (mode == Actionable.SIMULATE) {
                return Math.min(amount, storage.getEnergyStorage().getRoom(((InkKey) what).getColor()));
            }
            var inserted = storage.getEnergyStorage().addEnergy(((InkKey) what).getColor(), amount);
            if (inserted > 0) {
                storage.setInkDirty();
                ((BlockEntity) storage).setChanged();
                injectOrExtractCallback.run();
            }
            return inserted;
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (!(what instanceof InkKey))
                return 0;
            if (mode == Actionable.SIMULATE) {
                return Math.min(amount, storage.getEnergyStorage().getEnergy(((InkKey) what).getColor()));
            }
            var drained = storage.getEnergyStorage().drainEnergy(((InkKey) what).getColor(), amount);
            if (drained > 0) {
                storage.setInkDirty();
                ((BlockEntity) storage).setChanged();
                injectOrExtractCallback.run();
            }
            return drained;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            for (Map.Entry<InkColor, Long> entry : storage.getEnergyStorage().getEnergy().entrySet()) {
                out.add(new InkKey(entry.getKey()), entry.getValue());
            }
        }

        @Override
        public Component getDescription() {
            return GuiText.ExternalStorage.text(InkKeyType.TYPE.getDescription());
        }
    }
}
