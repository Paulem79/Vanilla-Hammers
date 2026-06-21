package net.paulem.vanillahammers.listeners;

import net.paulem.vanillahammers.VanillaHammers;
import net.paulem.vanillahammers.tasks.BlockSelectTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BlockSelectStarterListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Start the task loop on the player's specific region thread
        event.getPlayer().getScheduler().runAtFixedRate(
                VanillaHammers.INSTANCE, // Your plugin instance
                new BlockSelectTask(event.getPlayer()),
                null, // Action on cancel
                1L, 2L // Every 2 ticks
        );
    }
}
