package net.paulem.vanillahammers;

import fr.skytasul.glowingentities.GlowingBlocks;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import net.paulem.vanillahammers.commands.HammersCommand;
import net.paulem.vanillahammers.listeners.HammersListener;
import net.paulem.vanillahammers.tasks.BlockSelectCallingTask;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class VanillaHammers extends JavaPlugin {
    public static VanillaHammers INSTANCE;
    public GlowingBlocks glowingBlocks;

    @Override
    public void onEnable() {
        INSTANCE = this;

        glowingBlocks = new GlowingBlocks(this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(HammersCommand.createCommand().build());
        });

        getServer().getPluginManager().registerEvents(new HammersListener(), INSTANCE);

        GlobalRegionScheduler globalScheduler = getServer().getGlobalRegionScheduler();
        globalScheduler.runAtFixedRate(INSTANCE, new BlockSelectCallingTask(), 1L, 1L);

        getLogger().info("VanillaHammers has been enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Vanilla-Hammers has been disabled");
    }

    public static NamespacedKey key(@NotNull String key) {
        return NamespacedKey.fromString(key, INSTANCE);
    }
}
