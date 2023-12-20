package me.stephenminer.oreRegeneration.commands;

import me.stephenminer.oreRegeneration.OreRegeneration;
import me.stephenminer.oreRegeneration.Regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RegionList implements CommandExecutor {
    private final OreRegeneration plugin;
    public RegionList(OreRegeneration plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("regionList")){
            if (sender instanceof Player){
                Player p = (Player) sender;
                if (!p.hasPermission("oreGen.commands"))
                    return false;
            }
            sender.sendMessage(ChatColor.GREEN + "Regions:");
            List<String> list = regionList();
            if (list.size() < 1){
                sender.sendMessage(ChatColor.RED + "You do not have any regions!");
                return false;
            }
            for (String s : list.toArray(new String[0])){
                sender.sendMessage(ChatColor.GOLD + s);
            }
            return true;


        }
        return false;
    }
    private List<String> regionList(){
        List<String> regions = new ArrayList<>();
        for (Region region : plugin.regions){
            regions.add(region.getId());
        }
        return regions;
    }
}
