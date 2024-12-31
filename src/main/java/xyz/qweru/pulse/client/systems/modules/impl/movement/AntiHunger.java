package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import xyz.qweru.pulse.client.mixin.iinterface.IPlayerMoveC2SPacket;
import xyz.qweru.pulse.client.systems.events.SendPacketEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.utils.InputUtil;
import xyz.qweru.pulse.client.utils.Util;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class AntiHunger extends ClientModule {

    BooleanSetting onGround = booleanSetting()
            .name("Ground spoof")
            .description("Spoof ground")
            .defaultValue(false)
            .build();

    BooleanSetting walk = booleanSetting()
            .name("Walk spoof")
            .description("Spoof walking when sprinting")
            .defaultValue(true)
            .build();

    public AntiHunger() {
        super("Anti hunger", "Reduces hunger", InputUtil.KEY_UNKNOWN, Category.MOVEMENT);
        builder()
                .settings(walk, onGround);
    }

    @EventHandler
    void p2S(SendPacketEvent event) {
        if(Util.nullCheck()) return;
        Packet<?> p = event.getPacket();

        if (mc.player.hasVehicle() || mc.player.isTouchingWater() || mc.player.isSubmergedInWater()) return;

        if (p instanceof ClientCommandC2SPacket packet && walk.isEnabled()) {
            if (packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) event.cancel();
        }

        if (event.getPacket() instanceof PlayerMoveC2SPacket packet && onGround.isEnabled() && mc.player.isOnGround() && mc.player.fallDistance <= 0.0 && !mc.interactionManager.isBreakingBlock()) {
            ((IPlayerMoveC2SPacket) packet).pulse$setOnGround(false);
        }
    }

}
