package xyz.qweru.pulse.client.managers;

import xyz.qweru.pulse.client.managers.impl.CommandManager;
import xyz.qweru.pulse.client.managers.impl.ModuleManager;
import xyz.qweru.pulse.client.utils.BlockManager;
import xyz.qweru.pulse.client.utils.VarManger;
import xyz.qweru.pulse.client.utils.player.SlotManager;
import xyz.qweru.pulse.client.utils.render.shaders.ShaderManager;

public class Managers {
    public static CommandManager COMMAND = CommandManager.INSTANCE;
    public static ModuleManager MODULE = ModuleManager.INSTANCE;
    public static ShaderManager SHADER = new ShaderManager();
    public static VarManger VARIABLE = new VarManger();
    public static SlotManager SLOT = new SlotManager();
    public static BlockManager BLOCK = BlockManager.INSTANCE;
}
