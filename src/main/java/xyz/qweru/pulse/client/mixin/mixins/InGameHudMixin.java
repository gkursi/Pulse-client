package xyz.qweru.pulse.client.mixin.mixins;

import me.x150.renderer.render.MSAAFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.render.renderer.RenderContext;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.events.HudRenderEvent;
import xyz.qweru.pulse.client.systems.modules.ClientModule;
import xyz.qweru.pulse.client.systems.modules.HudModule;
import xyz.qweru.pulse.client.systems.modules.impl.hud.BetterHotbar;
import xyz.qweru.pulse.client.systems.modules.impl.setting.ClickGUI;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow @Final private DebugHud debugHud;

    @Inject(method = "render", at = @At("TAIL"))
    private void renderHUD(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(PulseClient.Events.post(new HudRenderEvent.Hud(context, tickCounter.getTickDelta(true))).isCancelled()) ci.cancel();
        if(!debugHud.shouldShowDebugHud()) {
            float tickDelta = tickCounter.getTickDelta(true);
            if(ClickGUI.MSAASamples.is("Disabled")) {
                for(ClientModule m : ModuleManager.INSTANCE.getItemList()) {
                    if(m instanceof HudModule && m.isEnabled()) {
                        ((HudModule) m).render(context, tickDelta,
                                new RenderContext(context.getMatrices(), context, 0, 0,
                                        context.getScaledWindowWidth(), context.getScaledWindowHeight(), tickDelta, ThemeInfo.COLORSCHEME, null));
                    }
                }
                return;
            }

            try {
                MSAAFramebuffer.use(Integer.parseInt(ClickGUI.MSAASamples.getCurrent()), () -> {
                    for(ClientModule m : ModuleManager.INSTANCE.getItemList()) {
                        if(m instanceof HudModule && m.isEnabled()) {
                            ((HudModule) m).render(context, tickDelta,
                                    new RenderContext(context.getMatrices(), context, 0, 0,
                                            context.getScaledWindowWidth(), context.getScaledWindowHeight(), tickDelta, ThemeInfo.COLORSCHEME, null));
                        }
                    }
                });
            } catch (Exception e) {
                ClickGUI.MSAASamples.setMode("Disabled");
                ClickGUI.ICON_MSAA.setState(false);
                throw new RuntimeException(e);
            }
        }
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    void potRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(PulseClient.Events.post(new HudRenderEvent.Potion()).isCancelled()) ci.cancel();
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    void vignette(DrawContext context, Entity entity, CallbackInfo ci) {
        if(PulseClient.Events.post(new HudRenderEvent.Vignette()).isCancelled()) ci.cancel();
    }

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    void inWall(DrawContext context, Identifier texture, float opacity, CallbackInfo ci) {
        if(PulseClient.Events.post(new HudRenderEvent.WallOverlay()).isCancelled()) ci.cancel();
    }

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    void hotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if(Managers.MODULE.getItemByClass(BetterHotbar.class).isEnabled()) ci.cancel();
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    void expBar(DrawContext context, int x, CallbackInfo ci) {
        if(Managers.MODULE.getItemByClass(BetterHotbar.class).isEnabled()) ci.cancel();
    }

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true)
    void hp(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        // todo
        //        if(Managers.MODULE.getItemByClass(BetterHotbar.class).isEnabled()) {
//            BetterHotbar.renderHealth(context, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
//            ci.cancel();
//        }
    }
}
