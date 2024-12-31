package xyz.qweru.pulse.client.systems.modules.impl.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.RotationAxis;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;

public class ViewModel extends ClientModule {

    public ViewModel() {
        builder(this)
                .name("ViewModel")
                .description("Change hand view")
                .settings("Mainhand scale", scaleX, scaleY, scaleZ)
                .settings("Mainhand pos", translateX, translateY, translateZ)
                .settings("Offhand scale", oscaleX, oscaleY, oscaleZ)
                .settings("Offhand pos", otranslateX, otranslateY, otranslateZ)
                .settings("Swing", handSwingProgress, swingDuration)
                .category(Category.RENDER);
    }

    NumberSetting scaleX = numberSetting()
            .name("Scale X")
            .description("scale x")
            .range(-2, 2)
            .defaultValue(1)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    NumberSetting scaleY = numberSetting()
            .name("Scale Y")
            .description("scale y")
            .range(-2, 2)
            .defaultValue(1)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    NumberSetting scaleZ = numberSetting()
            .name("Scale Z")
            .description("scale z")
            .range(-2, 2)
            .defaultValue(1)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    NumberSetting translateX = numberSetting()
            .name("Translate X")
            .description("move")
            .range(-10, 10)
            .defaultValue(0)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    NumberSetting translateY = numberSetting()
            .name("Translate Y")
            .description("move")
            .range(-10, 10)
            .defaultValue(0)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    NumberSetting translateZ = numberSetting()
            .name("Translate Z")
            .description("move")
            .range(-10, 10)
            .defaultValue(0)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    NumberSetting oscaleX = numberSetting()
            .name("Offhand scale X")
            .description("scale x")
            .range(-2, 2)
            .defaultValue(1)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    NumberSetting oscaleY = numberSetting()
            .name("Offhand scale Y")
            .description("scale y")
            .range(-2, 2)
            .defaultValue(1)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    NumberSetting oscaleZ = numberSetting()
            .name("Offhand scale Z")
            .description("scale z")
            .range(-2, 2)
            .defaultValue(1)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    NumberSetting otranslateX = numberSetting()
            .name("Offhand translate X")
            .description("move")
            .range(-10, 10)
            .defaultValue(0)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    NumberSetting otranslateY = numberSetting()
            .name("Offhand translate Y")
            .description("move")
            .range(-10, 10)
            .defaultValue(0)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    NumberSetting otranslateZ = numberSetting()
            .name("Offhand translate Z")
            .description("move")
            .range(-10, 10)
            .defaultValue(0)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    public BooleanSetting animations = booleanSetting()
            .name("Animations")
            .description("Render better hit animations")
            .build();

    public NumberSetting handSwingProgress = numberSetting()
            .name("Swing multiplier")
            .description("swing progress multiplier")
            .range(0, 2)
            .defaultValue(0)
            .setValueModifier(value -> (float) Util.round(value, 1))
            .build();

    public NumberSetting swingDuration = numberSetting()
            .name("Swing duration")
            .description("swing duration")
            .range(0, 24)
            .defaultValue(6)
            .stepFullNumbers()
            .build();

    public void renderMainhand(MatrixStack matrices) {
        matrices.translate(translateX.getValue(), translateY.getValue(), translateZ.getValue());
        matrices.scale(scaleX.getValue(), scaleY.getValue(), scaleZ.getValue());
    }

    public void renerOffhand(MatrixStack matrices) {
        matrices.translate(otranslateX.getValue(), otranslateY.getValue(), otranslateZ.getValue());
        matrices.scale(oscaleX.getValue(), oscaleY.getValue(), oscaleZ.getValue());
    }

    private long lastFrameTime = 0;
    private float start, end, current;

    NumberSetting animY = numberSetting()
            .name("Animation Y")
            .description("animation x rotation")
            .range(0, 360)
            .defaultValue(85)
            .stepFullNumbers()
            .build();

    NumberSetting animZ = numberSetting()
            .name("Animation Z")
            .description("animation z rotation")
            .range(-180, 180)
            .defaultValue(-17)
            .stepFullNumbers()
            .build();

    public void renderAnimation(ItemStack stack, MatrixStack matrices) {
        if(!animations.isEnabled()) return;

        MinecraftClient mc = PulseClient.mc;

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastFrameTime;
        lastFrameTime = currentTime;

        if(!(stack.getItem() instanceof SwordItem || stack.getItem() instanceof EndCrystalItem)) {
            return;
        }

        if (mc.options.useKey.isPressed()) {

            // Calculate the swing increment based on elapsed time
            float swingIncrement = 200f; // Adjust the swing speed as desired
            float swingProgress = swingIncrement * (elapsedTime / 1000f); // Divide by 1000 to convert milliseconds to seconds

            if (current > end) {
                current -= swingProgress;
            }
            else if (current < end) {
                current += swingProgress;
            }
            if (end == -130 && current <= end) {
                end = start;
            }
            if(end == start && current >= end) {
                current = end;
            }

            // Set the rotation point at the handle of the sword
            matrices.translate(0.4, -0.2, -0.1);
            // Rotates to face the player
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(animY.getValueLong()));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(animZ.getValueLong()));
            // Rotates the sword up/down
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(current));

            matrices.translate(0, 0.6, 0.7);
        }
    }

    @Override
    public void enable() {
        super.enable();
        start = -90;
        end = start;
        current = start;
    }
}
