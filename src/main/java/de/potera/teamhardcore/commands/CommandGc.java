package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class CommandGc implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("potera.gc")) {
            sender.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        boolean flagFlags = false;
        boolean flagEntities = false;
        boolean flagChunks = false;

        if (args.length > 0) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("-f"))
                    flagFlags = true;
                if (arg.equalsIgnoreCase("-e"))
                    flagEntities = true;
                if (arg.equalsIgnoreCase("-c")) {
                    flagChunks = true;
                }
                if (arg.equalsIgnoreCase("-a")) {
                    flagChunks = true;
                    flagEntities = true;
                    flagFlags = true;
                }
            }
        }

        Runtime runtime = Runtime.getRuntime();
        OperatingSystemMXBean opMxBean = ManagementFactory.getOperatingSystemMXBean();
        File file = new File(".");
        sender.sendMessage(StringDefaults.HEADER);
        sender.sendMessage("" + (opMxBean.getSystemLoadAverage() * 100.0D));
        sender.sendMessage(
                " §7Uptime: §e" + TimeUtil.timeToString(ManagementFactory.getRuntimeMXBean().getUptime(), false));
        sender.sendMessage(
                " §7TPS: §e" + Math.min(Math.round(Bukkit.getServer().spigot().getTPS()[0] * 100.0D) / 100.0D,
                        20.0D));
        sender.sendMessage(" §7CPU-Auslastung: §e" + (int) Math.min(opMxBean.getSystemLoadAverage(), 100.0D) + "%");
        sender.sendMessage(" §7Max. Ram: §e" + (runtime.maxMemory() / 1024L / 1024L) + " MB");
        sender.sendMessage(
                " §7Benutzter Ram: §e" + ((runtime.totalMemory() - runtime.freeMemory()) / 1024L / 1024L) + " MB");
        sender.sendMessage(" §7Totaler Speicher: §e" + Math.round(
                (float) (file.getTotalSpace() / 1024L / 1024L / 1024L)) + " GB");
        sender.sendMessage(
                " §7Freier Speicher: §e" + Math.round((float) (file.getFreeSpace() / 1024L / 1024L / 1024L)) + " GB");

        if (flagEntities) {
            int entities = 0;
            int tiles = 0;

            for (World world : Bukkit.getWorlds()) {
                entities += world.getEntities().size();
                for (Chunk ch : world.getLoadedChunks()) {
                    tiles += (ch.getTileEntities()).length;
                }
            }

            sender.sendMessage(" §7Entities: §e" + entities);
            sender.sendMessage(" §7TileEntities: §e" + tiles);
        }

        if (flagChunks) {
            int loadedChunks = 0;
            for (World world : Bukkit.getWorlds()) {
                loadedChunks += (world.getLoadedChunks()).length;
            }
            sender.sendMessage(" §7Chunks geladen: §e" + loadedChunks);
        }

        if (flagFlags) {
            StringBuilder sb = new StringBuilder();
            for (String flag : ManagementFactory.getRuntimeMXBean().getInputArguments())
                sb.append(flag).append("§8, §e");
            String flags = (sb.length() == 0) ? "Keine" : sb.substring(0, sb.length() - 6);
            sender.sendMessage(" §7Java-Parameter: §e" + flags);
        }
        sender.sendMessage(StringDefaults.FOOTER);

        return true;
    }
}