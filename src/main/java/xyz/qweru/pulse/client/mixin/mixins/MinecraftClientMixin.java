package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.mixin.iinterface.IFontManager;
import xyz.qweru.pulse.client.systems.events.TickEvent;
import xyz.qweru.pulse.client.systems.events.WorldTickEvent;
import xyz.qweru.pulse.client.systems.modules.impl.hud.EnderChest;
import xyz.qweru.pulse.client.systems.modules.impl.render.ESP;
import xyz.qweru.pulse.client.utils.callbacks.WindowResizeCallback;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

import static xyz.qweru.pulse.client.PulseClient.mc;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Nullable public ClientWorld world;

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Final private FontManager fontManager;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void preTick(CallbackInfo ci) {
        if(PulseClient.Events.post(new TickEvent.Pre()).isCancelled()) ci.cancel();
        if(this.world != null && this.player != null) if(PulseClient.Events.post(new WorldTickEvent.Pre()).isCancelled()) ci.cancel();

    }

    @Inject(method = "tick", at = @At("TAIL"), cancellable = true)
    private void postTick(CallbackInfo ci) {
        if(PulseClient.Events.post(new TickEvent.Post()).isCancelled()) ci.cancel();
        if(this.world != null && this.player != null) if(PulseClient.Events.post(new WorldTickEvent.Post()).isCancelled()) ci.cancel();
    }

    @Inject(method = "hasOutline", at = @At("TAIL"), cancellable = true)
    private void render(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if(ModuleManager.INSTANCE.getItemByClass(ESP.class).isEnabled()
                && ((ESP) ModuleManager.INSTANCE.getItemByClass(ESP.class)).mode.getCurrent().toLowerCase().equals("glow")
                && ((ESP) ModuleManager.INSTANCE.getItemByClass(ESP.class)).isValid(entity)
        ) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onResolutionChanged", at = @At("RETURN"))
    void a(CallbackInfo ci) {
        WindowResizeCallback.EVENT.invoker().onResized(mc, mc.getWindow());
    }

    @Inject(method = "setScreen", at = @At("TAIL"))
    void setScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof GenericContainerScreen containerScreen) {
            Text title = containerScreen.getTitle();
            if (title.getString().equals("Ender Chest")) {
                EnderChest.ENDER_CHEST = containerScreen.getScreenHandler().getInventory();
            }
        }
    }

    @Inject(method = "onFontOptionsChanged", at = @At("TAIL"))
    public void initFont(CallbackInfo ci) {
        IFontManager fonts = (IFontManager) this.fontManager;

//        RenderUtil.initFont(fonts);
    }
}
