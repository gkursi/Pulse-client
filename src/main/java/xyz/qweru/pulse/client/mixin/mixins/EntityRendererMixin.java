package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.qweru.pulse.client.mixin.iinterface.IEntityRenderer;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin implements IEntityRenderer {
    @Shadow @Final protected EntityRenderDispatcher dispatcher;

    @Override
    public EntityRenderDispatcher pulse$getDispatcher() {
        return dispatcher;
    }
}
