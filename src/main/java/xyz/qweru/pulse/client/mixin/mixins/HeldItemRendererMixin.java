package xyz.qweru.pulse.client.mixin.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.events.HeldItemRenderEvent;
import xyz.qweru.pulse.client.systems.modules.impl.render.Chams;
import xyz.qweru.pulse.client.systems.modules.impl.render.NoRender;
import xyz.qweru.pulse.client.systems.modules.impl.render.Shaders;
import xyz.qweru.pulse.client.systems.modules.impl.render.ViewModel;
import xyz.qweru.pulse.client.systems.modules.impl.setting.Rainbows;
import xyz.qweru.pulse.client.utils.render.shaders.Shader;
import xyz.qweru.pulse.client.utils.render.shaders.ShaderManager;

import java.awt.*;

import static xyz.qweru.pulse.client.PulseClient.mc;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow public abstract void renderItem(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);

    @Shadow private ItemStack mainHand;

    @Shadow private ItemStack offHand;

    @Shadow @Final private MinecraftClient client;

    @Shadow private float equipProgressMainHand;

    @Shadow private float equipProgressOffHand;

    @Shadow private float prevEquipProgressMainHand;

    @Shadow private float prevEquipProgressOffHand;

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    void renderEvent(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if(PulseClient.Events.post(new HeldItemRenderEvent(matrices, item, equipProgress, tickDelta, player, swingProgress, hand)).isCancelled()) ci.cancel();
    }

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    void render(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if(ModuleManager.INSTANCE.getItemByClass(ViewModel.class).isEnabled()) {
            switch (hand) {
                case MAIN_HAND ->
                        ((ViewModel) ModuleManager.INSTANCE.getItemByClass(ViewModel.class)).renderMainhand(matrices);
                case OFF_HAND ->
                        ((ViewModel) ModuleManager.INSTANCE.getItemByClass(ViewModel.class)).renerOffhand(matrices);
            }

            ((ViewModel) ModuleManager.INSTANCE.getItemByClass(ViewModel.class)).renderAnimation(item, matrices);
        }
    }

    @ModifyArgs(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    void a(Args args) {
//        if(Managers.SLOT.getRealSlot() != -1) {
//            args.set(1, client.player.getInventory().getStack(Managers.SLOT.getRealSlot()));
//        }
    }

    @ModifyArgs(method = "applyEatOrDrinkTransformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0))
    void eatAnim$translate(Args args) {
        NoRender noRender = (NoRender) Managers.MODULE.getItemByClass(NoRender.class);
        if(noRender.isEnabled() && noRender.eat.isEnabled()) args.set(1, 0f);
    }

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    void render2(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Chams chams = (Chams) ModuleManager.INSTANCE.getItemByClass(Chams.class);
        if(chams.isEnabled() && chams.heldItems.isEnabled()) {
            if(chams.rainbow.isEnabled()) {
                Color c = Rainbows.getRainbow(0, 0);
                RenderSystem.setShaderColor((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, chams.itemColor.getAlpha().asFloat());
            }
            else RenderSystem.setShaderColor(chams.itemColor.getRed().asFloat(), chams.itemColor.getGreen().asFloat(), chams.itemColor.getBlue().asFloat(), chams.itemColor.getAlpha().asFloat());
            if(chams.itemTextureMode.is("Custom")) RenderSystem.setShaderTexture(0, chams.getTextureID());
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    void render2$renderItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Chams chams = (Chams) ModuleManager.INSTANCE.getItemByClass(Chams.class);
        if(chams.isEnabled() && chams.heldItems.isEnabled()) {
            if(chams.itemTextureMode.is("Custom")) RenderSystem.setShaderTexture(0, chams.getTextureID());
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFFLnet/minecraft/util/Arm;)V"))
    void render2$renderArmHoldingItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Chams chams = (Chams) ModuleManager.INSTANCE.getItemByClass(Chams.class);
        if(chams.isEnabled() && chams.heldItems.isEnabled()) {
            if(chams.itemTextureMode.is("Custom")) RenderSystem.setShaderTexture(0, chams.getTextureID());
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "HEAD"), cancellable = true)
    void render3(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        //        Shaders shaders = (Shaders) ModuleManager.INSTANCE.getItemByClass(Shaders.class);
//       fixme
//        if(shaders.isEnabled() && shaders.itemShaders.isEnabled()) {
//            ci.cancel();
//           Managers.SHADER.renderShader(
//                    () -> this.renderItem(player, item,
//                            hand == Hand.MAIN_HAND ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
//                            !(hand == Hand.MAIN_HAND), matrices, vertexConsumers, light),
//                    Shader.OUTLINE
//            );
//        }
    }

    @Inject(method = "updateHeldItems", at = @At("HEAD"), cancellable = true)
    void updateHeldItems(CallbackInfo ci) {
        if(client.player == null) return;
        if((ModuleManager.INSTANCE.getItemByClass(ViewModel.class).isEnabled() && client.options.useKey.isPressed() && (((ViewModel) ModuleManager.INSTANCE.getItemByClass(ViewModel.class))).animations.isEnabled())) {

            this.equipProgressMainHand = 1;
            this.equipProgressOffHand = 1;

            this.prevEquipProgressMainHand = this.equipProgressMainHand;
            this.prevEquipProgressOffHand = this.equipProgressOffHand;
            ClientPlayerEntity clientPlayerEntity = this.client.player;
            ItemStack itemStack = clientPlayerEntity.getMainHandStack();
            ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();

            if (ItemStack.areEqual(this.mainHand, itemStack)) {
                this.mainHand = itemStack;
            }
            if (ItemStack.areEqual(this.offHand, itemStack2)) {
                this.offHand = itemStack2;
            }

            this.mainHand = itemStack;
            this.offHand = itemStack2;

            ci.cancel();
        }
    }
}
