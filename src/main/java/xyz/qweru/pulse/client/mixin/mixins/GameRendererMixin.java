package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceFactory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.mixin.iinterface.IGameRenderer;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.systems.modules.impl.render.FreeCam;

@Mixin(GameRenderer.class)
public class GameRendererMixin implements IGameRenderer {

    @Shadow @Nullable private PostEffectProcessor blurPostProcessor;

    /**
     * @see GameRenderer#renderBlur(float)
     */
    @Override
    public void pulse$renderBlur(float delta, float amount, float direction) {
        if (this.blurPostProcessor != null && amount >= 1.0F) {
            this.blurPostProcessor.setUniforms("Radius", amount);
            if(direction != 0.5f) this.blurPostProcessor.setUniforms("BlurDir", direction);
            this.blurPostProcessor.render(delta);
        }
    }

    @Inject(method = "loadPrograms", at = @At("HEAD"))
    void initShaders(ResourceFactory factory, CallbackInfo ci) {
        Pulse2D.RECT_PROGRAM.use();
    }

    @Inject(method = "getFov", at =  @At("RETURN"), cancellable = true)
    private void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if (ModuleManager.INSTANCE.getItemByClass(FreeCam.class).isEnabled()) cir.setReturnValue((double) MinecraftClient.getInstance().options.getFov().getValue());
    }
}
