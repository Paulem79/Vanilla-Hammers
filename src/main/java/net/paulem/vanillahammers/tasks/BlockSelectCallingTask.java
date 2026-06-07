package net.paulem.vanillahammers.tasks;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.paulem.vanillahammers.Utils;
import net.paulem.vanillahammers.events.BlockSelectEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BlockSelectCallingTask implements Consumer<ScheduledTask> {
    public Map<Player, Location> selectedBlocks = new HashMap<>();

    @Override
    public void accept(ScheduledTask scheduledTask) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int playerBlockRange = (int) Utils.getPlayerBlockRange(player);

            Block block = player.getTargetBlockExact(playerBlockRange);
            if (block == null) continue;

            Location loc = block.getLocation();

            Location lastLoc = selectedBlocks.get(player);
            if (lastLoc == null || !(lastLoc.distanceSquared(loc) < 0.01)) {
                BlockFace face = player.getTargetBlockFace(playerBlockRange);

                // New block selected
                new BlockSelectEvent(player, block, face).callEvent();
            }

            selectedBlocks.put(player, loc);
        }
    }
}
