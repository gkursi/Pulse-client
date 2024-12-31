package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import xyz.qweru.pulse.client.mixin.iinterface.IPlayerPositionLookS2CPacket;

@Mixin(PlayerPositionLookS2CPacket.class)
public class PlayerPositionLookS2CPacketMixin implements IPlayerPositionLookS2CPacket {


    @Mutable
    @Shadow @Final private float yaw;

    @Mutable
    @Shadow @Final private float pitch;

    @Override
    public void pulse$setLook(float pitch, float yaw) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
