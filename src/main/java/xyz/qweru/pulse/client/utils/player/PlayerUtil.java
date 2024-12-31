package xyz.qweru.pulse.client.utils.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;
import org.apache.commons.lang3.mutable.MutableObject;
import xyz.qweru.pulse.client.PulseClient;
import xyz.qweru.pulse.client.utils.world.PacketUtil;

@SuppressWarnings({"unused", "null", "ConstantConditions"})
public class PlayerUtil {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static Entity getEntityInRange(EntityType<?> entityType, double range) {
        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        Vec3d viewVector = mc.player.getRotationVecClient();
        Vec3d extendedPoint = cameraPos.add(viewVector.x * range, viewVector.y * range, viewVector.z * range);

        for (Entity entity : mc.world.getEntities()) {
            if (entity.getType() == entityType) {
                if (entity.getBoundingBox().expand(0.3).intersects(cameraPos, extendedPoint)) {
                    return entity;
                }
            }
        }

        return null;
    }

    public static boolean canSeePos(Vec3d pos, double range) {
        Vec3d vec3d_ = new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());
        float td = mc.getRenderTickCounter().getTickDelta(true);
        if (pos.distanceTo(vec3d_) > 128.0) {
            return false;
        } else {
            Vec3d vec3d = mc.player.getCameraPosVec(td);
            Vec3d vec3d2 = mc.player.getRotationVector(RotationUtil.getPitch(pos), RotationUtil.getYaw(pos));
            Vec3d vec3d3 = vec3d.add(vec3d2.x * range, vec3d2.y * range, vec3d2.z * range);

            HitResult result = mc.player.getWorld().raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
            return result.getType() == HitResult.Type.BLOCK && result.getPos() == pos;
        }
    }

    public static void attackEntity(Entity target) {
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    public static boolean lookingAt(Block block) {
        if (mc.crosshairTarget instanceof BlockHitResult) {
            BlockPos blockPos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            BlockState blockState = mc.world.getBlockState(blockPos);
            return blockState.getBlock() == block;
        }

        return false;
    }

    public static boolean isAboveWater() {
        BlockPos.Mutable blockPos = mc.player.getBlockPos().mutableCopy();

        for (int i = 0; i < 64; i++) {
            BlockState state = mc.world.getBlockState(blockPos);

            Fluid fluid = state.getFluidState().getFluid();
            if (fluid == Fluids.WATER || fluid == Fluids.FLOWING_WATER) {
                return true;
            }

            blockPos.move(0, -1, 0);
        }

        return false;
    }

    public static boolean isAboveBlock(Block block) {
        BlockPos.Mutable blockPos = mc.player.getBlockPos().mutableCopy();
        for (int i = 0; i < 64; i++) {
            BlockState state = mc.world.getBlockState(blockPos);

            Block above = state.getBlock();
            if (above == block) {
                return true;
            }

            blockPos.move(0, -1, 0);
        }

        return false;
    }

    public static void placeBlock(BlockHitResult hitResult) {
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hitResult);
    }

    public static void interact() {
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
    }

    /**
     * @see net.minecraft.client.network.ClientPlayerInteractionManager#interactItem(PlayerEntity, Hand)
     */
    public static ActionResult interact(PlayerEntity player, Hand hand, int slot, float pitch, float yaw) {
        if(slot == -1) hand = Hand.OFF_HAND;

        if (mc.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        } else {
            if(slot != -1) PacketUtil.send(new UpdateSelectedSlotC2SPacket(slot));
            MutableObject<ActionResult> mutableObject = new MutableObject();
            Hand finalHand = hand;
            PacketUtil.sendSequencedPacket(mc.world, (sequence) -> {
                PlayerInteractItemC2SPacket playerInteractItemC2SPacket = new PlayerInteractItemC2SPacket(finalHand, sequence, yaw, pitch);
                ItemStack itemStack = slot == -1 ? mc.player.getOffHandStack() : player.getInventory().getStack(slot);
                if (player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
                    mutableObject.setValue(ActionResult.PASS);
                    return playerInteractItemC2SPacket;
                } else {
                    TypedActionResult<ItemStack> typedActionResult = itemStack.use(mc.world, player, finalHand);
                    ItemStack itemStack2 = (ItemStack)typedActionResult.getValue();
                    if (itemStack2 != itemStack) {
                        player.setStackInHand(finalHand, itemStack2);
                    }

                    mutableObject.setValue(typedActionResult.getResult());
                    return playerInteractItemC2SPacket;
                }
            });
            return (ActionResult)mutableObject.getValue();
        }
    }

    /**
     * sends less packets
     * @see PlayerUtil#interact(PlayerEntity, Hand, int, float, float)
     */
    public static ActionResult interact$noSelect(PlayerEntity player, Hand hand, int slot, float pitch, float yaw) {
        if(slot == -1) hand = Hand.OFF_HAND;

        if (mc.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        } else {
            MutableObject<ActionResult> mutableObject = new MutableObject();
            Hand finalHand = hand;
            PacketUtil.sendSequencedPacket(mc.world, (sequence) -> {
                PlayerInteractItemC2SPacket playerInteractItemC2SPacket = new PlayerInteractItemC2SPacket(finalHand, sequence, yaw, pitch);
                ItemStack itemStack = slot == -1 ? mc.player.getOffHandStack() : player.getInventory().getStack(slot);
                if (player.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
                    mutableObject.setValue(ActionResult.PASS);
                    return playerInteractItemC2SPacket;
                } else {
                    TypedActionResult<ItemStack> typedActionResult = itemStack.use(mc.world, player, finalHand);
                    ItemStack itemStack2 = (ItemStack)typedActionResult.getValue();
                    if (itemStack2 != itemStack) {
                        player.setStackInHand(finalHand, itemStack2);
                    }

                    mutableObject.setValue(typedActionResult.getResult());
                    return playerInteractItemC2SPacket;
                }
            });
            return (ActionResult)mutableObject.getValue();
        }
    }

    // credit to chatgpt3
    public static float getBreakingTicks(BlockState blockState, ItemStack itemStack) {
        PlayerEntity player = mc.player;

        // Check if the block is unbreakable
        if (blockState.getHardness(mc.world, BlockPos.ORIGIN) == -1) {
            return Float.POSITIVE_INFINITY; // Unbreakable
        }

        // Base mining speed from the tool
        float miningSpeed = itemStack.getMiningSpeedMultiplier(blockState);

        // Check if the tool is effective
        if (!itemStack.isSuitableFor(blockState)) {
            miningSpeed = 1.0f; // Default if not effective
        }

        // Apply Efficiency enchantment
        int efficiencyLevel = getLevel(Enchantments.EFFICIENCY, itemStack);
        if (efficiencyLevel > 0 && miningSpeed > 1.0f) {
            miningSpeed += efficiencyLevel * efficiencyLevel + 1;
        }

        // Apply player speed modifiers (e.g., Haste, Mining Fatigue)
        if (player.hasStatusEffect(StatusEffects.HASTE)) {
            int hasteLevel = player.getStatusEffect(StatusEffects.HASTE).getAmplifier();
            miningSpeed *= 1.0f + (hasteLevel + 1) * 0.2f;
        }
        if (player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            int fatigueLevel = player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier();
            float fatigueModifier = switch (fatigueLevel) {
                case 0 -> 0.3f;
                case 1 -> 0.09f;
                case 2 -> 0.0027f;
                default -> 0.00081f;
            };
            miningSpeed *= fatigueModifier;
        }

        // Apply underwater and on-ground modifiers
        if (player.isSubmergedInWater()) { // todo: check for aqua affinity
            miningSpeed /= 5.0f;
        }
        if (!player.isOnGround()) {
            miningSpeed /= 5.0f;
        }

        // Calculate break time in ticks (base 30 ticks per block hardness)
        float hardness = blockState.getBlock().getHardness();

        return hardness * 30.0f / miningSpeed;
    }

    // todo there is probably a better way to do this
    public static int getLevel(RegistryKey<Enchantment> enchantment, ItemStack stack) {
        for (RegistryEntry<Enchantment> enchantmentRegistryEntry : stack.getEnchantments().getEnchantments()) {
            try {
                if(enchantmentRegistryEntry.getKey().orElseThrow().equals(enchantment)) return stack.getEnchantments().getLevel(enchantmentRegistryEntry);
            } catch (Exception e) {
                PulseClient.throwException(e);
            }
        }
        return 0;
    }
}