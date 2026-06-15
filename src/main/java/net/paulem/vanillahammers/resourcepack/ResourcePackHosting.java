package net.paulem.vanillahammers.resourcepack;

import io.javalin.Javalin;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mcbrawls.inject.javalin.InjectJavalinFactory;
import net.mcbrawls.inject.spigot.InjectSpigot;
import net.paulem.krimson.resourcepack.creator.ResourcePackKt;
import net.paulem.vanillahammers.VanillaHammers;
import net.radstevee.packed.core.pack.PackFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;

public class ResourcePackHosting implements Listener {
    private static final String RESOURCE_PACK_PATH = "krimson-pack";
    @Nullable
    public File pack;

    @Getter
    private boolean isJavalinReady = false;
    @Getter
    private Javalin javalin;

    public ResourcePackHosting() {
        // This constructor is intentionally empty
    }

    public void aggregate(File zipFile) {
        this.pack = zipFile;
    }

    public void start() {
        javalin = InjectJavalinFactory.create(InjectSpigot.INSTANCE, config -> {
            config.routes.get("/" + RESOURCE_PACK_PATH, ctx -> {
                if (pack == null) {
                    // Error if not aggregated
                    ctx.status(404).result("Resource pack not available yet");
                    return;
                }
                ctx.result(Files.readAllBytes(pack.toPath()));
            });

            config.events.serverStarted(() -> {
                VanillaHammers.INSTANCE.getLogger().info("Javalin started");
                isJavalinReady = true;
            });

            config.events.serverStartFailed(() -> {
                VanillaHammers.INSTANCE.getLogger().info("Javalin failed to start");
                isJavalinReady = true;
            });
        });

        VanillaHammers.INSTANCE.getLogger().info("Javalin initialized");

        javalin.start();
    }

    public void stop() {
        if (javalin != null) {
            javalin.stop();
            VanillaHammers.INSTANCE.getLogger().info("Javalin stopped");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (pack == null) {
            aggregate(ResourcePackKt.main(VanillaHammers.INSTANCE.getDataFolder(), PackFormat.LATEST));
            VanillaHammers.INSTANCE.getLogger().info("Generated resource pack");
        }

        try {
            player.addResourcePack(
                    UUID.nameUUIDFromBytes(Files.readAllBytes(pack.toPath())),
                    "http://localhost:" + Bukkit.getPort() + "/" + RESOURCE_PACK_PATH,
                    createSha1(pack),
                    NamedTextColor.GREEN + "Hammer Resource Pack",
                    false
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] createSha1(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (InputStream fis = new FileInputStream(file)) {
                int n = 0;
                byte[] buffer = new byte[8192];
                while (n != -1) {
                    n = fis.read(buffer);
                    if (n > 0) {
                        digest.update(buffer, 0, n);
                    }
                }
            }
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
