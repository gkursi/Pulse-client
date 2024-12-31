package xyz.qweru.pulse.client.utils.player;

import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.utils.world.PacketUtil;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.world.BlockUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class MovementUtil {
    public static void pathAndMoveTo(BlockPos pos) {
        // move to center of block instead of corner
        pathAndMoveTo(new Vec3d(
                pos.getX() > 0 ? pos.getX()+0.5 : pos.getX() - 0.5,
                pos.getY(),
                pos.getZ() > 0 ? pos.getZ()+0.5 : pos.getZ() - 0.5
        ));
    }

    private static boolean isBlockAir(Vec3d pos) {
//        PulseClient.LOGGER.info("Checked for air at {} {} {}", pos.x, pos.y, pos.z);
        return BlockUtil.getBlockAt(new BlockPos(((int) pos.getX()), ((int) pos.getY()), ((int) pos.getZ()))).equals(Blocks.AIR);
    }

    public static void pathAndMoveTo(Vec3d pos) {
        int MAX_Y_EXTENSION = 10;

        Vec3d playerPos = mc.player.getPos();

        for(int py = 1; py <= MAX_Y_EXTENSION; py++) {
            if(isBlockAir(pos.add(0, py, 0)) && isBlockAir(pos.add(0, py+1, 0))) {
                boolean foundBlock = false;
                if(pos.getX() > playerPos.getX()) {
                    for(double x = playerPos.getX(); x < pos.getX(); x++) {
                        if(!isBlockAir(new Vec3d(x, pos.getY()+py, pos.getZ()))) {
                            foundBlock = true;
                            break;
                        }
                    }
                } else {
                    for(double x = playerPos.getX(); x > pos.getX(); x--) {
                        if(!isBlockAir(new Vec3d(x, pos.getY()+py, pos.getZ()))) {
                            foundBlock = true;
                            break;
                        }
                    }
                }
                if(foundBlock) continue;

                if(pos.getZ() > playerPos.getZ()) {
                    for(double z = playerPos.getZ(); z < pos.getZ(); z++) {
                        if(!isBlockAir(new Vec3d(pos.getX(), pos.getY()+py, z))) {
                            foundBlock = true;
                            break;
                        }
                    }
                } else {
                    for(double z = playerPos.getZ(); z > pos.getZ(); z--) {
                        if(!isBlockAir(new Vec3d(pos.getX(), pos.getY()+py, z))) {
                            foundBlock = true;
                            break;
                        }
                    }
                }
                if(foundBlock) continue;

                Vec3d above = pos.add(0, 1, 0);

                // 1 above
                if(above.getX() > playerPos.getX()) {
                    for(double x = playerPos.getX(); x < above.getX(); x++) {
                        if(!isBlockAir(new Vec3d(x, above.getY()+py, above.getZ()))) {
                            foundBlock = true;
                            break;
                        }
                    }
                } else {
                    for(double x = playerPos.getX(); x > above.getX(); x--) {
                        if(!isBlockAir(new Vec3d(x, above.getY()+py, above.getZ()))) {
                            foundBlock = true;
                            break;
                        }
                    }
                }
                if(foundBlock) continue;

                if(above.getZ() > playerPos.getZ()) {
                    for(double z = playerPos.getZ(); z < above.getZ(); z++) {
                        if(!isBlockAir(new Vec3d(above.getX(), above.getY()+py, z))) {
                            foundBlock = true;
                            break;
                        }
                    }
                } else {
                    for(double z = playerPos.getZ(); z > above.getZ(); z--) {
                        if(!isBlockAir(new Vec3d(above.getX(), above.getY()+py, z))) {
                            foundBlock = true;
                            break;
                        }
                    }
                }
                if(foundBlock) continue;

                Vec3d step1 = new Vec3d(playerPos.getX(), pos.add(0, py, 0).getY(), playerPos.getZ());
                Vec3d step2 = new Vec3d(pos.getX()+(pos.getX() > 0 ? 0 : 1), pos.add(0, py, 0).getY(), pos.getZ()+(pos.getZ() > 0 ? 0 : 1));
                Vec3d step3 = new Vec3d(pos.getX()+(pos.getX() > 0 ? 0 : 1), pos.getY(), pos.getZ()+(pos.getZ() > 0 ? 0 : 1));

//                ChatUtil.sendLocalMsg("Calculated steps: (%s %s %s) (%s %s %s) (%s %s %s)".formatted(step1.x, step1.y, step1.z, step2.x, step2.y, step2.z, step3.x, step3.y, step3.z));

                PacketUtil.sendImmediateMove(step1);
                Util.delay(() -> PacketUtil.sendImmediateMove(step2), 20);
                Util.delay(() -> PacketUtil.sendImmediateMove(step3), 40);
//                ChatUtil.sendLocalMsg("Finished moving!");
                mc.player.setPos(pos.x, pos.y, pos.z);
                PacketUtil.sendMove(pos);
//                Util.delay(() -> { if(mc.player.getPos() != pos) ChatUtil.warn("Failed to execute move steps! (not at target pos)"); }, 1500);
                break;
            }

//            ChatUtil.warn("Failed to execute move steps! (no valid pos found above target)");
        }
    }

    public static void buildVClip(Vec3d pos) {
        for (int i = 0; i < 9; i++) {  // Build up clip range
            PacketUtil.sendImmediately(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.getY(), pos.getZ(), mc.player.isOnGround()));
        }
    }

    private static void waitTicks(long ticks) {
        try {
            Thread.sleep(ticks*50);
            PulseClient.LOGGER.info("slept {} ticks", ticks);
        } catch (InterruptedException ignored) {}
    }

    public static void moveTo(Vec3d from, Vec3d to, boolean paperMode){
        if(!paperMode) {
            double td = Math.ceil(from.distanceTo(to) / 8.5);
            for (int i = 1; i<=td; i++) {
                Vec3d curPos = from.lerp(to, i / td);
                PacketUtil.sendImmediately(new PlayerMoveC2SPacket.PositionAndOnGround(curPos.getX(), curPos.getY(), curPos.getZ(), mc.player.isOnGround()));
                if(i%4 == 0) {
                    waitTicks(2);
                }
            }
        } else {
            Vec3d pos = mc.player.getPos();
            buildVClip(pos); // build tp range
            double maxDistance = 99.0D;
            // vclip up 99 blocks
            PacketUtil.send(new PlayerMoveC2SPacket.PositionAndOnGround(pos.getX(), pos.add(0, maxDistance, 0).getY(), pos.getZ(), mc.player.isOnGround()));
            waitTicks(1); // sleep for 1 tick
            // tp to target xz, client y
            moveTo(mc.player.getPos(), new Vec3d(to.x, mc.player.getPos().y, to.z), false);
            waitTicks(1); // sleep for 1 tick
            buildVClip(mc.player.getPos()); // build tp range
            // tp to target pos
            PacketUtil.send(new PlayerMoveC2SPacket.PositionAndOnGround(to.getX(), to.getY(), to.getZ(), mc.player.isOnGround()));
        }
    }

    public static Vec3d moveTowards(Vec3d from, Vec3d to) {
        double td = Math.ceil(from.distanceTo(to) / 8.5);
        Vec3d curPos = from.lerp(to, 1 / td);
        PacketUtil.send(new PlayerMoveC2SPacket.PositionAndOnGround(curPos.getX(), curPos.getY(), curPos.getZ(), mc.player.isOnGround()));
        return curPos;
    }

}
