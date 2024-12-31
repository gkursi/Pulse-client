package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.qweru.pulse.client.mixin.iinterface.IFontManager;

import java.util.Map;

@Mixin(FontManager.class)
public class FontManagerMixin implements IFontManager {
    @Shadow @Final private Map<Identifier, FontStorage> fontStorages;

    @Shadow @Final private FontStorage missingStorage;

    @Override
    public TextRenderer pulse$createRenderer(Identifier fontID) {
        return new TextRenderer(id -> this.fontStorages.getOrDefault(fontID, this.missingStorage), false);
    }
}
