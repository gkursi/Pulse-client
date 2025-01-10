package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import xyz.qweru.pulse.client.mixin.iinterface.IPlayerInteractEntityC2SPacket;

@Mixin(PlayerInteractEntityC2SPacket.class)
public class PlayerInteractEntityC2SPacketMixin implements IPlayerInteractEntityC2SPacket {

    @Mutable
    @Shadow @Final private int entityId;

//    @Shadow @Final private PlayerInteractEntityC2SPacket.InteractTypeHandler type;
//
//    @Shadow @Final
//    static PlayerInteractEntityC2SPacket.InteractTypeHandler ATTACK;

    @Override
    public void pulse$setID(int id) {
        this.entityId = id;
    }

    // this is peak code trust me
    // nvm it doesn't work :(
//    @Override
//    public boolean pulse$isAttack() {
//        return this.type == ATTACK;
//    }


}
