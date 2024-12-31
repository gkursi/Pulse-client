package xyz.qweru.pulse.client.systems.modules.impl.movement;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.events.TickEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;
import xyz.qweru.pulse.client.utils.world.BlockUtil;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.player.MovementUtil;
import meteordevelopment.orbit.EventHandler;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import java.lang.constant.ClassDesc;

import static xyz.qweru.pulse.client.PulseClient.LOGGER;
import static xyz.qweru.pulse.client.PulseClient.mc;

@ExcludeModule
public class AutoHole extends ClientModule {

    BooleanSetting serverOnly = booleanSetting()
            .name("Server only")
            .description("Only move server side")
            .defaultValue(true)
            .build();

    BooleanSetting toggle = booleanSetting()
            .name("Toggle")
            .description("Toggle after use")
            .defaultValue(true)
            .build();

    public AutoHole() {
        super("AutoHole", "Automatically moves you to the middle of your hole", -1, Category.MOVEMENT);
        builder(this).settings(serverOnly, toggle);
    }

    @EventHandler
    void tick(WorldTickEvent.Post e) {
        if(!check1x1(mc.player.getBlockPos()) && !check1x2(mc.player.getBlockPos()) && !check2x2(mc.player.getBlockPos())) {
            LOGGER.info("Not in hole");
        }
        if(toggle.isEnabled()) setEnabled(false);
    }


    boolean checkBlock(BlockPos pos) {
        return Util.equalsAny(BlockUtil.getBlockAt(pos), Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.REINFORCED_DEEPSLATE, Blocks.BARRIER, Blocks.END_PORTAL_FRAME);
    }

    boolean checkAir(BlockPos pos) {
        return Util.equalsAny(BlockUtil.getBlockAt(pos), Blocks.AIR, Blocks.WATER, Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH);
    }

    boolean check1x1(BlockPos pos) {
        if(checkBlock(pos.add(1, 0, 0)) &&
                checkBlock(pos.add(-1, 0, 0)) &&
                checkBlock(pos.add(0, 0, 1)) &&
                checkBlock(pos.add(0, 0, -1)) &&
                checkBlock(pos.add(0, -1, 0)))
        {
            LOGGER.warn("In 1x1 hole");
            return true;
        }
        return false;
    }

    boolean check1x2(BlockPos pos) {
        if(pos.getX() >= 0 && pos.getZ() >= 0) {
            if(checkBlock(pos.add(0, 0, 1)) && // fw
                checkBlock(pos.add(0, 0, -1)) &&
                checkBlock(pos.add(1, 0, 1)) &&
                checkBlock(pos.add(1, 0, -1)) &&
                checkBlock(pos.add(2, 0, 0)) &&
                checkBlock(pos.add(-1, 0, 0)) &&
            checkAir(pos) && checkAir(pos.add(1,0,0))) {
                LOGGER.info("In forwards 1x2 hole, positive x, positive z");
                move(new Vec3d(pos.getX() + 1, mc.player.getY(), mc.player.getZ()));
                return true;
            } else if(checkBlock(pos.add(0, 0, 1)) && // bw
                    checkBlock(pos.add(0, 0, -1)) &&
                    checkBlock(pos.add(-1, 0, 1)) &&
                    checkBlock(pos.add(-1, 0, -1)) &&
                    checkBlock(pos.add(-2, 0, 0)) &&
                    checkBlock(pos.add(1, 0, 0)) &&
                    checkAir(pos) && checkAir(pos.add(-1, 0,0))
            ) {
                LOGGER.info("In backwards 1x2 hole, positive x, positive z");
                move(new Vec3d(pos.getX() - 1, mc.player.getY(), mc.player.getZ()));
                return true;
            } else if(checkBlock(pos.add(1, 0, 0)) && // left
                    checkBlock(pos.add(-1, 0, 0)) &&
                    checkBlock(pos.add(1, 0, -1)) &&
                    checkBlock(pos.add(-1, 0, -1)) &&
                    checkBlock(pos.add(0, 0, 1)) &&
                    checkBlock(pos.add(0, 0, -2)) &&
                    checkAir(pos) && checkAir(pos.add(0, 0, -1))
            ) {
                LOGGER.info("In left 1x2 hole, positive x, positive z");
                move(new Vec3d(mc.player.getX(), mc.player.getY(), pos.getZ() - 1));
                return true;
            } else if(checkBlock(pos.add(1, 0, 0)) && // right
                    checkBlock(pos.add(-1, 0, 0)) &&
                    checkBlock(pos.add(1, 0, 1)) &&
                    checkBlock(pos.add(-1, 0, 1)) &&
                    checkBlock(pos.add(0, 0, -1)) &&
                    checkBlock(pos.add(0, 0, 2)) &&
                    checkAir(pos) && checkAir(pos.add(0, 0, 1))
            ) {
                LOGGER.info("In right 1x2 hole, positive x, positive z");
                move(new Vec3d(mc.player.getX(), mc.player.getY(), pos.getZ() + 1));
                return true;
            }
        } else if(pos.getX() >= 0 && pos.getZ() <= 0) {

        } else if(pos.getX() <= 0 && pos.getZ() >= 0) {

        } else if(pos.getX() <= 0 && pos.getZ() <= 0) {

        }
        return false;
    }

    boolean check2x2(BlockPos pos) {
        return false;
    }

    void move(Vec3d pos) {
        if(!serverOnly.isEnabled()) {
            mc.player.setPos(pos.x, pos.y, pos.z);
        }
        PacketUtil.sendMove(pos);
    }

    double calc(double n, double mod, Op op) {
        return switch (op) {
            case ADD -> n > 0 ? n + mod : n - mod;
            case SUB -> n > 0 ? n - mod : n + mod;
        };
    }

    enum Op {
        ADD,
        SUB
    }
}
