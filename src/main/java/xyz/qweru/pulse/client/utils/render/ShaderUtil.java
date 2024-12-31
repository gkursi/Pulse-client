package xyz.qweru.pulse.client.utils.render;

import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;

import java.nio.charset.StandardCharsets;

public class ShaderUtil {
//    public static final ManagedShaderEffect INVERT_SHADER = ShaderEffectManager.getInstance()
//            .manage(Identifier.of("pulse", "shaders/post/invert.json"));
//
//    public static final ManagedShaderEffect TEST_SHADER = ShaderEffectManager.getInstance()
//            .manage(Identifier.of("pulse", "shaders/post/test.json"));

    public static void init() {}

    /**
     * Register a fragment shader (saved as Identifier.of("pulse", "shaders/program/dynamic_" + name + ".fsh"))
     * @param content content of the file
     * @param name name of the file
     * @return an identifier for the file
     */
    public static Identifier registerFragmentProgram(String content, String name) {
        Identifier id = Identifier.of("pulse", "shaders/program/dynamic_" + name + ".fsh");
        registerTextFile(id, content);
        return id;
    }

    /**
     * Register a vertex shader (saved as Identifier.of("pulse", "shaders/program/dynamic_" + name + ".vsh"))
     * @param content content of the file
     * @param name name of the file
     * @return an identifier for the file
     */
    public static Identifier registerVertexProgram(String content, String name) {
        Identifier id = Identifier.of("pulse", "shaders/program/dynamic_" + name + ".vsh");
        registerTextFile(id, content);
        return id;
    }

    public static void registerTextFile(Identifier identifier, String content) {
        TextureUtil.registerTexture(identifier, content.getBytes(StandardCharsets.UTF_8));
    }
}
