package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
	@Shadow @Nullable private SplashTextRenderer splashText;

	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
//		ExampleMod.LOGGER.info("This line is printed by an example mod mixins!");
		this.splashText = new SplashTextRenderer("Pulse on top!");
	}
}
