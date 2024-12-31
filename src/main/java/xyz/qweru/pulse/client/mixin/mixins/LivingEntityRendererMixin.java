package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.systems.modules.impl.render.Chams;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow @Final protected List<FeatureRenderer<T, M>> features;

    @Shadow protected M model;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    void render(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        Chams chams = ((Chams) ModuleManager.INSTANCE.getItemByClass(Chams.class));
        if (livingEntity instanceof PlayerEntity pe && chams.isEnabled() && chams.players.isEnabled()) {
            chams.renderPlayer(pe, f, g, matrixStack, i, model, ci);
            if (!pe.isSpectator()) {
                float n;
                Direction direction;
                Entity entity;
                matrixStack.push();
                float h = MathHelper.lerpAngleDegrees(g, pe.prevBodyYaw, pe.bodyYaw);
                float j = MathHelper.lerpAngleDegrees(g, pe.prevHeadYaw, pe.headYaw);
                float k = j - h;
                if (pe.hasVehicle() && (entity = pe.getVehicle()) instanceof LivingEntity) {
                    LivingEntity livingEntity2 = (LivingEntity) entity;
                    h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
                    k = j - h;
                    float l = MathHelper.wrapDegrees(k);
                    if (l < -85.0f) {
                        l = -85.0f;
                    }
                    if (l >= 85.0f) {
                        l = 85.0f;
                    }
                    h = j - l;
                    if (l * l > 2500.0f) {
                        h += l * 0.2f;
                    }
                    k = j - h;
                }
                float m = MathHelper.lerp(g, pe.prevPitch, pe.getPitch());
                if (LivingEntityRenderer.shouldFlipUpsideDown(pe)) {
                    m *= -1.0f;
                    k *= -1.0f;
                }
                if (pe.isInPose(EntityPose.SLEEPING) && (direction = pe.getSleepingDirection()) != null) {
                    n = pe.getEyeHeight(EntityPose.STANDING) - 0.1f;
                    matrixStack.translate((float) (-direction.getOffsetX()) * n, 0.0f, (float) (-direction.getOffsetZ()) * n);
                }
                float l = pe.age + g;
                chams.setupTransforms1(pe, matrixStack, l, h, g);
                matrixStack.scale(-1.0f, -1.0f, 1.0f);
                matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
                matrixStack.translate(0.0f, -1.501f, 0.0f);
                n = 0.0f;
                float o = 0.0f;
                if (!pe.hasVehicle() && pe.isAlive()) {
                    n = pe.limbAnimator.getSpeed(g);
                    o = pe.limbAnimator.getPos(g);
                    if (pe.isBaby())
                        o *= 3.0f;

                    if (n > 1.0f)
                        n = 1.0f;
                }

                for (FeatureRenderer<T, M> featureRenderer : features) {
                    featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntity, o, n, g, l, k, m);
                }
                matrixStack.pop();
            }
        }
    }

}
