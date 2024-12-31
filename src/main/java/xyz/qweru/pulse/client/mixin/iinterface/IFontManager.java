package xyz.qweru.pulse.client.mixin.iinterface;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

public interface IFontManager {
    TextRenderer pulse$createRenderer(Identifier fontID);
}
