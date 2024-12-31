package xyz.qweru.pulse.client.systems.modules.impl.movement;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.option.GameOptions;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.mixin.iinterface.IPlayerPositionLookS2CPacket;
import xyz.qweru.pulse.client.systems.events.HandlePacketEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.PlayerUtil;
import xyz.qweru.pulse.client.utils.player.SlotUtil;
import xyz.qweru.pulse.client.utils.world.BlockUtil;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import static xyz.qweru.pulse.client.PulseClient.LOGGER;
import static xyz.qweru.pulse.client.PulseClient.mc;

public class AutoPhase extends ClientModule {

    public AutoPhase() {
        builder(this)
                .name("AutoPhase")
                .description("Automatically phase in to the wall using ender pearls (WIP)")
                .settings(silentSwitch, mode, noPearlRot, placeBlock, delay)
                .category(Category.MOVEMENT);
    }

    BooleanSetting silentSwitch = booleanSetting()
            .name("Silent switch")
            .description("Switch using packets")
            .defaultValue(true)
            .build();

    BooleanSetting noPearlRot = booleanSetting()
            .name("Ignore pearl rotation")
            .description("Ignore ender pearl rotation")
            .defaultValue(true)
            .build();

    ModeSetting mode = modeSetting()
            .name("Mode")
            .description("mode")
            .defaultMode("Simple")
            .mode("Smart")
            .mode("Simple")
            .build();

    BooleanSetting placeBlock = booleanSetting()
            .name("Place block")
            .description("Ignore ender pearl rotation")
            .defaultValue(true)
            .build();

    NumberSetting delay = numberSetting()
            .name("Pearl delay")
            .description("Delay between block place and pearl throw")
            .range(0, 1000)
            .defaultValue(50)
            .stepFullNumbers()
            .build();

    @Override
    public void enable() {
        super.enable();
        if(Util.nullCheck()) return;
        execute();
        if(!noPearlRot.isEnabled()) this.toggle();
    }

    Direction getVelocityDirection() {
        GameOptions options = mc.options;
        switch (Direction.fromRotation(mc.player.getYaw())) {
            case NORTH -> {
                if(options.rightKey.isPressed()) return Direction.EAST;
                else if(options.leftKey.isPressed()) return Direction.WEST;
                else if(options.backKey.isPressed()) return Direction.SOUTH;
                else if(options.forwardKey.isPressed()) return Direction.NORTH;
            }
            case SOUTH -> {
                if(options.rightKey.isPressed()) return Direction.WEST;
                else if(options.leftKey.isPressed()) return Direction.EAST;
                else if(options.backKey.isPressed()) return Direction.NORTH;
                else if(options.forwardKey.isPressed()) return Direction.SOUTH;
            }
            case WEST -> {
                if(options.rightKey.isPressed()) return Direction.NORTH;
                else if(options.leftKey.isPressed()) return Direction.SOUTH;
                else if(options.backKey.isPressed()) return Direction.EAST;
                else if(options.forwardKey.isPressed()) return Direction.WEST;
            }
            case EAST -> {
                if(options.rightKey.isPressed()) return Direction.SOUTH;
                else if(options.leftKey.isPressed()) return Direction.NORTH;
                else if(options.backKey.isPressed()) return Direction.WEST;
                else if(options.forwardKey.isPressed()) return Direction.EAST;
            }
        }

        return mc.player.getMovementDirection();
    }

    void execute() {
        if(Util.nullCheck()) return;
        int ps = mc.player.getInventory().selectedSlot;
        SlotUtil.runWithItem((slot, inventory) -> {
            if(mode.is("simple")) {
                PlayerUtil.interact(mc.player, Hand.MAIN_HAND, slot, 81, mc.player.getYaw());
                PacketUtil.send(new UpdateSelectedSlotC2SPacket(ps));
            } else if(mode.is("smart")) {
                Direction direction = getVelocityDirection();

                float pitch = 81;
                if(direction == Direction.UP || direction == Direction.DOWN) {
                    pitch = 85;
                    direction = Direction.NORTH;
                }

                if(placeBlock.isEnabled()) {
                    BlockPos pos = BlockPos.ofFloored(mc.player.getPos().offset(direction, 1));
                    if(BlockUtil.getBlockAt(pos).equals(Blocks.AIR) && BlockUtil.getBlockAt(mc.player.getBlockPos()).equals(Blocks.AIR)) {
                        if(mc.player.getPos().distanceTo(Vec3d.of(pos)) > 0.5f) pitch = 75f;
                        Direction finalDirection = direction;
                        SlotUtil.runWithItem((s, i) -> {
                            PlayerUtil.placeBlock(new BlockHitResult(Vec3d.of(pos), finalDirection, pos, false));
                        }, Items.OBSIDIAN, silentSwitch.isEnabled());
                    }
                    Util.sleep(delay.getValueLong());
                }
                float yaw = 0;
                switch (direction) {
                    case EAST -> yaw = -90;
                    case WEST -> yaw = 90;
                    case NORTH -> yaw = 180;
                    case SOUTH -> yaw = 0;
                }
                PlayerUtil.interact(mc.player, Hand.MAIN_HAND, slot, pitch, yaw);
                PacketUtil.send(new UpdateSelectedSlotC2SPacket(ps));
            }
        }, Items.ENDER_PEARL, silentSwitch.isEnabled());
    }

    @EventHandler
    void packet(HandlePacketEvent e) {
        if(Util.nullCheck()) return;
        if(e.getPacket() instanceof PlayerPositionLookS2CPacket packet && noPearlRot.isEnabled()) {
            ((IPlayerPositionLookS2CPacket) packet).pulse$setLook(mc.player.getPitch(), mc.player.getYaw());
            LOGGER.info("Server pos look packet: {}", e.getPacket().getClass().getSimpleName());
            this.toggle();
        }
    }

}
