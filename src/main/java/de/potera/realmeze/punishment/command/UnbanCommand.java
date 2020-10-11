package de.potera.realmeze.punishment.command;

import de.potera.realmeze.punishment.controller.PunishmentController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class UnbanCommand implements CommandExecutor {

    private final PunishmentController punishmentController;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(command.getName().equalsIgnoreCase("unban")){
            if(args.length == 1) {
                if(getPunishmentController().unban(Bukkit.getOfflinePlayer(args[0]))){
                    commandSender.sendMessage("unbanned " + args[0]);
                    return true;
                } else{
                    commandSender.sendMessage("wasnt banned " + args[0]);
                    return true;
                }
            } else {
                showHelp(commandSender);
            }
        }
        return false;
    }

    public void showHelp(CommandSender sender){
            sender.sendMessage("/unban player");
    }
}
