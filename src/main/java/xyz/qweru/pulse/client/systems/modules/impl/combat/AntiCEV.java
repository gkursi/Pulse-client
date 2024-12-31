package xyz.qweru.pulse.client.systems.modules.impl.combat;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.qweru.pulse.client.mixin.iinterface.IWorld;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.entity.EntityFinder;
import xyz.qweru.pulse.client.utils.player.PlayerUtil;
import xyz.qweru.pulse.client.utils.player.SlotUtil;
import xyz.qweru.pulse.client.utils.world.BlockUtil;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import java.util.ArrayList;
import java.util.List;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class AntiCEV extends ClientModule {

    NumberSetting range = numberSetting()
            .name("Range")
            .description("Range")
            .range(0, 7)
            .defaultValue(5f)
            .setValueModifier(value -> (float) Util.round(value, 2))
            .build();

    public AntiCEV() {
        builder()
                .name("Anti CEV")
                .description("Automatically attack crystals near you")
                .settings(range)
                .category(Category.COMBAT);
    }

    @EventHandler
    void tick(WorldTickEvent.Post e) {
        EntityFinder.EntityList targets = EntityFinder.findEntitiesInRange(range.getValue(), mc.player.getPos());
        for (Entity entity : targets.get()) {
            if(!entity.isAlive()) continue;
            if(entity instanceof EndCrystalEntity ec) {
                PlayerUtil.attackEntity(ec);
            }
        }
    }

}
