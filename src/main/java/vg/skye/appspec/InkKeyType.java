package vg.skye.appspec;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import de.dafuqs.spectrum.registries.SpectrumRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class InkKeyType extends AEKeyType {
    public static final Component INK = Component.translatable("gui.appspec.ink");
    public static final AEKeyType TYPE = new InkKeyType();

    private InkKeyType() {
        super(AppliedSpectrometry.id("ink"), InkKey.class, INK);
    }

    @Override
    public @Nullable AEKey readFromPacket(FriendlyByteBuf input) {
        var colorId = input.readResourceLocation();
        var color = SpectrumRegistries.INK_COLORS.get(colorId);
        return new InkKey(color);
    }

    @Override
    public @Nullable AEKey loadKeyFromTag(CompoundTag tag) {
        var colorId = new ResourceLocation(tag.getString("Color"));
        var color = SpectrumRegistries.INK_COLORS.get(colorId);
        return new InkKey(color);
    }

    @Override
    public int getAmountPerByte() {
        return 800;
    }

    @Override
    public int getAmountPerOperation() {
        return 100;
    }
}
