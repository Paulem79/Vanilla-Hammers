package net.paulem.vanillahammers.commands;

import net.paulem.vanillahammers.Hammer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.inventory.ItemStack;

public class HammersCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("hammers")
            .then(Commands.literal("give")
                .executes(HammersCommand::runLogic)
            );
    }

    private static int runLogic(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender(); // Retrieve the command sender
        Entity executor = ctx.getSource().getExecutor(); // Retrieve the command executor, which may or may not be the same as the sender

        // Check whether the executor is a player, as you can only set a player's flight speed
        if (!(executor instanceof Player player)) {
            // If a non-player tried
            return Command.SINGLE_SUCCESS;
        }

        // Logic here
        ItemStack hammer = Hammer.getHammer();
        player.getInventory().addItem(hammer);

        // If set by a different sender
        return Command.SINGLE_SUCCESS;
    }
}