package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import static xyz.qweru.pulse.client.PulseClient.LOGGER;
import static xyz.qweru.pulse.client.PulseClient.mc;

public class PlayerTP extends ClientModule {

    BooleanSetting everyTick = booleanSetting()
            .name("Every tick")
            .description("Teleport every tick")
            .defaultValue(true)
            .build();

    NumberSetting range = numberSetting()
            .name("Range")
            .description("Range")
            .range(0, 128)
            .defaultValue(64)
            .build();

    public PlayerTP() {
        builder()
                .name("PlayerTP")
                .description("Automatically teleport to the nearest player (assumes line of sight)")
                .settings(everyTick, range)
                .category(Category.MOVEMENT);
    }

    @EventHandler
    void tick(WorldTickEvent.Post e) {
        LOGGER.debug("Tick");
        PlayerEntity closest = null;
        double d = Double.MAX_VALUE;
        for (Entity entity : mc.world.getEntities()) {
            if(entity instanceof PlayerEntity player && player != mc.player && mc.player.distanceTo(player) < d) {
                closest = player;
                d = mc.player.distanceTo(player);
            }
        }

        if(closest != null) {
            Vec3d from = mc.player.getPos();
            Vec3d to = closest.getPos();

            double td = Math.ceil(from.distanceTo(to) / 8.5);
            for (int i = 1; i<=td; i++) {
                Vec3d curPos = from.lerp(to, i / td);
                PacketUtil.sendImmediately(new PlayerMoveC2SPacket.PositionAndOnGround(curPos.getX(), curPos.getY(), curPos.getZ(), mc.player.isOnGround()));
                mc.player.setPosition(curPos);
//                LOGGER.debug("Set cur pos to {}", curPos);
            }
        } else {
//            LOGGER.debug("!! null");
        }
    }
}
