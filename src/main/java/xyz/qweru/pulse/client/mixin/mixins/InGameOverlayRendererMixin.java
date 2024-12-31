package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.systems.events.HudRenderEvent;
import xyz.qweru.pulse.client.systems.modules.impl.render.NoRender;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true)
    private static void wall(Sprite sprite, MatrixStack matrices, CallbackInfo ci) {
        if(ModuleManager.INSTANCE.getItemByClass(NoRender.class).isEnabled() && ((NoRender) ModuleManager.INSTANCE.getItemByClass(NoRender.class)).overlays.isEnabled()) ci.cancel();
    }

    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void fire(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if(ModuleManager.INSTANCE.getItemByClass(NoRender.class).isEnabled() && ((NoRender) ModuleManager.INSTANCE.getItemByClass(NoRender.class)).overlays.isEnabled()) ci.cancel();
    }

    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
    private static void underwater(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if(ModuleManager.INSTANCE.getItemByClass(NoRender.class).isEnabled() && ((NoRender) ModuleManager.INSTANCE.getItemByClass(NoRender.class)).overlays.isEnabled()) ci.cancel();
    }
}
