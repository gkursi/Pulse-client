package xyz.qweru.pulse.client.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.*;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.mixin.iinterface.IChatHud;
import xyz.qweru.pulse.client.render.ui.color.ThemeInfo;
import xyz.qweru.pulse.client.systems.modules.impl.misc.AutoDupe;
import xyz.qweru.pulse.client.systems.modules.impl.misc.Chat;
import xyz.qweru.pulse.client.utils.Util;
import xyz.qweru.pulse.client.utils.render.RenderUtil;

import java.time.LocalDateTime;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IChatHud {
    @Shadow public abstract void addMessage(Text message);

    @Shadow protected abstract boolean isChatHidden();

    @Shadow public abstract int getVisibleLineCount();

    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract double getChatScale();

    @Shadow public abstract int getWidth();

    @Shadow protected abstract int getMessageIndex(double chatLineX, double chatLineY);

    @Shadow protected abstract double toChatLineX(double x);

    @Shadow protected abstract double toChatLineY(double y);

    @Shadow protected abstract int getLineHeight();

    @Shadow private int scrolledLines;

    @Shadow
    private static double getMessageOpacityMultiplier(int age) {
        double d = (double)age / 200.0;
        d = 1.0 - d;
        d *= 10.0;
        d = MathHelper.clamp(d, 0.0, 1.0);
        d *= d;
        return d;
    }

    @Shadow protected abstract int getIndicatorX(ChatHudLine.Visible line);

    @Shadow protected abstract void drawIndicatorIcon(DrawContext context, int x, int y, MessageIndicator.Icon icon);

    @Shadow private boolean hasUnreadNewMessages;

    @Shadow @Final private List<ChatHudLine> messages;

    @Override
    public void pulse$add(Text text) {

        if(messages.isEmpty()) addMessage(text);
        else {
            String[] a = Util.orderedTextToString(text.asOrderedText()).split(" ", 2);
            String[] b = Util.orderedTextToString(messages.get(0).content().asOrderedText()).split(" ", 2);

            if(a.length > 2 && b.length > 2 && a[2].equals(b[2]) && (a[1].equalsIgnoreCase("Enabled") || a[1].equalsIgnoreCase("Disabled"))) {
                ChatHudLine prev = messages.get(0);
                this.messages.set(0, new ChatHudLine(this.client.inGameHud.getTicks(), text, prev.signature(), prev.indicator()));
                PulseClient.LOGGER.debug("Set chat line!!");
            } else {
                addMessage(text);
            }
        }
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
    void aVoid(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
        AutoDupe dupe = ((AutoDupe) ModuleManager.INSTANCE.getItemByClass(AutoDupe.class));

        if(dupe.isEnabled() && dupe.duping && dupe.hideDupeMessages.isEnabled() && message.contains(Text.of("Dupe"))) ci.cancel();
    }

    ChatHudLine prevLine = null;
    int repeatTimes = 1;
    @ModifyArgs(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addVisibleMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V"))
    void a(Args args) {
//        if(prevLine == null) {
//            prevLine = args.get(0);
//            return;
//        }
//
//        ChatHudLine line = args.get(0);
//        if(line.content().getString() == prevLine.content().getString()) {
//            repeatTimes++;
//            Text text = Text.empty()
//                            .append(line.content())
//                                    .append(" (x%s)".formatted(repeatTimes));
//            args.set(0, new ChatHudLine(line.creationTick(), text, line.signature(), line.indicator()));
//        } else {
//            repeatTimes = 1;
//            prevLine = line;
//        }
    }

    @Unique
    String prevContent = "";
    @Unique
    int times = 1;
    @ModifyExpressionValue(method = "addVisibleMessage", at = @At(value = "NEW", target = "(ILnet/minecraft/text/OrderedText;Lnet/minecraft/client/gui/hud/MessageIndicator;Z)Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;"))
    private ChatHudLine.Visible onAddMessage_modifyChatHudLineVisible(ChatHudLine.Visible line, @Local(ordinal = 1) int j) {

//        String content = Util.orderedTextToString(line.content());
//        if(content == prevContent) {
//            times++;
//            return new ChatHudLine.Visible(line.addedTime(),
//                    Text.empty().append(Util.orderedTextToMutableText(line.content())).append(" (x%s)".formatted(times)).asOrderedText(), line.indicator(), line.endOfEntry());
//        } else {
//            times = 1;
//            prevContent = content;
//        }

        LocalDateTime time = LocalDateTime.now();
        if(Chat.timestamps.isEnabled() && ModuleManager.INSTANCE.getItemByClass(Chat.class).isEnabled()) {
            line = new ChatHudLine.Visible(line.addedTime(),
                    Text.empty().append(
                            Text.empty()
                                    .setStyle(Style.EMPTY.withColor(ThemeInfo.COLORSCHEME.ACCENT().getRGB()))
                                    .append("<")
                                    .append((time.getHour() < 10 ? "0" + time.getHour() : time.getHour()) + ":" + (time.getMinute() < 10 ? "0" + time.getMinute() : time.getMinute()))
                                    .append("> ")
                    ).append(Util.orderedTextToMutableText(line.content())).asOrderedText(), line.indicator(), line.endOfEntry());
        }

        return line;
    }

//    @Inject(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At())

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void drawText(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
        if(Managers.MODULE.getItemByClass(Chat.class).isEnabled()) {
            ci.cancel();
            if (!this.isChatHidden()) {
                int i = this.getVisibleLineCount();
                int j = this.visibleMessages.size();
                if (j > 0) {
                    this.client.getProfiler().push("chat");
                    float f = (float)this.getChatScale();
                    int k = MathHelper.ceil((float)this.getWidth() / f);
                    int l = context.getScaledWindowHeight();
                    context.getMatrices().push();
                    context.getMatrices().scale(f, f, 1.0F);
                    context.getMatrices().translate(4.0F, 0.0F, 0.0F);
                    int m = MathHelper.floor((float)(l - 40) / f);
                    double d = this.client.options.getChatOpacity().getValue() * 0.8999999761581421 + 0.10000000149011612;
                    double e = this.client.options.getTextBackgroundOpacity().getValue();
                    double g = this.client.options.getChatLineSpacing().getValue();
                    int o = this.getLineHeight();
                    int p = (int)Math.round(-8.0 * (g + 1.0) + 4.0 * g);
                    int q = 0;

                    int t;
                    int u;
                    int v;
                    int x;
                    for(int r = 0; r + this.scrolledLines < this.visibleMessages.size() && r < i; ++r) {
                        int s = r + this.scrolledLines;
                        ChatHudLine.Visible visible = this.visibleMessages.get(s);
                        if (visible != null) {
                            t = currentTick - visible.addedTime();
                            if (t < 200 || focused) {
                                double h = focused ? 1.0 : getMessageOpacityMultiplier(t);
                                u = (int)(255.0 * h * d);
                                v = (int)(255.0 * h * e);
                                ++q;
                                if (u > 3) {
                                    x = m - r * o;
                                    int y = x + p;
                                    if(!Chat.noBG.isEnabled()) {
                                        context.fill(-4, x - o, k + 4 + 4, x, v << 24);
                                        int z = ThemeInfo.COLORSCHEME.ACCENT().getRGB();
                                        context.fill(-4, x - o, -2, x, z);
                                    }

                                    float xOff = t < 5 ? 50-t * 10 : 0;

                                    context.getMatrices().push();
                                    context.getMatrices().translate(0.0F, 0.0F, 50.0F);
                                    if(Chat.font.isEnabled()) RenderUtil.textRenderer.drawOrderedText(context.getMatrices(), visible.content(), Chat.animation.isEnabled() ? -xOff : 0, y + RenderUtil.fontOffsetY);
                                    else context.drawTextWithShadow(this.client.textRenderer, visible.content(), 0, y, 0xFFFFFF + (u << 24));
                                    context.getMatrices().pop();
                                }
                            }
                        }
                    }

                    long ac = this.client.getMessageHandler().getUnprocessedMessageCount();
                    int ad;
                    if (ac > 0L) {
                        ad = (int)(128.0 * d);
                        t = (int)(255.0 * e);
                        context.getMatrices().push();
                        context.getMatrices().translate(0.0F, (float)m, 0.0F);
                        context.fill(-2, 0, k + 4, 9, t << 24);
                        context.getMatrices().translate(0.0F, 0.0F, 50.0F);
                        context.drawTextWithShadow(this.client.textRenderer, Text.translatable("chat.queue", ac), 0, 1, 16777215 + (ad << 24));
                        context.getMatrices().pop();
                    }

                    if (focused) {
                        ad = this.getLineHeight();
                        t = j * ad;
                        int ae = q * ad;
                        int af = this.scrolledLines * ae / j - m;
                        u = ae * ae / t;
                        if (t != ae) {
                            v = af > 0 ? 170 : 96;
                            int w = this.hasUnreadNewMessages ? 13382451 : 3355562;
                            x = k + 4;
                            context.fill(x, -af, x + 2, -af - u, 100, w + (v << 24));
                            context.fill(x + 2, -af, x + 1, -af - u, 100, 13421772 + (v << 24));
                        }
                    }

                    context.getMatrices().pop();
                    this.client.getProfiler().pop();
                }
            }
        }
    }
}
