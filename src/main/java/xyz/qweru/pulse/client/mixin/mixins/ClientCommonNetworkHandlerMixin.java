package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.qweru.pulse.client.systems.commands.ClientCommand;
import xyz.qweru.pulse.client.managers.impl.CommandManager;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.mixin.iinterface.IPlayerMoveC2SPacket;
import xyz.qweru.pulse.client.systems.modules.impl.misc.AutoDupe;
import xyz.qweru.pulse.client.systems.modules.impl.misc.Chat;
import xyz.qweru.pulse.client.systems.modules.impl.movement.LiveOverflow;
import xyz.qweru.pulse.client.systems.modules.impl.setting.ChatCommands;
import xyz.qweru.pulse.client.utils.player.ChatUtil;

import java.awt.*;

import static xyz.qweru.pulse.client.PulseClient.*;

@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {
    @Shadow @Final protected ClientConnection connection;

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void sendPacket(Packet<?> packet, CallbackInfo ci) { // ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴩqʀꜱᴛᴜᴠᴡxyᴢ

        if(packet instanceof CommandExecutionC2SPacket packet1) {
            AutoDupe autoDupe = ((AutoDupe) ModuleManager.INSTANCE.getItemByClass(AutoDupe.class));
            if(
                    (autoDupe.ticksSinceLastDupe < 5 || autoDupe.ticksSinceLastDupe > autoDupe.ticksDelay.getMax() - 5)
                            && ModuleManager.INSTANCE.getItemByClass(AutoDupe.class).isEnabled() && !((AutoDupe) ModuleManager.INSTANCE.getItemByClass(AutoDupe.class)).duping

            )
            {
                ChatUtil.warn("Command limit reached!");
                ci.cancel();
            }
        }

        else if(packet instanceof ChatMessageC2SPacket sPacket) {
            if(sPacket.chatMessage().startsWith(ChatCommands.PREFIX.getValue())) {
                ClientCommand c = CommandManager.INSTANCE.getCommandByName(sPacket.chatMessage().substring(1).split(" ")[0]);
                if(c != null) {
                    ChatUtil.sendLocalMsg(Text.empty().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.GRAY.getRGB()))).append(" > " + c.getName()));
                    c.run(sPacket.chatMessage().substring(1).split(" "));
                }
                else {
                    ChatUtil.err("Invalid command!");
                }
            } else {
                String packetText = ((ChatMessageC2SPacket) packet).chatMessage();

                if(ModuleManager.INSTANCE.getItemByClass(Chat.class).isEnabled()) {
                    if(Chat.fancyChat.isEnabled()) {
                        if(Chat.onlySuffix.isEnabled()) {
                            packetText += replaceWFancy(Chat.suffix.getValue());
                        } else {
                            packetText = replaceWFancy(packetText);
                        }
                    }
                }

                packet = new ChatMessageC2SPacket(packetText, sPacket.timestamp(), sPacket.salt(), sPacket.signature(), sPacket.acknowledgment());
                connection.send(packet);
            }

            ci.cancel();
        }

        if(ModuleManager.INSTANCE.getItemByClass(LiveOverflow.class).isEnabled() && LiveOverflow.robotMove.isEnabled()) {
            if(packet instanceof PlayerMoveC2SPacket p){
                double x = ((int)(((PlayerMoveC2SPacket) p).getX(mc.player.getX()) * 100)) / 100.0;
                double z = ((int)(((PlayerMoveC2SPacket) p).getZ(mc.player.getZ()) * 100)) / 100.0;
                    ((IPlayerMoveC2SPacket) p).pulse$setX(x);
                    ((IPlayerMoveC2SPacket) p).pulse$setZ(z);
                    LiveOverflow.lastPacket = p;
            }

            if(mc.player != null && mc.player.getVehicle() instanceof BoatEntity){
                if(packet instanceof VehicleMoveC2SPacket){
                    ChatUtil.warn("Vehicle robot move has not been implemented yet, im lazy");
                    ci.cancel();
                }
            }


        }
    }

    @Unique
    private String replaceWFancy(String suffix) {
        suffix = suffix.replace("a", "ᴀ");
        suffix = suffix.replace("A", "ᴀ");
        suffix = suffix.replace("b", "ʙ");
        suffix = suffix.replace("B", "ʙ");
        suffix = suffix.replace("c", "ᴄ");
        suffix = suffix.replace("C", "ᴄ");
        suffix = suffix.replace("d", "ᴅ");
        suffix = suffix.replace("D", "ᴅ");
        suffix = suffix.replace("e", "ᴇ");
        suffix = suffix.replace("E", "ᴇ");
        suffix = suffix.replace("f", "ꜰ");
        suffix = suffix.replace("F", "ꜰ");
        suffix = suffix.replace("g", "ɢ");
        suffix = suffix.replace("G", "ɢ");
        suffix = suffix.replace("h", "ʜ");
        suffix = suffix.replace("H", "ʜ");
        suffix = suffix.replace("i", "ɪ");
        suffix = suffix.replace("I", "ɪ");
        suffix = suffix.replace("j", "ᴊ");
        suffix = suffix.replace("J", "ᴊ");
        suffix = suffix.replace("k", "ᴋ");
        suffix = suffix.replace("K", "ᴋ");
        suffix = suffix.replace("l", "ʟ");
        suffix = suffix.replace("L", "ʟ");
        suffix = suffix.replace("m", "ᴍ");
        suffix = suffix.replace("M", "ᴍ");
        suffix = suffix.replace("n", "ɴ");
        suffix = suffix.replace("N", "ɴ");
        suffix = suffix.replace("o", "ᴏ");
        suffix = suffix.replace("O", "ᴏ");
        suffix = suffix.replace("p", "ᴩ");
        suffix = suffix.replace("P", "ᴩ");
        suffix = suffix.replace("q", "q");
        suffix = suffix.replace("Q", "q");
        suffix = suffix.replace("r", "ʀ");
        suffix = suffix.replace("R", "ʀ");
        suffix = suffix.replace("s", "ꜱ");
        suffix = suffix.replace("S", "ꜱ");
        suffix = suffix.replace("t", "ᴛ");
        suffix = suffix.replace("T", "ᴛ");
        suffix = suffix.replace("u", "ᴜ");
        suffix = suffix.replace("U", "ᴜ");
        suffix = suffix.replace("v", "ᴠ");
        suffix = suffix.replace("V", "ᴠ");
        suffix = suffix.replace("w", "ᴡ");
        suffix = suffix.replace("W", "ᴡ");
        suffix = suffix.replace("x", "x");
        suffix = suffix.replace("X", "x");
        suffix = suffix.replace("y", "y");
        suffix = suffix.replace("Y", "y");
        suffix = suffix.replace("z", "ᴢ");
        suffix = suffix.replace("Z", "ᴢ");
        return suffix;
    }

}
