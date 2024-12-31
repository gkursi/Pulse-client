package xyz.qweru.pulse.client.systems.modules.impl.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.TextSetting;
import xyz.qweru.pulse.client.utils.InputUtil;

import java.io.*;


public class CoolCape extends ClientModule {

    private static Identifier capeTextures;

    public CoolCape() {
        builder(this)
                .name("Cape")
                .description("Set your cape")
                .bind(InputUtil.KEY_UNKNOWN)
                .category(Category.MISC)
                .settings("Path", pathCape);
    }

    TextSetting pathCape = textSetting()
            .name("Path")
            .description("Path to cape texture")
            .defaultValue("D:\\cape.png")
            .build();



    @Override
    public void enable() {
        try {
            File capePath = new File(pathCape.getValue());
            NativeImage cape = NativeImage.read(new FileInputStream(capePath));
            capeTextures = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("pulse", new NativeImageBackedTexture(parseCape(cape)));
        } catch (Exception ignored) {
            this.toggle();
            PulseClient.LOGGER.error("Failed to load cape texture");
        }

    }

    @Override
    public void disable() {
        capeTextures = null;
    }

    public static Identifier getCapeTextures() {
        return capeTextures;
    }


    public static NativeImage parseCape(NativeImage image) {
        int imageWidth = 64;
        int imageHeight = 32;
        int imageSrcWidth = image.getWidth();
        int srcHeight = image.getHeight();

        for (int imageSrcHeight = image.getHeight(); imageWidth < imageSrcWidth
                || imageHeight < imageSrcHeight; imageHeight *= 2) {
            imageWidth *= 2;
        }

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                imgNew.setColor(x, y, image.getColor(x, y));
            }
        }
        image.close();
        return imgNew;
    }


}
