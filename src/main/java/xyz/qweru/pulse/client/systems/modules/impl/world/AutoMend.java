package xyz.qweru.pulse.client.systems.modules.impl.world;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.impl.combat.AutoAnchor;
import xyz.qweru.pulse.client.systems.modules.impl.combat.AutoCrystal;
import xyz.qweru.pulse.client.systems.modules.impl.combat.Killaura;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.player.ChatUtil;
import xyz.qweru.pulse.client.utils.player.InventoryUtils;
import xyz.qweru.pulse.client.utils.player.PlayerUtil;
import xyz.qweru.pulse.client.utils.timer.TimerUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class AutoMend extends ClientModule {

    NumberSetting percentage = numberSetting()
            .name("Repair %")
            .description("What % of durability should armor be repaired at")
            .range(0, 1)
            .defaultValue(0.5f)
            .setValueModifier(value -> (float) Util.round(value, 2))
            .build();

    NumberSetting tDelay = numberSetting()
            .name("Throw delay")
            .description("Delay in ms between xp throws")
            .range(0, 500)
            .defaultValue(20)
            .stepFullNumbers().build();

    NumberSetting yaw = numberSetting()
            .name("Throw yaw")
            .description("yaw")
            .range(-180, 180)
            .defaultValue(0)
            .stepFullNumbers().build();

    NumberSetting pitch = numberSetting()
            .name("Throw pitch")
            .description("pitch")
            .range(-90, 90)
            .defaultValue(-90)
            .stepFullNumbers().build();

    BooleanSetting pauseCombat = booleanSetting()
            .name("Pause combat")
            .description("Pause all combat modules")
            .build();

    BooleanSetting dupe = booleanSetting()
            .name("Redupe")
            .description("Automatically dupe exp on play.dupeanarchy.com")
            .build();

    BooleanSetting fullRepair = booleanSetting()
            .name("Full repair")
            .description("Repair to max durability instead of the repair %")
            .defaultValue(true)
            .build();

    public AutoMend() {
        builder()
                .name("Auto Mend")
                .description("Automatically mend your armor")
                .settings(percentage, pauseCombat, tDelay, fullRepair, dupe)
                .settings("Angle", yaw, pitch)
                .category(Category.WORLD);
    }

    TimerUtil timer = new TimerUtil();
    boolean prevRepair = false;
    boolean crystal, anchor, sword;
    float targetPercent = 0.95f;
    int dupeTicks = 0;
    @EventHandler
    void tick(WorldTickEvent.Post ignored) {
        if(!shouldRepair()) {
            targetPercent = percentage.getValue();
            if(prevRepair && pauseCombat.isEnabled()) {
                Managers.MODULE.getItemByClass(AutoCrystal.class).setEnabled(crystal);
                Managers.MODULE.getItemByClass(AutoAnchor.class).setEnabled(anchor);
                Managers.MODULE.getItemByClass(Killaura.class).setEnabled(sword);
                Managers.SLOT.sync();
            }

            prevRepair = false;
            return;
        }
        if(!timer.hasReached(tDelay.getValue())) return;
        int slot = InventoryUtils.getItemSlotHotbar(Items.EXPERIENCE_BOTTLE);
        if(slot == -1 && mc.player.getOffHandStack().getItem() != Items.EXPERIENCE_BOTTLE) return;
        if(!prevRepair) {
            Managers.SLOT.selectOnServer(slot);
            crystal = Managers.MODULE.getItemByClass(AutoCrystal.class).isEnabled();
            anchor = Managers.MODULE.getItemByClass(AutoAnchor.class).isEnabled();
            sword = Managers.MODULE.getItemByClass(Killaura.class).isEnabled();

            if(pauseCombat.isEnabled()) {
                Managers.MODULE.getItemByClass(AutoCrystal.class).setEnabled(false);
                Managers.MODULE.getItemByClass(AutoAnchor.class).setEnabled(false);
                Managers.MODULE.getItemByClass(Killaura.class).setEnabled(false);
            }

            if(fullRepair.isEnabled()) targetPercent = 0.95f;

            prevRepair = true;
        }

        PlayerUtil.interact(mc.player, Hand.MAIN_HAND, slot, pitch.getValue(), yaw.getValue());

        // autodupe
        if(dupe.isEnabled()) {
            int count = InventoryUtils.totalItemCount(Items.EXPERIENCE_BOTTLE);
            if(count != 0) {
                if (count <= 32) {
                    if (dupeTicks > 0) dupeTicks--;
                    else {
                        if (count == 32) ChatUtil.sendServerMsg("/dupe 1 experience_bottle");
                        else if (count >= 16) ChatUtil.sendServerMsg("/dupe 4 experience_bottle");
                        else ChatUtil.sendServerMsg("/dupe 64 experience_bottle");
                        dupeTicks = 6;
                    }
                }
            }
        }

        timer.reset();
    }

    boolean shouldRepair() {
        return getMinArmorDurability() <= percentage.getValue();
    }

    float getMinArmorDurability() {
        PlayerInventory inventory = mc.player.getInventory();

        float minArmorDurability = 1f;
        for (ItemStack itemStack : inventory.armor) {
            if(itemStack.isEmpty() || !(itemStack.getItem() instanceof ArmorItem armorItem) || itemStack.getMaxDamage() <= 0) continue;
            minArmorDurability = Math.min(1 - ((float) itemStack.getDamage() / itemStack.getMaxDamage()), minArmorDurability);
        }

        return minArmorDurability;
    }

    @Override
    public void disable() {
        super.disable();
    }

    @Override
    public void enable() {
        super.enable();
        prevRepair = false;
    }
}


