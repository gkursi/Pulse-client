package xyz.qweru.pulse.client.render.world.blocks;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.Pulse3D;
import xyz.qweru.pulse.client.render.world.PulseBlock;

import java.awt.*;

public class FillAnimationBlock extends PulseBlock {
    public FillAnimationBlock(BlockPos pos, Color fill) {
        super(pos, fill);
    }

    public FillAnimationBlock(BlockPos pos, Color fill, Color outline) {
        super(pos, fill, outline);
    }

    public FillAnimationBlock(BlockPos pos, Color fill, Color outline, double fadeTime) {
        super(pos, fill, outline, fadeTime);
    }

    @Override
    public void render(MatrixStack matrices) {
        Pulse3D.renderThroughWalls();
        if(!animation.hasEnded()) {
            double size = animation.getDouble(0.5, 1);
            double invertedSize = Math.abs(1 - size);
            Vec3d pos = Vec3d.of(this.pos).add(new Vec3d(1, 1, 1).multiply(invertedSize));
            Vec3d dimensions = new Vec3d(1, 1, 1).multiply((0.5 - invertedSize) * 2);

            Color fill = Pulse2D.injectAlpha(this.fill, animation.getInt(40, this.fill.getAlpha()));
            Color outline = Pulse2D.injectAlpha(this.outline, animation.getInt(40, this.outline.getAlpha()));

            Pulse3D.renderEdged(matrices, fill, outline, pos, dimensions);
        } else {
            Pulse3D.renderEdged(matrices, fill, outline, Vec3d.of(pos), new Vec3d(1, 1, 1));
        }
        Pulse3D.stopRenderThroughWalls();
    }
}
