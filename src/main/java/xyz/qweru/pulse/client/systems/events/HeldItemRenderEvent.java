package xyz.qweru.pulse.client.systems.events;

import meteordevelopment.orbit.ICancellable;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class HeldItemRenderEvent implements ICancellable {
    private final MatrixStack matrices;

    public ItemStack getItem() {
        return item;
    }

    public MatrixStack getMatrices() {
        return matrices;
    }

    private final ItemStack item;
    private final float equipProgress;
    private final float delta;
    private final AbstractClientPlayerEntity player;
    private final float swingProgress;
    private final Hand hand;
    boolean cancelled = false;

    public HeldItemRenderEvent(MatrixStack matrices, ItemStack item, float equipProgress, float delta, AbstractClientPlayerEntity player, float swingProgress, Hand hand) {
        this.matrices = matrices;
        this.item = item;
        this.equipProgress = equipProgress;
        this.delta = delta;
        this.player = player;
        this.swingProgress = swingProgress;
        this.hand = hand;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public float getDelta() {
        return delta;
    }

    public float getEquipProgress() {
        return equipProgress;
    }

    public float getSwingProgress() {
        return swingProgress;
    }

    public AbstractClientPlayerEntity getPlayer() {
        return player;
    }

    public Hand getHand() {
        return hand;
    }
}
