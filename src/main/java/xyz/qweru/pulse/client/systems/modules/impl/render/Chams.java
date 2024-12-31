package xyz.qweru.pulse.client.systems.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.builders.ColorSettingBuilder;
import xyz.qweru.pulse.client.systems.modules.settings.impl.*;

import static xyz.qweru.pulse.client.utils.render.ModelRenderer.SINE_45_DEGREES;

public class Chams extends ClientModule {
    public Chams() {
        builder(this)
                .name("Chams")
                .description("Change rendering")
                .settings("Crystal", endCrystal, crystalColor, crystalTextureMode, staticCrystal)
                .settings("Item", heldItems, itemColor, itemTextureMode, rainbow)
                .settings("Player", players, playerColor, playerTextureMode)
                .settings("Misc", texture)
                .category(Category.RENDER);
    }

    public ColorSetting itemColor = new ColorSettingBuilder()
            .setName("Item color")
            .setDescription("Color for held items")
            .setShouldShow(true)
            .setRed(255).setGreen(255).setBlue(255).setAlpha(255)
            .build();

    public ColorSetting crystalColor = new ColorSettingBuilder()
            .setName("Crystal color")
            .setDescription("Color for crystals")
            .setShouldShow(true)
            .setRed(255).setGreen(255).setBlue(255).setAlpha(255)
            .build();

    public ColorSetting playerColor = new ColorSettingBuilder()
            .setName("Player color")
            .setDescription("Color for players")
            .setShouldShow(true)
            .setRed(255).setGreen(255).setBlue(255).setAlpha(255)
            .build();

    public BooleanSetting endCrystal = new BooleanSetting("Crystals", "Change crystal rendering", true, true);
    public BooleanSetting rainbow = new BooleanSetting("Rainbow items", "omg colors", false, true);
    public BooleanSetting players = new BooleanSetting("Players", "Change player rendering", true, true);
    public BooleanSetting heldItems = new BooleanSetting("Hands", "Change hand / held item rendering", true, true);
    public ModeSetting playerTextureMode = modeSetting()
            .name("Player texture")
            .description("Player texture mode")
            .defaultMode("None")
            .mode("Default")
            .mode("Custom")
            .mode("None")
            .build();

    public ModeSetting crystalTextureMode = modeSetting()
            .name("Crystal texture")
            .description("Crystal texture mode")
            .defaultMode("None")
            .mode("Default")
            .mode("Custom")
            .mode("None")
            .build();

    public ModeSetting itemTextureMode = modeSetting()
            .name("Item texture")
            .description("Item texture mode")
            .defaultMode("Custom")
            .mode("Default")
            .mode("Custom")
            .build();

    public BooleanSetting staticCrystal = new BooleanSetting("Static crystal", "stop crystal from moving", false, true);

    public ModeSetting texture = modeSetting()
            .name("Custom texture")
            .description("Texture mode")
            .defaultMode("Nebula 1")
            .mode("Nebula 2")
            .mode("Nebula 3")
            .mode("Nebula 4")
            .mode("Moon phases")
            .mode("Nebula 1")
            .build();

    public Identifier getTextureID() {
        return switch (texture.getCurrent()) {
            case "Nebula 1" -> Identifier.of("pulse", "images/chams.png");
            case "Nebula 2" -> Identifier.of("pulse", "images/chams2.jpg");
            case "Nebula 3" -> Identifier.of("pulse", "images/chams3.jpg");
            case "Nebula 4" -> Identifier.of("pulse", "images/chams4.jpg");
            case "Moon phases" -> Identifier.of("pulse", "images/chams-moonphase.png");
            default -> throw new IllegalStateException("Unexpected value: " + texture.getCurrent());
        };
    }

    static Identifier ct = Identifier.of("textures/entity/end_crystal/end_crystal.png");

    public void renderCrystal(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, int i, ModelPart core, ModelPart frame) {
        float pulse$scale = ModuleManager.INSTANCE.getItemByClass(CrystalTweaks.class).isEnabled() ?
                ((CrystalTweaks) ModuleManager.INSTANCE.getItemByClass(CrystalTweaks.class)).scale.getValue() : 1;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        BufferBuilder buffer;

        if (!crystalTextureMode.is("None")) {
            if (crystalTextureMode.is("Default")) {
                RenderSystem.setShaderTexture(0, ct);
            } else {
                RenderSystem.setShaderTexture(0, getTextureID());
            }
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionProgram);
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        }

        matrixStack.push();
        float h = getYOffset(endCrystalEntity, g);
        float j = staticCrystal.isEnabled() ? 45f : ((float) endCrystalEntity.endCrystalAge + g) * 3.0f;
        matrixStack.push();
        RenderSystem.setShaderColor(crystalColor.getRed().asFloat(), crystalColor.getGreen().asFloat(), crystalColor.getBlue().asFloat(), crystalColor.getAlpha().asFloat());

