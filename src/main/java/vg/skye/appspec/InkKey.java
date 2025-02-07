package vg.skye.appspec;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import de.dafuqs.spectrum.api.energy.color.InkColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class InkKey extends AEKey {
    private final InkColor color;

    public InkKey(InkColor color) {
        this.color = color;
    }

    @Override
    public AEKeyType getType() {
        return InkKeyType.TYPE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag() {
        var tag = new CompoundTag();
        tag.putString("Color", color.getID().toString());
        return tag;
    }

    @Override
    public Object getPrimaryKey() {
        return color;
    }

    @Override
    public ResourceLocation getId() {
        return color.getID();
    }

    @Override
    public void writeToPacket(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(color.getID());
    }

    @Override
    protected Component computeDisplayName() {
        return Component.translatable("ink.suffix", color.getName());
    }

    @Override
    public void addDrops(long l, List<ItemStack> list, Level level, BlockPos blockPos) {
    }

    public InkColor getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InkKey inkKey = (InkKey) o;
        return Objects.equals(color, inkKey.color);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(color);
    }
}
