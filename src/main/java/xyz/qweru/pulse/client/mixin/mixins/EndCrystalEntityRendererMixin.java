package xyz.qweru.pulse.client.mixin.mixins;

import me.x150.renderer.render.MSAAFramebuffer;
import me.x150.renderer.render.OutlineFramebuffer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.mixin.iinterface.IEndCrystalEntityRenderer;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.modules.impl.render.Chams;
import xyz.qweru.pulse.client.systems.modules.impl.render.CrystalTweaks;
import xyz.qweru.pulse.client.utils.render.ModelRenderer;
import xyz.qweru.pulse.client.utils.render.framebuffers.PulseOutlineFramebuffer;

import java.util.ArrayList;
import java.util.List;


@Mixin(EndCrystalEntityRenderer.class)
public abstract class EndCrystalEntityRendererMixin implements IEndCrystalEntityRenderer {

    @Shadow @Final private ModelPart frame;

    @Shadow @Final private ModelPart bottom;

    @Shadow @Final private ModelPart core;

    @Inject(
            method = "render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    "HEAD"
            )
    )
    void render(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
//        ModelRenderer.renderEndCrystal(endCrystalEntity, PulseClient.lastMatrices);
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
                    ordinal = 1
            )
    )
    void renderFrame$1(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
//        ModelRenderer.renderModelPart(matrixStack, this.frame, endCrystalEntity.getPos());
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
                    ordinal = 2
            )
    )
    void renderFrame$2(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
//        ModelRenderer.renderModelPart(matrixStack, this.frame, endCrystalEntity.getPos());
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
                    ordinal = 3
            )
    )
    void renderCore(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
//        ModelRenderer.renderModelPart(matrixStack, this.core, endCrystalEntity.getPos());
    }



    @ModifyArgs(method = "render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V", ordinal = 0))
    void maScale(Args args) {
        float pulse$scale = ModuleManager.INSTANCE.getItemByClass(CrystalTweaks.class).isEnabled() ?
                ((CrystalTweaks) ModuleManager.INSTANCE.getItemByClass(CrystalTweaks.class)).scale.getValue() : 1;

        args.set(0, 2.0F * pulse$scale);
        args.set(1, 2.0F * pulse$scale);
        args.set(2, 2.0F * pulse$scale);
    }

    @ModifyArgs(method = "render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;"))
    private void modifySpeed(Args args) {
        CrystalTweaks module = (CrystalTweaks) ModuleManager.INSTANCE.getItemByClass(CrystalTweaks.class);
        if (module == null || !module.isEnabled()) return;

        float speed = module.rotateSpeed.getValue();
        args.set(0, ((float) args.get(0)) * speed);
    }

    @Inject(method = "render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = {@At("HEAD")}, cancellable = true)
    public void render$chams(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        Chams chams = (Chams) ModuleManager.INSTANCE.getItemByClass(Chams.class);
        if(chams.isEnabled() && chams.endCrystal.isEnabled()) {
            ci.cancel();
            chams.renderCrystal(endCrystalEntity, f, g, matrixStack, i, core, frame);
        }
    }

    @Override
    public ModelPart pulse$getCore() {
        return core;
    }

    @Override
    public ModelPart pulse$getFrame() {
        return frame;
    }
}