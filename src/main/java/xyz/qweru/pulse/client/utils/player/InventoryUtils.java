package xyz.qweru.pulse.client.utils.player;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;
import xyz.qweru.pulse.client.utils.annotations.ExcludeModule;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

import java.util.Optional;
import java.util.function.Predicate;

@ExcludeModule
public class InventoryUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void selectItemHotbar(Item item) {
        PlayerInventory inv = mc.player.getInventory();
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = inv.getStack(i);
            if (item.equals(itemStack.getItem())) {
                inv.selectedSlot = i;
                break;
            }
        }
    }

    public static boolean isHolding(Item item) {
        return mc.player.isHolding(item);
    }

    public static void clickSlot(int slot) {
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, GLFW.GLFW_MOUSE_BUTTON_1, SlotActionType.PICKUP_ALL, mc.player);
    }

    public static void dropStack() {
//    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, GLFW.GLFW_MOUSE_BUTTON_1, SlotActionType., mc.player);
        PacketUtil.send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, Direction.UP));
    }

    public static void swapTo(int slot) {
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, GLFW.GLFW_MOUSE_BUTTON_1, SlotActionType.SWAP, mc.player);
    }

    public static int getItemSlotHotbar(Item item) {
        for (int i = 0; i <= 8; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static int getItemSlotInv(Item item) {
        int i;
        for (i = 9; i <= 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem().equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public static int getItemSlotAll(Item item) {
        int i;
        for (i = 0; i <= 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem().equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public static int getItemSlotAll(Predicate<ItemStack> predicate) {
        int i;
        assert mc.player != null;
        for (i = 0; i <= 36; i++) {
            if (predicate.test(mc.player.getInventory().getStack(i))) {
                return i;
            }
        }
        return -1;
    }

    public static int getPotionEffectsAll(RegistryEntry<StatusEffect>... effects) {
        for (int i = 0; i <= 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem() instanceof PotionItem pi) {
                for (StatusEffectInstance effect : pi.getComponents().get(DataComponentTypes.POTION_CONTENTS).getEffects()) {
                    for (RegistryEntry<StatusEffect> statusEffect : effects) {
                        if(effect.getEffectType().equals(statusEffect)) return i;
                    }
                }
            }
        }
        return -1;
    }

    public static boolean hasEffect(RegistryEntry<StatusEffect> effectTarget, Item item) {
        if (item instanceof PotionItem pi) {
            for (StatusEffectInstance effect : pi.getComponents().get(DataComponentTypes.POTION_CONTENTS).getEffects()) {
                if(effect.getEffectType().equals(effectTarget)) return true;
            }
        }
        return false;
    }

    public static void selectSlot(int slot) {
        mc.player.getInventory().selectedSlot = slot;
    }

    public static int getHotbarItem(Item...items) {
        for (Item enabledItem : items) {
            int slot = getItemSlotHotbar(enabledItem);
            if(slot == -1) continue;
            return slot;
        }

        return -1;
    }

    public static boolean isInSlot(int slot) {
        return mc.player.getInventory().selectedSlot == slot;
    }

    public static int totalItemCount(Item item) {
        int n = 0;
        for (int i = 0; i <= 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem().equals(item)) {
                n += mc.player.getInventory().getStack(i).getCount();
            }
        }
        return n;
    }

    public static int totalItemCount(Predicate<ItemStack> itemTest) {
        int n = 0;
        for (int i = 0; i <= 36; i++) {
            if (itemTest.test(mc.player.getInventory().getStack(i))) {
                n += mc.player.getInventory().getStack(i).getCount();
            }
        }
        return n;
    }

    public static int findBestPickaxe(boolean onlyHotbar) {
        int slot = mc.player.getInventory().selectedSlot;
        int lvl = 0; // wood = 1, stone = 2, gold = 3, iron = 4, diamond = 5, netherite = 6;
        for(int i = 0; i <= (onlyHotbar ? 8 : 36); i++) {
            if(getStack(i).getItem().equals(Items.NETHERITE_PICKAXE)) {
                slot = i;
                break;
            } else if(getStack(i).getItem().equals(Items.DIAMOND_PICKAXE)) {
                slot = i;
                lvl = 5;
            } else if(getStack(i).getItem().equals(Items.IRON_PICKAXE) && lvl < 4) {
                slot = i;
                lvl = 4;
            } else if(getStack(i).getItem().equals(Items.GOLDEN_PICKAXE) && lvl < 3) {
                slot = i;
                lvl = 3;
            } else if(getStack(i).getItem().equals(Items.STONE_PICKAXE) && lvl < 2) {
                slot = i;
                lvl = 2;
            } else if(getStack(i).getItem().equals(Items.WOODEN_PICKAXE) && lvl < 1) {
                slot = i;
                lvl = 1;
            }
        }

        return slot;
    }

    public static int findFastestTool(BlockState state) {
        float bestScore = 1;
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isSuitableFor(state)) continue;

            float score = stack.getMiningSpeedMultiplier(state);
            if (score > bestScore) {
                bestScore = score;
                slot = i;
            }
        }

        return slot;
    }

    static ItemStack getStack(int slot) {
        return mc.player.getInventory().getStack(slot);
    }
}