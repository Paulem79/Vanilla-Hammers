package net.paulem.vanillahammers.listeners;

import net.paulem.vanillahammers.VanillaHammers;
import net.paulem.vanillahammers.events.PlayerStopDamageBlockEvent;
import net.paulem.vanillahammers.utils.RaycastUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlockMiningTracker implements Listener {
    // Stores the player's UUID -> Their current mining info
    private final Map<UUID, MiningSession> activeMining = new ConcurrentHashMap<>();

    // 5 ticks threshold equals exactly 250ms at standard 20 TPS.
    // If a player doesn't damage the block for 5 ticks, they stopped mining.
    private static final int TICK_THRESHOLD = 5;

    public BlockMiningTracker() {
        // Start the tracking task every 4 ticks (0.2 seconds)
        startTrackingTask();
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        UUID uuid = player.getUniqueId();

        MiningSession session = activeMining.get(uuid);
        int currentTick = Bukkit.getCurrentTick();

        if (session == null || !session.getBlock().getLocation().equals(block.getLocation())) {
            // The player starts mining a NEW block
            activeMining.put(uuid, new MiningSession(block, currentTick));
        } else {
            // The player continues mining the SAME block, refresh the tick counter
            session.updateTick(currentTick);
        }
    }

    private void startTrackingTask() {
        Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(VanillaHammers.INSTANCE, (_) -> {
            int currentTick = Bukkit.getCurrentTick();
            Iterator<Map.Entry<UUID, MiningSession>> iterator = activeMining.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<UUID, MiningSession> entry = iterator.next();
                UUID uuid = entry.getKey();
                MiningSession session = entry.getValue();

                // If no interaction with the block for more than 5 ticks (250ms equivalent)
                if (currentTick - session.getLastDamageTick() > TICK_THRESHOLD) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        // ====================================================
                        // THE PLAYER STOPPED BREAKING THE BLOCK
                        // ====================================================
                        onPlayerStopMining(player, session.getBlock());
                    }
                    // Remove the player from the Map
                    iterator.remove();
                }
            }
        }, 1L, 4L); // Executes every 4 ticks
    }

    /**
     * When player stop mining a block
     */
    private void onPlayerStopMining(Player player, Block block) {
        BlockFace face = RaycastUtils.getTargetBlockFace(player);

        new PlayerStopDamageBlockEvent(player, block, face).callEvent();
    }

    // Security: Clean up the Map if the player disconnects while mining
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        activeMining.remove(event.getPlayer().getUniqueId());
    }

    // Utility inner class to store the mining state
    private static class MiningSession {
        private final Block block;
        private int lastDamageTick;

        public MiningSession(Block block, int lastDamageTick) {
            // Usages d'un int car Bukkit.getCurrentTick() retourne un primitif int.
            this.block = block;
            this.lastDamageTick = lastDamageTick;
        }

        public Block getBlock() { return block; }
        public int getLastDamageTick() { return lastDamageTick; }
        public void updateTick(int tick) { this.lastDamageTick = tick; }
    }
}