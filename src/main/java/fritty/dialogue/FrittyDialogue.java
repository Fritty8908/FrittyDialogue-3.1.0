package fritty.dialogue;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class FrittyDialogue extends JavaPlugin {

    private static FrittyDialogue instance;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("FrittyDialogue 3.1.0 is starting...");
        getLogger().info("Plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("FrittyDialogue is shutting down.");

        instance = null;
    }

    public static FrittyDialogue getInstance() {
        return instance;
    }

    public Logger getPluginLogger() {
        return getLogger();
    }
}
