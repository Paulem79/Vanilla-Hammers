package net.paulem.vanillahammers;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import fr.skytasul.glowingentities.GlowingEntities;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import net.paulem.vanillahammers.commands.HammersCommand;
import net.paulem.vanillahammers.config.Config;
import net.paulem.vanillahammers.hammers.Hammer;
import net.paulem.vanillahammers.tasks.BlockMiningTask;
import net.paulem.vanillahammers.listeners.HammersListener;
import net.paulem.vanillahammers.managers.BlockOutlineManager;
import net.paulem.vanillahammers.resourcepack.ResourcePackHosting;
import net.paulem.vanillahammers.tasks.BlockSelectTask;
import net.paulem.vanillahammers.tasks.TickCounter;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ovh.paulem.arcana.ArcanaAPI;

public class VanillaHammers extends JavaPlugin {
    public static VanillaHammers INSTANCE;
    public static ArcanaAPI<VanillaHammers> API;
    public static Config CONFIG;

    public static NamespacedKey HAMMER_PDC_KEY;

    @Getter
    private static TaskScheduler scheduler;
    @Getter
    @Nullable
    private static ResourcePackHosting packHosting;

    public GlowingEntities glowingEntities;

    @Override
    public void onEnable() {
        INSTANCE = this;

        HAMMER_PDC_KEY = key("hammer");

        saveDefaultConfig();

        scheduler = UniversalScheduler.getScheduler(INSTANCE);

        API = new ArcanaAPI<>(INSTANCE);
        API.init();

        CONFIG = API.loadConfig(Config.class, getConfig());

        TickCounter.init();

        Hammer.init();

        glowingEntities = new GlowingEntities(INSTANCE);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(HammersCommand.createCommand().build());
        });

        getServer().getPluginManager().registerEvents(new HammersListener(), INSTANCE);
        getServer().getPluginManager().registerEvents(new BlockMiningTask(), INSTANCE);

        packHosting = new ResourcePackHosting();
        getServer().getPluginManager().registerEvents(packHosting, INSTANCE);
        packHosting.start();

        getScheduler().runTaskTimer(new BlockSelectTask(), 1L, 1L);

        getLogger().info("VanillaHammers has been enabled");
    }

    @Override
    public void onDisable() {
        BlockOutlineManager.removeAllOutlines();

        if(packHosting != null) {
            packHosting.stop();
        }

        getLogger().info("Vanilla-Hammers has been disabled");
    }

    public static NamespacedKey key(@NotNull String key) {
        return NamespacedKey.fromString(key, INSTANCE);
    }
}
