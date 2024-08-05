package me.stephenminer.oreregeneration.commands;

import me.stephenminer.oreregeneration.OreRegeneration;
import me.stephenminer.oreregeneration.Regions.Migrator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class Migrate implements CommandExecutor, TabCompleter {
    private final OreRegeneration plugin;

    public Migrate(OreRegeneration plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("migrateregion")){
            if (sender instanceof Player){
                Player player = (Player) sender;
                if (!player.hasPermission("oreGen.commands.migrate")){
                    player.sendMessage(ChatColor.RED + "You do not have permission to send this command!");
                    return false;
                }
            }
            int size = args.length;
            if (size < 1){
                sender.sendMessage(ChatColor.RED + "Sorry, but you need to input a region that you want to migrate!");
                return false;
            }
            String id = args[0];
            if (plugin.DynamicRegionFile.getConfig().contains("regions"))
                for (String key : plugin.DynamicRegionFile.getConfig().getConfigurationSection("regions").getKeys(false)){
                    if (id.equalsIgnoreCase(key) && plugin.DynamicRegionFile.getConfig().contains("regions." + key + ".world")){
                        Migrator migrator = new Migrator(plugin, id, true);
                        migrator.secondMigration();
                       // DynamicRegion.migrate(plugin, id);
                        sender.sendMessage(ChatColor.GREEN + "Migrating your dynamic region now!");
                        return true;
                    }
                    if (id.equalsIgnoreCase(key) && plugin.DynamicRegionFile.getConfig().contains("regions." + key + ".loc1")){
                        Migrator migrator = new Migrator(plugin, id, true);
                        migrator.firstMigration();
                        sender.sendMessage(ChatColor.GREEN + "Migrated your dynamic region!");
                    }
                }
            if (plugin.RegionStorageFile.getConfig().contains("regions"))
                for (String key : plugin.RegionStorageFile.getConfig().getConfigurationSection("regions").getKeys(false)){
                    if (id.equalsIgnoreCase(key) && plugin.RegionStorageFile.getConfig().contains("regions." + key + ".world")){
                        Migrator migrator = new Migrator(plugin, id, false);
                        migrator.secondMigration();
                        sender.sendMessage(ChatColor.GREEN + "Migrating your region now!");
                        return true;
                    }
                    if (id.equalsIgnoreCase(key) && plugin.RegionStorageFile.getConfig().contains("regions." + key + ".loc1")){
                        Migrator migrator = new Migrator(plugin, id, false);
                        migrator.firstMigration();
                        sender.sendMessage(ChatColor.GREEN + "Migrating your region now!");
                        return true;
                    }
                }
        }
        sender.sendMessage(ChatColor.RED + "Inputted region is already migrated or doesn't exist!");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("migrateregion")){
            int size = args.length;
            if (size == 1) return unmigrated(args[0]);
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


    private List<String> unmigrated(String match){
        List<String> names = new ArrayList<>();
        if (plugin.RegionStorageFile.getConfig().contains("regions")){
            Set<String> regions = plugin.RegionStorageFile.getConfig().getConfigurationSection("regions").getKeys(false);
            for (String entry : regions){
                if (!plugin.RegionStorageFile.getConfig().contains("regions." + entry + ".pos1")){
                    names.add(entry);
                }
            }
        }
        if (plugin.DynamicRegionFile.getConfig().contains("regions")){
            Set<String> regions = plugin.DynamicRegionFile.getConfig().getConfigurationSection("regions").getKeys(false);
            for (String entry : regions){
                if (!plugin.DynamicRegionFile.getConfig().contains("regions." + entry + ".pos1")){
                    names.add(entry);
                }
            }
        }
        return filter(names, match);
    }
}
