package xyz.qweru.pulse.client.render.world.blocks;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.Pulse3D;
import xyz.qweru.pulse.client.render.world.PulseBlock;

import java.awt.*;

public class FadeOutBlock extends PulseBlock {
    public FadeOutBlock(BlockPos pos, Color fill) {
        super(pos, fill);
    }

    public FadeOutBlock(BlockPos pos, Color fill, Color outline) {
        super(pos, fill, outline);
    }

    public FadeOutBlock(BlockPos pos, Color fill, Color outline, double fadeTime) {
        super(pos, fill, outline, fadeTime);
    }

    @Override
    public void render(MatrixStack matrices) {
        Pulse3D.renderThroughWalls();
        if(!animation.hasEnded()) {
            Color fill = Pulse2D.injectAlpha(this.fill, animation.getInt(this.fill.getAlpha(), 0));
            Color outline = Pulse2D.injectAlpha(this.outline, animation.getInt(this.fill.getAlpha(), 0));
            Pulse3D.renderEdged(matrices, fill, outline, Vec3d.of(pos), new Vec3d(1, 1, 1));
        }
        Pulse3D.stopRenderThroughWalls();
    }

    public boolean hasFaded() {
        return animation.hasEnded();
    }
}
