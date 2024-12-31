package xyz.qweru.pulse.client.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.BlockBreakingInfo;
import net.minecraft.particle.ParticleEffect;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.mixin.iinterface.IWorldRenderer;
import xyz.qweru.pulse.client.systems.events.ParticleSpawnEvent;
import xyz.qweru.pulse.client.systems.modules.impl.setting.Rainbows;

import java.awt.*;

import static xyz.qweru.pulse.client.PulseClient.mc;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements IWorldRenderer {

    @Shadow @Final private Int2ObjectMap<BlockBreakingInfo> blockBreakingInfos;

    @ModifyExpressionValue(method = "render", at = @At(value = "NEW", target = "net/minecraft/client/util/math/MatrixStack"))
    private MatrixStack setMatrixStack(MatrixStack matrixStack) {
        return matrixStack;
    }

    @Inject(method = "spawnParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
    void spawnParticle(ParticleEffect parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        if(PulseClient.Events.post(new ParticleSpawnEvent(parameters, alwaysSpawn, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ)).isCancelled()) cir.cancel();
    }

    @Inject(method = "renderLayer", at = @At("HEAD"), cancellable = true)
    void spawnParticle(RenderLayer renderLayer, double x, double y, double z, Matrix4f matrix4f, Matrix4f positionMatrix, CallbackInfo ci) {
        Color color = Rainbows.getRainbow(0, 0);
        if(Managers.MODULE.getItemByClass(Rainbows.class).isEnabled()) RenderSystem.setShaderColor((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, 1f);
    }

    @Override
    public Int2ObjectMap<BlockBreakingInfo> pulse$getBlockBreakingInfos() {
        return blockBreakingInfos;
    }
}
