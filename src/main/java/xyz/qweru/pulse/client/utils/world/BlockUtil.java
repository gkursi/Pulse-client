package xyz.qweru.pulse.client.utils.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.utils.Util;

import static xyz.qweru.pulse.client.PulseClient.mc;

public class BlockUtil {

    public static Block getBlockAt(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
    }

    public static Block getBlockAt(Vec3d pos) {
        return getBlockAt(BlockPos.ofFloored(pos));
    }

    public static boolean isPosReplaceable(BlockPos pos) {
        return Util.equalsAny(getBlockAt(pos), Blocks.AIR, Blocks.WATER, Blocks.LAVA, Blocks.FIRE, Blocks.WATER, Blocks.SOUL_FIRE);
    }

    public static boolean isPosBreakable(BlockPos pos) {
        return !Util.equalsAny(getBlockAt(pos), Blocks.BEDROCK, Blocks.END_PORTAL_FRAME, Blocks.NETHER_PORTAL, Blocks.END_PORTAL, Blocks.END_GATEWAY);
    }

    public static void forBlocksInRange(BlockRunnable runnable, int range) {
        for(int x = -range; x <= range; x++) {
            for(int y = -range; y <= range; y++) {
                for(int z = -range; z <= range; z++) {
                    runnable.call(x, y, z, BlockPos.ofFloored(mc.player.getPos()).add(x, y, z));
                }
            }
        }
    }

    public static void forBlocksInRange(BlockRunnable runnable, int range, Entity entity) {
        for(int x = -range; x <= range; x++) {
            for(int y = -range; y <= range; y++) {
                for(int z = -range; z <= range; z++) {
                    runnable.call(entity.getX() + x, entity.getY() + y, entity.getZ() + z, BlockPos.ofFloored(entity.getPos()).add(x, y, z));
                }
            }
        }
    }

    public static void forBlocksInRange(BlockRunnable runnable, double range, Vec3d pos) {
        for(double x = -range; x <= range; x++) {
            for(double y = -range; y <= range; y++) {
                for(double z = -range; z <= range; z++) {
//                    LOGGER.warn("called blocksInRange ({} {} {})", x, y, z);
                    runnable.call(pos.getX() + x, pos.getY() + y, pos.getZ() + z, BlockPos.ofFloored(pos.add(x, y, z)));
                }
            }
        }
    }

    public interface BlockRunnable {
        void call(double px, double py, double pz, BlockPos blockPos);
    }

    public static float getMiningTime(BlockState blockState, ItemStack toolStack) {
        // Get the block hardness
        float blockHardness = blockState.getHardness(null, null); // Null for player and world

        // Get the tool mining speed (assumes it's a ToolItem)
        if (toolStack.getItem() instanceof ToolItem toolItem) {
            ToolMaterial material = toolItem.getMaterial();

            // Tool mining speed (for the specific block type)
            float toolSpeed = material.getMiningSpeedMultiplier();

            // Optional: Check for Efficiency enchantment and add multiplier
            int efficiencyLevel = getLevel(Enchantments.EFFICIENCY, toolStack);
            float efficiencyMultiplier = 1.0f + efficiencyLevel * 0.3f; // Minecraft standard formula

            // Calculate the mining time
            return (blockHardness * 1.5f) / (toolSpeed * efficiencyMultiplier);
        }

        // Default return if not a valid tool
        return blockHardness * 1.5f; // If the tool doesn't affect the block
    }

    // there is probably a better way to do this
    static int getLevel(RegistryKey<Enchantment> enchantment, ItemStack stack) {
        for (RegistryEntry<Enchantment> enchantmentRegistryEntry : stack.getEnchantments().getEnchantments()) {
            try {
                if(enchantmentRegistryEntry.getKey().orElseThrow().equals(enchantment)) return stack.getEnchantments().getLevel(enchantmentRegistryEntry);
            } catch (Exception e) {
                PulseClient.throwException(e);
            }
        }
        return 0;
    }

    public static void mine(BlockPos pos) {
        PacketUtil.send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
        PacketUtil.send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
    }

    public static void startMine(BlockPos pos) {
        PacketUtil.send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
    }

    public static double getBreakDelta(int slot, BlockState state) {
        float hardness = state.getHardness(null, null);
        if (hardness == -1) return 0;
        else {
            return getBlockBreakingSpeed(slot, state) / hardness / (!state.isToolRequired() || mc.player.getInventory().main.get(slot).isSuitableFor(state) ? 30 : 100);
        }
    }

    /**
     * @see net.minecraft.entity.player.PlayerEntity#getBlockBreakingSpeed(BlockState)
     */
    private static double getBlockBreakingSpeed(int slot, BlockState block) {
        double speed = mc.player.getInventory().main.get(slot).getMiningSpeedMultiplier(block);

        if (speed > 1) {
            ItemStack tool = mc.player.getInventory().getStack(slot);

            int efficiency = Util.getEnchantmentLevel(tool, Enchantments.EFFICIENCY);

            if (efficiency > 0 && !tool.isEmpty()) speed += efficiency * efficiency + 1;
        }

        if (StatusEffectUtil.hasHaste(mc.player)) {
            speed *= 1 + (StatusEffectUtil.getHasteAmplifier(mc.player) + 1) * 0.2F;
        }

        if (mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float k = switch (mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };

            speed *= k;
        }

        if (mc.player.isSubmergedIn(FluidTags.WATER)) {
            speed *= mc.player.getAttributeValue(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED);
        }

        if (!mc.player.isOnGround()) {
            speed /= 5.0F;
        }

        return speed;
    }
}
