package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.systems.modules.impl.movement.Step;
import xyz.qweru.pulse.client.systems.modules.impl.movement.Velocity;
import xyz.qweru.pulse.client.systems.modules.impl.render.NoRender;
import xyz.qweru.pulse.client.systems.modules.impl.render.ViewModel;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
    void takeKB(double strength, double x, double z, CallbackInfo ci) {
        if(ModuleManager.INSTANCE.getItemByClass(Velocity.class).isEnabled() && !(Velocity.mode.is("vulcan") && !Velocity.removeKB)) {
            ci.cancel();
        }
    }

    @Inject(method = "getHandSwingProgress", at = @At("RETURN"), cancellable = true)
    void swingProgress(float tickDelta, CallbackInfoReturnable<Float> cir) {
        NoRender noRender = (NoRender) Managers.MODULE.getItemByClass(NoRender.class);
        if(noRender.isEnabled() && noRender.swing.isEnabled()) {
            cir.setReturnValue(0f);
            return;
        }

        ViewModel module = ((ViewModel) ModuleManager.INSTANCE.getItemByClass(ViewModel.class));
        if(module.isEnabled()) cir.setReturnValue(cir.getReturnValueF() * module.handSwingProgress.getValue());
    }

    @Inject(method = "getHandSwingDuration", at = @At("RETURN"), cancellable = true)
    void swingDuration(CallbackInfoReturnable<Integer> cir) {
        ViewModel module = ((ViewModel) ModuleManager.INSTANCE.getItemByClass(ViewModel.class));
        if(module.isEnabled()) cir.setReturnValue(module.swingDuration.getValueInt());
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    void push(Entity entity, CallbackInfo ci) {
        if(ModuleManager.INSTANCE.getItemByClass(Velocity.class).isEnabled()){
            ci.cancel();
        }
    }

    @Inject(method = "pushAway", at = @At("HEAD"), cancellable = true)
    void pushAway(Entity entity, CallbackInfo ci) {
        if(ModuleManager.INSTANCE.getItemByClass(Velocity.class).isEnabled()) ci.cancel();
    }

    @Inject(method = "getStepHeight", at = @At("RETURN"), cancellable = true)
    void step(CallbackInfoReturnable<Float> cir) {
        if(Managers.MODULE.getItemByClass(Step.class).isEnabled()) cir.setReturnValue(Step.height.getValue());
    }

}
