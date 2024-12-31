package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.mixin.iinterface.ICamera;
import xyz.qweru.pulse.client.systems.modules.impl.render.FreeCam;

@Mixin(Camera.class)
public abstract class CameraMixin implements ICamera {

    @Override
    public float pulse$getX() {
        return (float) pos.x;
    }

    @Override
    public float pulse$getY() {
        return (float) pos.y;
    }

    @Override
    public float pulse$getZ() {
        return (float) pos.z;
    }

    @Override
    public float pulse$getPitch() {
        return pitch;
    }

    @Override
    public float pulse$getYaw() {
        return yaw;
    }

    @Override
    public Vec3d pulse$getRotationVector() {
        float f = this.pitch * ((float)Math.PI / 180);
        float g = -this.yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    @Override
    public void pulse$setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void pulse$setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    public void pulse$setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Override
    public void pulse$setPos(Vec3d pos) {
        this.pos = pos;
    }

    @Shadow private Vec3d pos;

    @Shadow protected abstract void moveBy(float f, float g, float h);

    @Shadow private float pitch;

    @Shadow private float yaw;

    @Shadow @Final private Quaternionf rotation;

    @Override
    public void pulse$moveBy(double x, double y, double z) {
        this.pos.add(x, y, z);
    }

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    void clip(float f, CallbackInfoReturnable<Float> cir) {
        if(ModuleManager.INSTANCE.getItemByClass(FreeCam.class).isEnabled()) cir.setReturnValue(f);
    }

    // prevent moving/rotating with player
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    public void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if(ModuleManager.INSTANCE.getItemByClass(FreeCam.class).isEnabled()) ci.cancel();
    }

    // show player model
    @Inject(method = "isThirdPerson", at = @At("RETURN"), cancellable = true)
    public void isThirdPerson(CallbackInfoReturnable<Boolean> cir) {
        if(ModuleManager.INSTANCE.getItemByClass(FreeCam.class).isEnabled()) cir.setReturnValue(true);
    }

    @Inject(method = "updateEyeHeight", at = @At("RETURN"), cancellable = true)
    public void updateEyeHeight(CallbackInfo ci) {
        if(ModuleManager.INSTANCE.getItemByClass(FreeCam.class).isEnabled()) ci.cancel();
    }

    @Inject(method = "getSubmersionType", at = @At("HEAD"), cancellable = true)
    public void getSubmersionType(CallbackInfoReturnable<CameraSubmersionType> cir) {
        if(ModuleManager.INSTANCE.getItemByClass(FreeCam.class).isEnabled()) cir.setReturnValue(CameraSubmersionType.NONE);
    }
}
