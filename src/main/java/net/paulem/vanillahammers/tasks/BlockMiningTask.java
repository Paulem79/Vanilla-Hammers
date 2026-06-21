package net.paulem.vanillahammers.tasks;

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
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlockMiningTask implements Listener {
    private final Map<UUID, MiningSession> activeMining = new ConcurrentHashMap<>();
    private static final int TICK_THRESHOLD = 5;

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        UUID uuid = player.getUniqueId();
        int currentTick = Bukkit.getCurrentTick();

        MiningSession session = activeMining.get(uuid);

        if (session == null || !session.getBlock().getLocation().equals(block.getLocation())) {
            // Cancel old task if mining a different block
            if (session != null && session.getTask() != null) {
                session.getTask().cancel();
            }

            MiningSession newSession = new MiningSession(block, currentTick);
            activeMining.put(uuid, newSession);

            // Start check loop on the player's region thread
            ScheduledTask task = player.getScheduler().runAtFixedRate(
                    VanillaHammers.INSTANCE,
                    (scheduledTask) -> {
                        int tickMaintenant = Bukkit.getCurrentTick();
                        MiningSession currentSession = activeMining.get(uuid);

                        if (currentSession == null) {
                            scheduledTask.cancel();
                            return;
                        }

                        // Player stopped mining
                        if (tickMaintenant - currentSession.getLastDamageTick() > TICK_THRESHOLD) {
                            if (player.isOnline()) {
                                onPlayerStopMining(player, currentSession.getBlock());
                            }
                            activeMining.remove(uuid);
                            scheduledTask.cancel();
                        }
                    },
                    null,
                    1L, 4L
            );

            newSession.setTask(task);
        } else {
            // Still mining the same block, refresh tick
            session.updateTick(currentTick);
        }
    }

    private void onPlayerStopMining(Player player, Block block) {
        BlockFace face = RaycastUtils.getTargetBlockFace(player);
        new PlayerStopDamageBlockEvent(player, block, face).callEvent();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        MiningSession session = activeMining.remove(uuid);
        if (session != null && session.getTask() != null) {
            session.getTask().cancel();
        }
    }

    private static class MiningSession {
        private final Block block;
        private int lastDamageTick;
        private ScheduledTask task;

        public MiningSession(Block block, int lastDamageTick) {
            this.block = block;
            this.lastDamageTick = lastDamageTick;
        }

        public Block getBlock() { return block; }
        public int getLastDamageTick() { return lastDamageTick; }
        public void updateTick(int tick) { this.lastDamageTick = tick; }

        public ScheduledTask getTask() { return task; }
        public void setTask(ScheduledTask task) { this.task = task; }
    }
}