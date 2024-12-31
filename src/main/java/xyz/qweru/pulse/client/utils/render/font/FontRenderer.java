package xyz.qweru.pulse.client.utils.render.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.minecraft.client.render.*;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Vec2f;
import org.apache.commons.codec.binary.Base64;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.systems.modules.impl.setting.ClickGUI;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

/**
 * @author Nahr.
 * @author nuf
 */
public class FontRenderer {

    private Font theFont;
    private Graphics2D theGraphics;
    private FontMetrics theMetrics;
    private final float fontSize;
    private final int startChar;
    private final int endChar;
    private final float[] xPos;
    private final float[] yPos;
    public BufferedImage bufferedImage;
    public Identifier resourceLocation;
    private final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OG]"), patternUnsupported = Pattern.compile("(?i)\\u00A7[L-O]");

    public FontRenderer(Object font, Identifier fontPath, float size) {
        this(font, fontPath, size, 0F);
    }

    public FontRenderer(Object font, Identifier fontPath) {
        this(font, fontPath, 18F, 0F);
    }

    /**
     * Creates a font renderer with the given parameters
     * @param font The font to render
     * @param fontPath The folder that the font is stored in
     * @param size The size of the font
     * @param spacing The spacing between the characters
     */
    public FontRenderer(Object font, Identifier fontPath, float size, float spacing) {
        this.fontSize = size;
        this.startChar = 32;
        this.endChar = 255;
        this.xPos = new float[this.endChar - this.startChar];
        this.yPos = new float[this.endChar - this.startChar];
        setupGraphics2D();
        createFont(font, size, fontPath);
    }

    private void setupGraphics2D() {
        this.bufferedImage = new BufferedImage(256, 256, 2);
        this.theGraphics = ((Graphics2D) this.bufferedImage.getGraphics());
        this.theGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private void createFont(Object font, float size, Identifier fontPath) {
        try {
            if (font instanceof Font)
                this.theFont = ((Font) font);
            else if (font instanceof File)
                this.theFont = Font.createFont(0, (File) font).deriveFont(size);
            else if (font instanceof InputStream)
                this.theFont = Font.createFont(0, (InputStream) font).deriveFont(size);
            else if (font instanceof Identifier) {
                String name = ((Identifier)font).getPath();
                if (name.toLowerCase().endsWith("ttf") || name.endsWith("otf"))
                    this.theFont = Font.createFont(0, Objects.requireNonNull(FontRenderer.class.getResourceAsStream(File.separator + "assets" + File.separator + ((Identifier) font).getNamespace() + File.separator + name))).deriveFont(size);
                else
                    this.theFont = new Font(name, Font.PLAIN, Math.round(size));
            } else if (font instanceof String) {
                this.theFont = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(FontRenderer.class.getResourceAsStream("/assets/pulse/" + fontPath.getPath() + "/" + font))).deriveFont(size);
            } else
                this.theFont = new Font("Verdana", Font.PLAIN, Math.round(size));
            this.theGraphics.setFont(this.theFont);
        } catch (Exception e) {
            PulseClient.throwException(e);
            PulseClient.LOGGER.error("Error loading font: {}", font.toString());
            this.theFont = new Font("Verdana", Font.PLAIN, Math.round(size));
            this.theGraphics.setFont(this.theFont);
        }
        this.theGraphics.setColor(new Color(255, 255, 255, 0));
        this.theGraphics.fillRect(0, 0, 256, 256);
        this.theGraphics.setColor(Color.white);
        this.theMetrics = this.theGraphics.getFontMetrics();

        float x = 5.0F;
        float y = 5.0F;
        for (int i = this.startChar; i < this.endChar; i++) {
            this.theGraphics.drawString(Character.toString((char) i), x, y + this.theMetrics.getAscent());
            this.xPos[(i - this.startChar)] = x;
            this.yPos[(i - this.startChar)] = (y - this.theMetrics.getMaxDescent());
            x += this.theMetrics.stringWidth(Character.toString((char) i)) + 2.0F;
            if (x >= 250 - this.theMetrics.getMaxAdvance()) {
                x = 5.0F;
                y += this.theMetrics.getMaxAscent() + this.theMetrics.getMaxDescent() + this.fontSize / 2.0F;
            }
        }
        String base64 = imageToBase64String(bufferedImage, "png");
        this.setResourceLocation(base64, theFont, fontPath, size);
    }

    private String imageToBase64String(BufferedImage image, String type) {
        String ret = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, type, bos);
            byte[] bytes = bos.toByteArray();
            Base64 encoder = new Base64();
            ret = encoder.encodeAsString(bytes);
            ret = ret.replace(System.lineSeparator(), "");
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return ret;
    }

    private void setResourceLocation(String base64, Object font, Identifier identifier, float size) {
        NativeImage image = readTexture(base64);
        if (image == null) return;

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                imgNew.setColor(x, y, image.getColor(x, y));
            }
        }

        image.close();
        this.resourceLocation = Identifier.of(identifier.getNamespace(), identifier.getPath() + getFont().getFontName().toLowerCase().replace(" ", "-") + size);
        applyTexture(resourceLocation, imgNew);
    }

    private static NativeImage readTexture(String textureBase64) {
        try {
            byte[] imgBytes = Base64.decodeBase64(textureBase64);
            ByteArrayInputStream bais = new ByteArrayInputStream(imgBytes);
            return NativeImage.read(bais);
        } catch (IOException e) {
            PulseClient.throwException(e);

            return null;
        }
    }

    private void applyTexture(Identifier identifier, NativeImage nativeImage) {
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(nativeImage)));
    }

    public void drawString(MatrixStack matrixStack, String text, double x, double y, int color) {
        drawString(matrixStack, text, (float) x, (float) y, FontType.NORMAL, color);
    }

    public void drawOrderedText(MatrixStack matrixStack, OrderedText text, double x, double y) {
        FontRenderer.ColoredString string = FontRenderer.ColoredString.of("", Color.GREEN);

        text.accept((index, style, codePoint) -> {
            String ch = new StringBuilder().appendCodePoint(codePoint).toString();
            Color col = style.getColor() == null ? Color.WHITE : new Color(style.getColor().getRgb());
            string.add(ch, col);
            return true;
        });

        RenderUtil.textRenderer.drawColoredString(matrixStack, string, (float) x, (float) (y + RenderUtil.fontOffsetY));
    }

    public void drawText(MatrixStack matrixStack, Text text, double x, double y) {
        drawOrderedText(matrixStack, text.asOrderedText(), x, y);
    }

    public void drawString(MatrixStack matrixStack, String text, float x, float y, int color) {
        drawString(matrixStack, text, x, y, FontType.NORMAL, color);
    }

    public void drawString(MatrixStack matrixStack, String text, float x, float y, FontType fontType, int color, int color2) {
        if(text.isEmpty()) return;
        if(ClickGUI.fontMode.is("All caps")) text = text.toUpperCase();
        else if(ClickGUI.fontMode.is("All lowercase")) text = text.toLowerCase();
        text = stripUnsupported(text);

        Renderer.setup2DRender(false);
        String text2 = stripControlCodes(text);
        switch (fontType.ordinal()) {
            case 1:
                drawer(matrixStack, text2, x + 0.5F, y, color2);
                drawer(matrixStack, text2, x - 0.5F, y, color2);
                drawer(matrixStack, text2, x, y + 0.5F, color2);
                drawer(matrixStack, text2, x, y - 0.5F, color2);
                break;
            case 2:
                drawer(matrixStack, text2, x + 0.5F, y + 0.5F, color2);
                break;
            case 3:
                drawer(matrixStack, text2, x + 0.5F, y + 1.0F, color2);
                break;
            case 4:
                drawer(matrixStack, text2, x, y + 0.5F, color2);
                break;
            case 5:
                drawer(matrixStack, text2, x, y - 0.5F, color2);
                break;
            case 6:
                break;
        }

        drawer(matrixStack, text, x, y, color);
        Renderer.end2DRender();
    }

    public void drawString(MatrixStack matrixStack, String text, float x, float y, FontType fontType, int color) {
        matrixStack.scale(0.5f, 0.5f, 1);
        drawString(matrixStack, text, x, y, fontType, color, 0xBB000000);
        matrixStack.scale(2f, 2f, 1);
    }

    BufferBuilder tempBB = null;
    private void drawer(MatrixStack matrixStack, String text, float x, float y, int color) {
        x *= 2.0F;
        y *= 2.0F;
        Renderer.setup2DRender(false);
        Renderer.bindTexture(this.resourceLocation);

        if ((color & -67108864) == 0)
        {
            color |= -16777216;
        }

        int newColor = color;
        float startX = x;
        boolean scramble = false;
        Tessellator tessellator = Tessellator.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        tempBB = bufferBuilder;
        // RenderSystem.setShader(GameRenderer::getPositionTexColorShader);


        for (int i = 0; i < text.length(); i++)
            if ((text.charAt(i) == '\247') && (i + 1 < text.length())) {
                char oneMore = Character.toLowerCase(text.charAt(i + 1));
                if (oneMore == 'n') {
                    y += this.theMetrics.getAscent() + 2;
                    x = startX;
                }else if (oneMore == 'k') {
                    scramble = true;
                } else if (oneMore == 'r')
                    newColor = color;
                else {
                    newColor = getColorFromCode(oneMore);
                }
                i++;
            } else {
                try {
                    String obfText = "\\:><&%$@!/?";
                    char c = scramble ? obfText.charAt((int)(new Random().nextFloat() * (obfText.length() - 1))) : text.charAt(i);
                    drawChar(matrixStack, c, x, y, newColor);
                    x += getStringWidth(Character.toString(c), false) * 2.0F;
                } catch (ArrayIndexOutOfBoundsException indexException) {
                    char c = text.charAt(i);
                    System.err.println("Can't draw character: " + c + " (" + Character.getNumericValue(c) + ")");
                }
            }
        try {
            BuiltBuffer b = bufferBuilder.end();
            if(b != null) BufferRenderer.drawWithGlobalProgram(b);
            Renderer.shaderColor(0xffffffff);
            tempBB = null;
        } catch (Exception ignored) {}

    }

    private Rectangle2D getBounds(String text) {
        return this.theMetrics.getStringBounds(text, this.theGraphics);
    }

    private void drawChar(MatrixStack matrixStack, char character, float x, float y, int color) throws ArrayIndexOutOfBoundsException {
        Rectangle2D bounds = this.theMetrics.getStringBounds(Character.toString(character), this.theGraphics);
        drawTexturedModalRect(matrixStack, x, y, this.xPos[(character - this.startChar)], this.yPos[(character - this.startChar)], (float) bounds.getWidth(), (float) bounds.getHeight() + this.theMetrics.getMaxDescent() + 1.0F, color);
    }

    @SuppressWarnings("unused")
    private List<String> listFormattedStringToWidth(String s, int width) {
        return Arrays.asList(wrapFormattedStringToWidth(s, width).split("\n"));
    }

    private String wrapFormattedStringToWidth(String s, float width) {
        int wrapWidth = sizeStringToWidth(s, width);

        if (s.length() <= wrapWidth) {
            return s;
        }
        String split = s.substring(0, wrapWidth);
        String split2 = getFormatFromString(split)
                + s.substring(wrapWidth + ((s.charAt(wrapWidth) == ' ') || (s.charAt(wrapWidth) == '\n') ? 1 : 0));
        try {
            return split + "\n" + wrapFormattedStringToWidth(split2, width);
        } catch (Exception e) {
            System.err.println("Cannot wrap string to width.");
        }
        return "";
    }

    private int sizeStringToWidth(String par1Str, float par2) {
        int var3 = par1Str.length();
        float var4 = 0.0F;
        int var5 = 0;
        int var6 = -1;

        for (boolean var7 = false; var5 < var3; var5++) {
            char var8 = par1Str.charAt(var5);

            switch (var8) {
                case '\n':
                    var5--;
                    break;
                case '\247':
                    if (var5 < var3 - 1) {
                        var5++;
                        char var9 = par1Str.charAt(var5);

                        if ((var9 != 'l') && (var9 != 'L')) {
                            if ((var9 == 'r') || (var9 == 'R') || (isFormatColor(var9)))
                                var7 = false;
                        } else
                            var7 = true;
                    }
                    break;
                case ' ':
                    var6 = var5;
                case '-':
                    var6 = var5;
                case '_':
                    var6 = var5;
                case ':':
                    var6 = var5;
                default:
                    String text = String.valueOf(var8);
                    var4 += getStringWidth(text, false);

                    if (var7) {
                        var4 += 1.0F;
                    }
                    break;
            }
            if (var8 == '\n') {
                var5++;
                var6 = var5;
            } else {
                if (var4 > par2) {
                    break;
                }
            }
        }
        return (var5 != var3) && (var6 != -1) && (var6 < var5) ? var6 : var5;
    }

    private String getFormatFromString(String par0Str) {
        String var1 = "";
        int var2 = -1;
        int var3 = par0Str.length();

        while ((var2 = par0Str.indexOf('\247', var2 + 1)) != -1) {
            if (var2 < var3 - 1) {
                char var4 = par0Str.charAt(var2 + 1);

                if (isFormatColor(var4))
                    var1 = "\247" + var4;
                else if (isFormatSpecial(var4)) {
                    var1 = var1 + "\247" + var4;
                }
            }
        }

        return var1;
    }

    private boolean isFormatColor(char par0) {
        return ((par0 >= '0') && (par0 <= '9')) || ((par0 >= 'a') && (par0 <= 'f')) || ((par0 >= 'A') && (par0 <= 'F'));
    }

    private boolean isFormatSpecial(char par0) {
        return ((par0 >= 'k') && (par0 <= 'o')) || ((par0 >= 'K') && (par0 <= 'O')) || (par0 == 'r') || (par0 == 'R');
    }

    private void drawTexturedModalRect(MatrixStack matrixStack, float x, float y, float u, float v, float width, float height, int color) {
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        float scale = 0.0039063F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tempBB;
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        bufferBuilder.vertex(matrix4f, x + 0.0F, y + height, 0.0f).texture((u + 0.0F) * scale, (v + height) * scale).color(f1, f2, f3, f);
        bufferBuilder.vertex(matrix4f, x + width, y + height, 0.0f).texture((u + width) * scale, (v + height) * scale).color(f1, f2, f3, f);
        bufferBuilder.vertex(matrix4f, x + width, y + 0.0F, 0.0f).texture((u + width) * scale, (v + 0.0F) * scale).color(f1, f2, f3, f);
        bufferBuilder.vertex(matrix4f, x + 0.0F, y + 0.0F, 0.0f).texture((u + 0.0F) * scale, (v + 0.0F) * scale).color(f1, f2, f3, f);
    }

    public final String stripControlCodes(String s) {
        return this.patternControlCode.matcher(s).replaceAll("");
    }

    public final String stripUnsupported(String s) {
        return this.patternUnsupported.matcher(s).replaceAll("");
    }

    public final Font getFont() {
        return theFont;
    }

    private int getColorFromCode(char code) {
        return switch (code) {
            case '0' -> Color.BLACK.getRGB();
            case '1' -> 0xff0000AA;
            case '2' -> 0xff00AA00;
            case '3' -> 0xff00AAAA;
            case '4' -> 0xffAA0000;
            case '5' -> 0xffAA00AA;
            case '6' -> 0xffFFAA00;
            case '7' -> 0xffAAAAAA;
            case '8' -> 0xff555555;
            case '9' -> 0xff5555FF;
            case 'a' -> 0xff55FF55;
            case 'b' -> 0xff55FFFF;
            case 'c' -> 0xffFF5555;
            case 'd' -> 0xffFF55FF;
            case 'e' -> 0xffFFFF55;
            case 'f' -> 0xffffffff;
            case 'g' -> 0xffDDD605;
            default -> -1;
        };
    }

    public enum FontType {
        NORMAL, SHADOW_THICK, SHADOW_THIN, OUTLINE_THIN, EMBOSS_TOP, EMBOSS_BOTTOM
    }

    private final static MinecraftClient mc = MinecraftClient.getInstance();

    public float getStringWidth(String string, boolean mcFont) {
        if (!mcFont)
            return (float) (getBounds(this.stripControlCodes(string)).getWidth()) / 2F;
        else
            return mc.textRenderer.getWidth(string);
    }

    public float getWidth(String s) {
        return getStringWidth(s, false);
    }

    public float getHeight(String s) {
        return getStringHeight(s, false);
    }

    public float getStringHeight(String string, boolean mcFont) {
        if (!mcFont)
            return (float) (getBounds((this.stripControlCodes(string))).getHeight() / 2.0F);
        else
            return mc.textRenderer.fontHeight;
    }

    public float getStringWidth(Text string) {
        return mc.textRenderer.getWidth(string);
    }

    public void drawCenteredString(MatrixStack matrixStack, String string, float x, float y, int color, boolean shadow) {
        float newX = x - ((getStringWidth(string, false)) / 2);
        this.drawString(matrixStack, string, newX, y, shadow ? FontType.SHADOW_THIN : FontType.NORMAL, color);
    }

    public String fix(String s) {
        if (s == null || s.isEmpty())
            return s;
        for (int i = 0; i < 9; i++) {
            if (s.contains("\247" + i))
                s = s.replace("\247" + i, "");
        }
        return s.replace("\247a", "").replace("\247b", "").replace("\247c", "").replace("\247d", "").replace("\247e", "").replace("\247f", "").replace("\247g", "");
    }

    public String trimToWidth(String string, int width) {
        try {
            return string.substring(0, width);
        } catch(Exception e) {
            return string;
        }
    }

    public String trimToWidth(String string, int width, boolean backwards) {
        try {
            return backwards ? string.substring(width) : string.substring(0, width);
        } catch(Exception e) {
            return string;
        }
    }

    private static class Renderer {
        /**
         * Sets properties to render objects in 2d
         */
        public static void setup2DRender(boolean disableDepth) {
            RenderSystem.enableBlend();
            //RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            if (disableDepth)
                RenderSystem.disableDepthTest();
        }

        /**
         * Unsets the properties set in {@link #setup2DRender(boolean)}
         */
        public static void end2DRender() {
            RenderSystem.disableBlend();
            //RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }

        /**
         * Binds the texture to be drawn
         * @param identifier The location of the texture
         */
        public static void bindTexture(Identifier identifier) {
            RenderSystem.setShaderTexture(0, identifier);
        }

        /**
         * Sets the shader color to the given integer rgb value
         * @param rgb The rgb value to use
         */
        public static void shaderColor(int rgb) {
            float alpha = (rgb >> 24 & 0xFF) / 255.0F;
            float red = (rgb >> 16 & 0xFF) / 255.0F;
            float green = (rgb >> 8 & 0xFF) / 255.0F;
            float blue = (rgb & 0xFF) / 255.0F;
            RenderSystem.setShaderColor(red, green, blue, alpha);
        }
    }

    public Vec2f calcPosInBox(String text, float x, float y, float width, float height) {
        Vec2f pos = new Vec2f(x, y);
        float xPad = (width - getWidth(text)) / 2;
        float yPad = (height - getStringHeight(text, false)) / 2;
        return pos.add(new Vec2f(xPad, yPad));
    }

    public void drawColoredString(MatrixStack matrices, ColoredString cs, float x, float y) {
        for (ColoredString.ColoredStringSegment segment : cs.segments) {
            drawString(matrices, segment.text, x, y, segment.color.getRGB());
            x += segment.width();
        }
    }

    /**
     * multicolor strings
     *
     * @author qweru
     */
    public record ColoredString(ArrayList<ColoredStringSegment> segments) {

        public static ColoredString of(String str, Color color) {
            return new ColoredString(new ArrayList<>(Collections.singleton(new ColoredStringSegment(str, color))));
        }

        public ColoredString add(String s, Color color) {
            segments.add(new ColoredStringSegment(s, color));
            return this;
        }

        @FunctionalInterface
        interface ColoredStringRunnable {
            void loop(String string, int color);
        }

        public void loop(ColoredStringRunnable runnable) {
            segments.forEach(coloredStringSegment -> runnable.loop(coloredStringSegment.text, coloredStringSegment.color.getRGB()));
        }

        public float getWidth() {
            float width = 0;
            for (ColoredStringSegment segment : segments) {
                width += segment.width();
            }
            return width;
        }

        public float getHeight() {
            float height = 0;
            for (ColoredStringSegment segment : segments) {
                height = Math.max(segment.height(), height);
            }
            return height;
        }

        public record ColoredStringSegment(String text, Color color) {
            public float width() {
                return RenderUtil.textRenderer.getWidth(text);
            }

            public float height() {
                return RenderUtil.textRenderer.getHeight(text);
            }
        }
    }
}