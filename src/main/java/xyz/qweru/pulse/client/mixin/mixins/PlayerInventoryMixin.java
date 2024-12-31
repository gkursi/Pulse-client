package xyz.qweru.pulse.client.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.qweru.pulse.client.managers.Managers;
import xyz.qweru.pulse.client.systems.modules.impl.world.BoxBreaker;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Shadow @Final public DefaultedList<ItemStack> main;

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        if(Managers.MODULE.getItemByClass(BoxBreaker.class).isEnabled()) cir.setReturnValue(this.main.get(Managers.SLOT.getRealSlot()).getMiningSpeedMultiplier(block));
    }

}
