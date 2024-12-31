package xyz.qweru.pulse.client.render.world.blocks;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.Pulse3D;
import xyz.qweru.pulse.client.render.world.PulseBlock;

import java.awt.*;
import java.util.function.Supplier;

public class ScaledBlock extends PulseBlock {
    private final Supplier<Double> scaleFunc;

    public ScaledBlock(BlockPos pos, Color fill, Supplier<Double> scaleFunc) {
        this(pos, fill, fill, scaleFunc);
    }

    public ScaledBlock(BlockPos pos, Color fill, Color outline, Supplier<Double> scaleFunc) {
        super(pos, fill, outline);
        this.scaleFunc = scaleFunc;
    }

    @Override
    public void render(MatrixStack matrices) {
        double size = scaleFunc.get();
        double invertedSize = Math.abs(1 - size);
        Vec3d pos = Vec3d.of(this.pos).add(new Vec3d(1, 1, 1).multiply(invertedSize));
        Vec3d dimensions = new Vec3d(1, 1, 1).multiply((0.5 - invertedSize) * 2);

        Color fill = Pulse2D.injectAlpha(this.fill, (int) (this.fill.getAlpha() * scaleFunc.get()));
        Color outline = Pulse2D.injectAlpha(this.outline, (int) (this.outline.getAlpha() * scaleFunc.get()));

        Pulse3D.renderEdged(matrices, fill, outline, pos, dimensions);
    }
}
