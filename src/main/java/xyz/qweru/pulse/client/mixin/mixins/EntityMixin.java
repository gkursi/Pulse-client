package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.systems.events.PlayerMoveEvent;
import xyz.qweru.pulse.client.systems.modules.impl.world.Scaffold;

import static xyz.qweru.pulse.client.PulseClient.mc;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    void move(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        Entity _this = ((Entity) (Object) this);

        if(!(_this == mc.player)) return;
        PlayerMoveEvent event = new PlayerMoveEvent(movementType, movement);
        PulseClient.Events.post(event);
        if(event.isCancelled()) ci.cancel();
    }

    @Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
    void s(CallbackInfoReturnable<Boolean> cir) {
//        if(Managers.MODULE.getItemByClass(Scaffold.class).isEnabled()) cir.setReturnValue(true);
    }

}
