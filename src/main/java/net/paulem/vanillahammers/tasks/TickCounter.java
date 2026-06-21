package net.paulem.vanillahammers.tasks;

import lombok.Getter;
import net.paulem.vanillahammers.VanillaHammers;

/**
 * This is not reliable for most operations, but it's good enough for ticks diff because it's always the "same ticking rate"
 */
public class TickCounter {
    @Getter
    private static long ticks = 0;

    public static void init() {
        // Scheduling a repeating task on a specific location's region
        VanillaHammers.getScheduler().runTaskTimer(() -> {
            ticks++;
        }, 1L, 1L);
    }
}
