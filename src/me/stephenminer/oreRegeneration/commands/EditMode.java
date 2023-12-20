package me.stephenminer.oreRegeneration.commands;

import me.stephenminer.oreRegeneration.Events.Interact;
import me.stephenminer.oreRegeneration.OreRegeneration;
import me.stephenminer.oreRegeneration.Regions.Region;
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

public class EditMode implements CommandExecutor, TabCompleter {
    private final OreRegeneration plugin;
    public EditMode(OreRegeneration plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("editMode")){
            if (sender instanceof Player){
                Player p = (Player) sender;
                if (!p.hasPermission("oreGen.commands.editmode"))
                    return false;
            }
            if (args.length == 0){
                sender.sendMessage(ChatColor.RED + "Please specify a region to put into Edit Mode!");
                return false;
            }
            String s = args[0];

            if (plugin.regions.size() <= 0){
                sender.sendMessage(ChatColor.YELLOW + "You have no regions!!!!");
                return false;
            }
            for (Region region : plugin.regions){
                if (region.getId().equalsIgnoreCase(s)){
                    if (args.length != 2){
                        sender.sendMessage(ChatColor.RED + "Please specify whether you are turning Edit Mode on or off for this region!");
                        return false;
                    }
                    String s1 = args[1];
                    boolean turnOn = turnOn(s1);
                    region.setEditmode(turnOn);
                    if (turnOn){
                        sender.sendMessage(ChatColor.GREEN + "Edit Mode in " + s + " was turned on!");
                    }else sender.sendMessage(ChatColor.GREEN + "Edit Mode in " + s + " was turned off!");
                }
            }
        }
        return false;
    }

    private boolean turnOn(String input){
        return input.equalsIgnoreCase("on");
    }


    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("editMode")){
            int size = args.length;
            if (size == 1) return regionList(args[0]);
            if (size == 2) return switches(args[1]);
        }
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
        List<String> names = new ArrayList<>();
        for (Region region : plugin.regions) {
            names.add(region.getId());
        }
        return filter(names, match);
    }
    private List<String> switches(String match){
        List<String> switches = new ArrayList<>();
        switches.add("on");
        switches.add("off");
        return filter(switches, match);
    }
}

