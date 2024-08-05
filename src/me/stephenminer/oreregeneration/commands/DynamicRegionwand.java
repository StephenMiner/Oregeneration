package me.stephenminer.oreregeneration.commands;

import me.stephenminer.oreregeneration.Items.Items;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DynamicRegionwand implements CommandExecutor {
    private final Items items;
    public DynamicRegionwand(){
        this.items = new Items();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if (!(player.hasPermission("oreGen.commands")))
                return false;
            player.sendMessage(ChatColor.GREEN + "You have received your wand!");
            player.getInventory().addItem(items.dynamicwand());
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Sorry, but non-players cannot receive items!");
        return false;
    }
}
