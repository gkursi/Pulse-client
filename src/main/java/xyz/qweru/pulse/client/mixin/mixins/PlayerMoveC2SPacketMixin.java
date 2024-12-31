package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import xyz.qweru.pulse.client.mixin.iinterface.IPlayerMoveC2SPacket;

@Mixin(PlayerMoveC2SPacket.class)
public class PlayerMoveC2SPacketMixin implements IPlayerMoveC2SPacket {

    @Mutable
    @Shadow @Final protected double x;

    @Mutable
    @Shadow @Final protected double y;

    @Mutable
    @Shadow @Final protected double z;

    @Mutable
    @Shadow @Final protected boolean changePosition;

    @Mutable
    @Shadow @Final protected boolean onGround;

    @Mutable
    @Shadow @Final protected float pitch;

    @Mutable
    @Shadow @Final protected float yaw;

    @Mutable
    @Shadow @Final protected boolean changeLook;

    @Override
    public void pulse$setX(double x) {
        this.x = x;
    }

    @Override
    public void pulse$setY(double y) {
        this.y = y;
    }

    @Override
    public void pulse$setZ(double z) {
        this.z = z;
    }

    @Override
    public void pulse$setChangesPosition(boolean changesPosition) {
        this.changePosition = changesPosition;
    }

    @Override
    public void pulse$setChangesRotation(boolean changesLook) {
        this.changeLook = changesLook;
    }

    @Override
    public void pulse$setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public void pulse$setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Override
    public void pulse$setYaw(float yaw) {
        this.yaw = yaw;
    }
}
