package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.mixin.iinterface.IWorld;
import xyz.qweru.pulse.client.systems.modules.impl.render.NoRender;
import xyz.qweru.pulse.client.systems.modules.impl.render.Sky;

import java.awt.*;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin implements IWorld {

    @Shadow abstract PendingUpdateManager getPendingUpdateManager();

    @Override
    public PendingUpdateManager pulse$getPendingUpdateManager() {
        return this.getPendingUpdateManager();
    }

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    void skyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        Vec3d c = cir.getReturnValue();
        Color color = Sky.getSkyColor(new Color((float) c.x, (float) c.y, (float) c.z));
        c = new Vec3d((double) color.getRed() / 255, (double) color.getGreen() / 255, (double) color.getBlue() / 255);
        cir.setReturnValue(c);
    }

    @Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
    void spawnParticle(BlockPos pos, BlockState state, CallbackInfo ci) {
        NoRender render = (NoRender) Managers.MODULE.getItemByClass(NoRender.class);

        if(render.isEnabled() && render.blockBreakParticle.isEnabled()) ci.cancel();
    }
}
