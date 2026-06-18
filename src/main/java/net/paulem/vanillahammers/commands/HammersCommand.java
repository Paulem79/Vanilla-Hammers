package net.paulem.vanillahammers.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.paulem.vanillahammers.hammers.Hammer;
import org.bukkit.Material;
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
    private HammersCommand() {
        /* This utility class should not be instantiated */
    }


    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("hammers")
            .then(Commands.literal("give")
                    .then(Commands.argument("type", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                for (Material material : Hammer.HAMMERS.keys()) {
                                    builder.suggest(material.name());
                                }

                                return builder.buildFuture();
                            })
                            .executes(HammersCommand::runLogic))
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
        String type = ctx.getArgument("type", String.class);
        Material material = Material.matchMaterial(type.toUpperCase());

        if(material == null) {
            sender.sendMessage("Material not found");
            return Command.SINGLE_SUCCESS;
        }

        Hammer hammer = Hammer.HAMMERS.getOrNull(material);

        if(hammer == null) {
            sender.sendMessage("Hammer not found");
            return Command.SINGLE_SUCCESS;
        }

        ItemStack hammerStack = hammer.getStack();
        player.getInventory().addItem(hammerStack);

        // If set by a different sender
        return Command.SINGLE_SUCCESS;
    }
}