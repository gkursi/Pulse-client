package xyz.qweru.pulse.client.systems.modules.impl.world;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.render.renderer.Pulse2D;
import xyz.qweru.pulse.client.render.renderer.Pulse3D;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.events.BreakBlockEvent;
import xyz.qweru.pulse.client.systems.events.InstamineEvent;
import xyz.qweru.pulse.client.systems.events.Render3DEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.timer.TimerUtil;
import xyz.qweru.pulse.client.utils.world.BlockUtil;
import xyz.qweru.pulse.client.utils.world.PosUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class InstantBreak extends ClientModule {

    public InstantBreak() {
        builder(this)
                .name("Instant break")
                .description("Instantly break blocks after mining once")
                .settings(range, delay, dontMineAnchors)
                .category(Category.WORLD);
    }

    public BlockPos pos = null;
    NumberSetting range = numberSetting()
            .name("Range")
            .description("Range for blocks")
            .range(0, 4.5f)
            .defaultValue(4)
            .build();

    NumberSetting delay = numberSetting()
            .name("Delay")
            .description("Delay between break packets (ticks)")
            .range(0, 10f)
            .defaultValue(1)
            .stepFullNumbers()
            .build();

    BooleanSetting dontMineAnchors = booleanSetting()
            .name("Dont mine anchors")
            .description("Doesnt instamine respawn anchors")
            .build();

    @EventHandler
    void bb(BreakBlockEvent e) {
        pos = e.getPos();
    }

    @EventHandler
    void render(Render3DEvent e) {
        if(pos != null) {
            Pulse3D.renderThroughWalls();
            Pulse3D.renderEdged(e.getMatrixStack(), Pulse2D.injectAlpha(ThemeInfo.COLORSCHEME.PRIMARY(), 0), ThemeInfo.COLORSCHEME.ACCENT(), Vec3d.of(pos), new Vec3d(1, 1, 1));
        }
    }

    TimerUtil timer = new TimerUtil();
    @EventHandler
    void tick(WorldTickEvent.Pre e) {
        if(pos == null) return;
        if(PosUtil.distanceBetween(Vec3d.of(pos), mc.player.getPos()) <= range.getValue() && shouldMine()) {
            if(timer.hasReached(delay.getValue() * 50)) sendPacket();
            timer.reset();
        } else {
            pos = null;
        }
    }

    @Override
    public void enable() {
        super.enable();
        pos = null;
    }

    public void sendPacket() {
        PulseClient.Events.post(new InstamineEvent.Pre());
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
        PulseClient.Events.post(new InstamineEvent.Post());
    }

    public boolean shouldMine() {
        return !mc.world.isOutOfHeightLimit(pos) && BlockUtil.isPosBreakable(pos) && !(dontMineAnchors.isEnabled() && BlockUtil.getBlockAt(pos).equals(Blocks.RESPAWN_ANCHOR));
    }

    public static boolean isBreaking(BlockPos pos) {
        InstantBreak module = (InstantBreak) ModuleManager.INSTANCE.getItemByClass(InstantBreak.class);
        return module.isEnabled() && pos == module.pos;
    }

    public static boolean isBreaking() {
        InstantBreak module = (InstantBreak) ModuleManager.INSTANCE.getItemByClass(InstantBreak.class);
        return module.isEnabled() && module.pos != null;
    }
}
