package net.paulem.vanillahammers;

import fr.skytasul.glowingentities.GlowingEntities;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import net.paulem.vanillahammers.commands.HammersCommand;
import net.paulem.vanillahammers.config.Config;
import net.paulem.vanillahammers.listeners.BlockMiningTracker;
import net.paulem.vanillahammers.listeners.HammersListener;
import net.paulem.vanillahammers.managers.BlockOutlineManager;
import net.paulem.vanillahammers.resourcepack.ResourcePackHosting;
import net.paulem.vanillahammers.tasks.BlockSelectCallingTask;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ovh.paulem.arcana.ArcanaAPI;

public class VanillaHammers extends JavaPlugin {
    public static VanillaHammers INSTANCE;
    public static ArcanaAPI<VanillaHammers> API;
    public static Config CONFIG;

    public static ResourcePackHosting packHosting;

    public static NamespacedKey HAMMER_KEY;
    public static NamespacedKey HAMMER_TEXTURE_KEY;

    public GlowingEntities glowingEntities;

    @Override
    public void onEnable() {
        INSTANCE = this;

        HAMMER_KEY = key("hammer");
        HAMMER_TEXTURE_KEY = key("item/hammer");

        saveDefaultConfig();

        API = new ArcanaAPI<>(INSTANCE);
        API.init();

        CONFIG = API.loadConfig(Config.class, getConfig());

        glowingEntities = new GlowingEntities(INSTANCE);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(HammersCommand.createCommand().build());
        });

        getServer().getPluginManager().registerEvents(new HammersListener(), INSTANCE);
        getServer().getPluginManager().registerEvents(new BlockMiningTracker(), INSTANCE);

        packHosting = new ResourcePackHosting();
        getServer().getPluginManager().registerEvents(packHosting, this);
        packHosting.start();

        GlobalRegionScheduler globalScheduler = getServer().getGlobalRegionScheduler();
        globalScheduler.runAtFixedRate(INSTANCE, new BlockSelectCallingTask(), 1L, 1L);

        getLogger().info("VanillaHammers has been enabled");
    }

    @Override
    public void onDisable() {
        BlockOutlineManager.removeAllOutlines();

        packHosting.stop();

        getLogger().info("Vanilla-Hammers has been disabled");
    }

    public static NamespacedKey key(@NotNull String key) {
        return NamespacedKey.fromString(key, INSTANCE);
    }
}
