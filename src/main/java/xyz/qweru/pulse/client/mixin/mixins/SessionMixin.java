package xyz.qweru.pulse.client.mixin.mixins;

import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import xyz.qweru.pulse.client.mixin.iinterface.ISession;

@Mixin(Session.class)
public class SessionMixin implements ISession {


    @Mutable
    @Shadow @Final private String username;

    @Override
    public void pulse$setUsername(String username) {
        this.username = username;
    }
}
