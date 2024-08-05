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

public class ShowBorder implements CommandExecutor, TabCompleter {
    private OreRegeneration plugin;

    public ShowBorder(OreRegeneration plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("oreGen.commands.showborder")){
                player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return false;
            }
            if (args.length > 1) {
                String name = args[0];
                boolean on = Boolean.parseBoolean(args[1]);
                int size = plugin.regions.size();
                for (int i = 0; i < size; i++) {
                    Region region = plugin.regions.get(i);
                    if (region.getId().equalsIgnoreCase(name)) {
                        if (on){
                            region.setOutlining(true);
                            region.showBorder();
                            player.sendMessage(ChatColor.GREEN + "Showing border for region " + name);
                        }else{
                            region.setOutlining(false);
                            player.sendMessage(ChatColor.GREEN + "Stopped showing border for region " + name);
                        }
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED + "The region you are trying to see doesn't exist!");
                return false;
            }else  sender.sendMessage(ChatColor.RED + "You need to define whether you want to turn showborder on/off with true/false");
        } else {
            sender.sendMessage(ChatColor.RED + "You cant see borders");
            return false;
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        int size = args.length;
        if (size == 1) return regionList(args[0]);
        if (size == 2) return bools(args[1]);
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
        for (Region region : plugin.regions){
            names.add(region.getId());
        }
        return filter(names, match);
    }
    private List<String> bools(String match){
        List<String> bools = new ArrayList<>();
        bools.add("true");
        bools.add("false");
        return filter(bools, match);
    }


    /*
    private boolean dynamicBorder(String name){
        if (plugin.DynamicRegionFile.getConfig().contains("regions")) {
            for (String s : plugin.DynamicRegionFile.getConfig().getConfigurationSection("regions").getKeys(false)) {
                if (name.equals(s)) {
                    Set<Block> clone = Create.getOutline(name, plugin);
                    Set<Block> change = Create.getOutline(name, plugin);
                    HashMap<Location, Material> savedMats = new HashMap<Location, Material>();
                    for (Block b : clone) {
                        savedMats.put(b.getLocation(), b.getType());
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Block block : clone) {
                                if (savedMats.containsKey(block.getLocation()))
                                    block.setType(savedMats.get(block.getLocation()));
                            }
                        }
                    }.runTaskLater(plugin, 40);
                    for (Block block : change) {
                        block.setType(Material.GLASS);
                    }
                    return true;
                }
            }
        }


        return false;
    }

     */
}
