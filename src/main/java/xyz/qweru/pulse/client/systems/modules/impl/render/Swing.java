package xyz.qweru.pulse.client.systems.modules.impl.render;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import xyz.qweru.pulse.client.systems.events.HeldItemRenderEvent;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.BooleanSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.utils.Util;

public class Swing extends ClientModule {

    public Swing() {
        builder(this)
                .name("Swing")
                .description("Modify swing")
                .settings(translateMatrices, rotateMatrices)
                .settings("Translate", translateX, translateY, translateZ)
                .settings("Rotate", rotateX, rotateY, rotateZ)
                .category(Category.RENDER);
    }

    BooleanSetting translateMatrices = new BooleanSetting("Translate pos", "change position", true, true);
    BooleanSetting rotateMatrices = new BooleanSetting("Rotate", "change rotation", true, true);

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

    NumberSetting rotateX = numberSetting()
            .name("Rotate X")
            .description("rotate")
            .range(-360, 360)
            .defaultValue(0)
            .stepFullNumbers()
            .build();

    NumberSetting rotateY = numberSetting()
            .name("Rotate Y")
            .description("rotate")
            .range(-360, 360)
            .defaultValue(0)
            .stepFullNumbers()
            .build();

    NumberSetting rotateZ = numberSetting()
            .name("Rotate Z")
            .description("rotate")
            .range(-360, 360)
            .defaultValue(0)
            .stepFullNumbers()
            .build();



    @EventHandler
    void renderItems(HeldItemRenderEvent e) {
        if(e.getHand() != Hand.MAIN_HAND) return;
        if(translateMatrices.isEnabled() && e.getSwingProgress() > 0) translateMatrices(e.getMatrices());
        if(rotateMatrices.isEnabled() && e.getSwingProgress() > 0) rotateMatrices(e.getMatrices());
    }

    void translateMatrices(MatrixStack matrixStack) {
        matrixStack.translate(translateX.getValue(), translateY.getValue(), translateZ.getValue());
    }

    void rotateMatrices(MatrixStack matrices) {
        float value = rotateX.getValue();
        if(value >= 0) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(value));
        else matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(value));

        value = rotateY.getValue();
        if(value >= 0) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(value));
        else matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(value));

        value = rotateZ.getValue();
        if(value >= 0) matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(value));
        else matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(value));
    }
}