        matrixStack.scale(2.0F * pulse$scale, 2.0F * pulse$scale, 2.0F * pulse$scale);
        matrixStack.translate(0.0f, -0.5f, 0.0f);
        int k = OverlayTexture.DEFAULT_UV;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.translate(0.0f, 1.5f + h / 2.0f, 0.0f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        frame.render(matrixStack, buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        frame.render(matrixStack, buffer, i, k);
        matrixStack.scale(0.875f, 0.875f, 0.875f);
        matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        core.render(matrixStack, buffer, i, k);
        matrixStack.pop();
        matrixStack.pop();
        BuiltBuffer builtBuffer = buffer.endNullable();
        if (builtBuffer != null)
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
    }

    float getYOffset(EndCrystalEntity crystal, float tickDelta) {
        float f = staticCrystal.isEnabled() ? 0.9f : (float)crystal.endCrystalAge + tickDelta;
        float g = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
        g = (g * g + g) * 0.4F;
        return g - 1.4F;
    }

    public void renderPlayer(PlayerEntity pe, float f, float g, MatrixStack matrixStack, int i, EntityModel model, CallbackInfo ci) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
        BufferBuilder buffer;

        if(playerTextureMode.is("Custom"))
            RenderSystem.setShaderTexture(0, getTextureID());
        else
            RenderSystem.setShaderTexture(0, ((AbstractClientPlayerEntity) pe).getSkinTextures().texture());

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        float n;
        Direction direction;
        Entity entity;
        matrixStack.push();

//        if (PulseClient.friendSystem.isPlayerInSystem(pe.getGameProfile().getName())) {
//            RenderSystem.setShaderColor(friendColor.getValue().getGlRed(), friendColor.getValue().getGlGreen(), friendColor.getValue().getGlBlue(), friendColor.getValue().getGlAlpha());
//        } else {
        RenderSystem.setShaderColor(playerColor.getRed().asFloat(), playerColor.getGreen().asFloat(), playerColor.getBlue().asFloat(), playerColor.getAlpha().asFloat());
//        }

        model.handSwingProgress = pe.getHandSwingProgress(g);
        model.riding = pe.hasVehicle();
        model.child = false;
        float h = MathHelper.lerpAngleDegrees(g, pe.prevBodyYaw, pe.bodyYaw);
        float j = MathHelper.lerpAngleDegrees(g, pe.prevHeadYaw, pe.headYaw);
        float k = j - h;
        if (pe.hasVehicle() && (entity = pe.getVehicle()) instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity) entity;
            h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
            k = j - h;
            float l = MathHelper.wrapDegrees(k);
            if (l < -85.0f) {
                l = -85.0f;
            }
            if (l >= 85.0f) {
                l = 85.0f;
            }
            h = j - l;
            if (l * l > 2500.0f) {
                h += l * 0.2f;
            }
            k = j - h;
        }
        float m = MathHelper.lerp(g, pe.prevPitch, pe.getPitch());
        if (LivingEntityRenderer.shouldFlipUpsideDown(pe)) {
            m *= -1.0f;
            k *= -1.0f;
        }
        if (pe.isInPose(EntityPose.SLEEPING) && (direction = pe.getSleepingDirection()) != null) {
            n = pe.getEyeHeight(EntityPose.STANDING) - 0.1f;
            matrixStack.translate((float) (-direction.getOffsetX()) * n, 0.0f, (float) (-direction.getOffsetZ()) * n);
        }
        float l = pe.age + g;

        setupTransforms1(pe, matrixStack, l, h, g);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);

        matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);

        n = 0.0f;
        float o = 0.0f;
        if (!pe.hasVehicle() && pe.isAlive()) {
            n = pe.limbAnimator.getSpeed(g);
            o = pe.limbAnimator.getPos(g);
            if (pe.isBaby())
                o *= 3.0f;

            if (n > 1.0f)
                n = 1.0f;
        }
        model.animateModel(pe, o, n, g);
        model.setAngles(pe, o, n, l, k, m);
        int p = LivingEntityRenderer.getOverlay(pe, 0);
        model.render(matrixStack, buffer, i, p);
        BuiltBuffer builtBuffer = buffer.endNullable();
        if (builtBuffer != null)
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
        RenderSystem.disableBlend();
        RenderSystem.disableCull();
        matrixStack.pop();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableDepthTest();
        if (!playerTextureMode.is("Default")) {
            ci.cancel();
        }
    }

    public void setupTransforms1(PlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h) {
        float j = abstractClientPlayerEntity.getLeaningPitch(h);
        float k = abstractClientPlayerEntity.getPitch(h);
        float l;
        float m;
        if (abstractClientPlayerEntity.isFallFlying()) {
            setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            l = (float) abstractClientPlayerEntity.getFallFlyingTicks() + h;
            m = MathHelper.clamp(l * l / 100.0F, 0.0F, 1.0F);
            if (!abstractClientPlayerEntity.isUsingRiptide()) {
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m * (-90.0F - k)));
            }

            Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
            Vec3d vec3d2 = abstractClientPlayerEntity.getVelocity();
            double d = vec3d2.horizontalLengthSquared();
            double e = vec3d.horizontalLengthSquared();
            if (d > 0.0 && e > 0.0) {
                double n = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double o = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation((float) (Math.signum(o) * Math.acos(n))));
            }
        } else if (j > 0.0F) {
            setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            l = abstractClientPlayerEntity.isTouchingWater() ? -90.0F - k : -90.0F;
            m = MathHelper.lerp(j, 0.0F, l);
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(m));
            if (abstractClientPlayerEntity.isInSwimmingPose()) {
                matrixStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
        }
    }

    private void setupTransforms(PlayerEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        if (!entity.isInPose(EntityPose.SLEEPING)) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
        }

        if (entity.deathTime > 0) {
            float f = ((float) entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 90.0F));
        } else if (entity.isUsingRiptide()) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F - entity.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((float) entity.age + tickDelta) * -75.0F));
        } else if (entity.isInPose(EntityPose.SLEEPING)) {
            Direction direction = entity.getSleepingDirection();
            float g = direction != null ? getYaw(direction) : bodyYaw;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0F));
        }
    }

    private static float getYaw(Direction direction) {
        return switch (direction) {
            case NORTH -> 270.0f;
            case SOUTH -> 90.0f;
            case EAST -> 180.0f;
            default -> 0.0f;
        };
    }

}
