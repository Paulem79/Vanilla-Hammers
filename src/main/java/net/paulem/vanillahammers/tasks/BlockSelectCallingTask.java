package net.paulem.vanillahammers.tasks;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.paulem.vanillahammers.utils.RaycastUtils;
import net.paulem.vanillahammers.events.BlockSelectEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class BlockSelectCallingTask implements Consumer<ScheduledTask> {
    public Map<Player, Location> selectedBlocks = new HashMap<>();

    @Override
    public void accept(ScheduledTask scheduledTask) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(player.getGameMode() == GameMode.SPECTATOR) {
                new BlockSelectEvent(player, null, null).callEvent();
                continue;
            }

            Entity entityInPath = RaycastUtils.getTargetEntity(player, Set.of(Shulker.class));

            Block block = RaycastUtils.getTargetBlock(player);

            if (block == null) {
                selectedBlocks.remove(player);

                new BlockSelectEvent(player, null, null).callEvent();
                continue;
            }

            // If entity isn't null and it's closer to the player than the block, then continue
            if (entityInPath != null) {
                Location playerLocation = player.getLocation();
                double blockDistance = playerLocation.distanceSquared(block.getLocation());
                double entityDistance = playerLocation.distanceSquared(entityInPath.getLocation());
                if (entityDistance < blockDistance) {
                    selectedBlocks.remove(player);

                    new BlockSelectEvent(player, null, null).callEvent();
                    continue;
                }
            }

            Location loc = block.getLocation();

            Location lastLoc = selectedBlocks.get(player);
            if (lastLoc == null || lastLoc.distanceSquared(loc) >= 0.01) {
                BlockFace face = RaycastUtils.getTargetBlockFace(player);

                selectedBlocks.put(player, loc);

                // New block selected
                new BlockSelectEvent(player, block, face).callEvent();
            }
        }
    }
}
