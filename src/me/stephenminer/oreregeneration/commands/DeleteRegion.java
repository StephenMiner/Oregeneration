package me.stephenminer.oreregeneration.commands;

import me.stephenminer.oreregeneration.OreRegeneration;
import me.stephenminer.oreregeneration.Regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class DeleteRegion implements CommandExecutor, TabCompleter {
    private final OreRegeneration plugin;
    public DeleteRegion(OreRegeneration plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player){
            Player p = (Player) sender;
            if (!p.hasPermission("oreGen.commands.delete"))
                return false;
        }
        if (args.length > 0){
            String name = args[0];
            List<String> list = regionList("");
            if (list.contains(name)){
                deleteRegion(name,false);
                sender.sendMessage(ChatColor.GREEN + "Deleted " + name);
                return true;
            }else{
                name = name.replace('_',' ');
                if (list.contains(name)){
                    deleteRegion(name,true);
                    sender.sendMessage(ChatColor.GREEN + "Deleted " + name);
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + name + " does not exist!");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        int size = args.length;
        if (size == 1) return regionList(args[0]);
        return null;
    }

    private List<String> filter(Collection<String> base, String match){
        List<String> filtered = new ArrayList<>();
        match = match.toLowerCase(Locale.ROOT);
        for (String entry : base){
            String temp = entry.toLowerCase(Locale.ROOT);
            if (temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }


    private List<String> regionList(String match){
        List<String> regions = new ArrayList<>();
        for (Region region : plugin.regions){
            regions.add(region.getId());
        }
        return filter(regions, match);
    }
    private void deleteRegion(String name, boolean space){
        int size = plugin.regions.size();
        for (int i = size - 1; i >= 0; i--){
            Region region = plugin.regions.get(i);
            if (region.getId().equalsIgnoreCase(name)){
                region.delete();
                return;
            }
        }
    }
}
