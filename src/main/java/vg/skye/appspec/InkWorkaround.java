package vg.skye.appspec;

import de.dafuqs.spectrum.api.energy.InkStorage;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import de.dafuqs.spectrum.api.energy.storage.SingleInkStorage;

public class InkWorkaround {
    // Returns the overflow of trying to insert ink into the item
    // SingleInkStorage.addEnergy incorrectly returns the amount of ink stored instead of overflow when item (e.g. flask) is empty
    public static long addEnergyBugfix(InkStorage storage, InkColor color, long amount) {
        var overflowBug = storage instanceof SingleInkStorage && storage.isEmpty();
        var overflow = storage.addEnergy(color, amount);
        return overflowBug ? 0 : overflow;
    }
}
