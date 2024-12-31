package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.mixin.iinterface.IEntityVelocityUpdateS2CPacket;
import xyz.qweru.pulse.client.systems.modules.impl.movement.Velocity;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public class EntityVelocityUpdateS2CPacketMixin implements IEntityVelocityUpdateS2CPacket {


    @Mutable
    @Shadow @Final private int velocityX;


    @Mutable
    @Shadow @Final private int velocityY;

    @Mutable
    @Shadow @Final private int velocityZ;

    @Override
    public void pulse$setX(int x) {
        this.velocityX = x;
    }

    @Override
    public void pulse$setY(int y) {
        this.velocityY = y;
    }

    @Override
    public void pulse$setZ(int z) {
        this.velocityZ = z;
    }
}
