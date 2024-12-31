package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import xyz.qweru.pulse.client.mixin.iinterface.IExplosionS2CPacket;

@Mixin(ExplosionS2CPacket.class)
public class ExplosionS2CPacketMixin implements IExplosionS2CPacket {

    @Mutable
    @Shadow @Final private float playerVelocityX;

    @Mutable
    @Shadow @Final private float playerVelocityY;

    @Mutable
    @Shadow @Final private float playerVelocityZ;

    @Override
    public void pulse$setVelocityX(float x) {
        this.playerVelocityX = x;
    }

    @Override
    public void pulse$setVelocityY(float y) {
        this.playerVelocityY = y;
    }

    @Override
    public void pulse$setVelocityZ(float z) {
        this.playerVelocityZ = z;
    }
}
