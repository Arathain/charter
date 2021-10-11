package net.arathain.charter.mixin;

import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.state.property.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CandleBlock.class)
public abstract class CandleBlockMixin extends AbstractCandleBlock implements Waterloggable {
    protected CandleBlockMixin(Settings settings) {
        super(settings);
    }
}
