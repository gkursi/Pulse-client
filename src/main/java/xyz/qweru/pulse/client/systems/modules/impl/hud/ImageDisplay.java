package xyz.qweru.pulse.client.systems.modules.impl.hud;

import me.x150.renderer.render.Renderer2d;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.systems.modules.Category;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.settings.impl.ModeSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.NumberSetting;
import xyz.qweru.pulse.client.systems.modules.settings.impl.TextSetting;
import xyz.qweru.pulse.client.utils.render.TextureUtil;
import xyz.qweru.pulse.client.utils.timer.TimerUtil;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageDisplay extends HudModule {

    TimerUtil timer = new TimerUtil();

    TextSetting path = textSetting()
            .name("Path")
            .description("Path to image / gif")
            .defaultValue("D:\\images.png")
            .build();
    ModeSetting mode = modeSetting()
            .name("Mode")
            .description("How should the file be read")
            .defaultMode("png/jpg/jpeg")
            .mode("gif")
            .mode("png/jpg/jpeg")
            .build();
    NumberSetting delayMS = numberSetting()
            .name("GIF delay")
            .description("GIF frame delay in ms")
            .range(0, 5000)
            .defaultValue(200)
            .build();

    public ImageDisplay() {
        hudBuilderOf(this)
                .pos(2, 2)
                .area(50, 50)
                .getBuilder()
                .name("Image Display")
                .description("Displays images / gifs")
                .category(Category.HUD)
                .settings("File", path)
                .settings("Settings", mode, delayMS);
        timer.reset();

        path.addOnToggle(() -> {
            gifIndex = 0;
            BufferedImage img;
            try
            {
                img = ImageIO.read(new File(path.getValue()));
            }
            catch (IOException e)
            {
                PulseClient.throwException(e);
                return;
            }
            TextureUtil.registerBufferedImageTexture(Identifier.of("pulse", "memory/imgdisplay.png"), img);
        });
    }

    int gifIndex = 0;
    @Override
    public void render(DrawContext drawContext, float delta, RenderContext context) {
        if(path.getValue() == "") return;
        switch (mode.getCurrent()) {
            case "gif" -> drawGifFrames(new File(path.getValue()), drawContext);
            case "png/jpg/jpeg" -> Renderer2d.renderTexture(context.getMatrices(), Identifier.of("pulse", "memory/imgdisplay.png"), this.x, this.y, this.width, this.height);
        }
    }

    // todo: optimize
    void drawGifFrames(File file, DrawContext context) {
        try {
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            ImageInputStream ciis = ImageIO.createImageInputStream(file);
            reader.setInput(ciis, false);

            int noi = reader.getNumImages(true);
            if(gifIndex >= noi) {
                PulseClient.LOGGER.warn("gif end: {} >= {}", noi, gifIndex);
                gifIndex = 0;
            }
            BufferedImage image = reader.read(gifIndex);
            if(image != null) {
                TextureUtil.registerBufferedImageTexture(Identifier.of("pulse", "memory/imgdisplay-gif.png"), image);
                Renderer2d.renderTexture(context.getMatrices(), Identifier.of("pulse", "memory/imgdisplay-gif.png"), this.x, this.y, this.width, this.height);
            }
            if(timer.hasReached(delayMS.getValue())) {
                gifIndex++;
                timer.reset();
            }
        } catch (IllegalStateException ignored) {}
        catch (Exception e) {
            PulseClient.throwException(e);
        }
    }
}
