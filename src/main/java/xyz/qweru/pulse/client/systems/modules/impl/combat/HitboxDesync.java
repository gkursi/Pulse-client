package xyz.qweru.pulse.client.systems.modules.impl.combat;

import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.utils.Util;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class HitboxDesync extends ClientModule {

    public HitboxDesync() {
        builder()
                .name("HitboxDesync")
                .description("Break minecrafts hitbox calculations")
                .category(Category.COMBAT);
    }

    @Override
    public void enable() {
        super.enable();
        if(Util.nullCheck()) return;
        Vec3d vec3d = mc.player.getBlockPos().toCenterPos();

        boolean flagX = (vec3d.x - mc.player.getX()) > 0;
        boolean flagZ = (vec3d.z - mc.player.getZ()) > 0;

        double x = vec3d.x + 0.20000000009497754 * (flagX ? -1 : 1);
        double z = vec3d.z + 0.2000000000949811 * (flagZ ? -1 : 1);

        mc.player.setPosition(x, mc.player.getY(), z);
        this.toggle();
    }


}
