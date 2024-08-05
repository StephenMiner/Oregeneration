package me.stephenminer.oreregeneration.commands;

import me.stephenminer.oreregeneration.DynamicRegion.EditDynamic;
import me.stephenminer.oreregeneration.OreRegeneration;
import me.stephenminer.oreregeneration.Regions.AddOptions;
import me.stephenminer.oreregeneration.Regions.EditRegion;
import me.stephenminer.oreregeneration.Regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class RegionCmd implements CommandExecutor, TabCompleter {
    private final OreRegeneration plugin;
    public RegionCmd(OreRegeneration plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean dynamic = false;
        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Sorry! Only players can use this command!");
            return false;
        }
        if (!sender.hasPermission("oreGen.commands.region")){
            sender.sendMessage(ChatColor.RED + "Sorry but you do not have permission to use this command!");
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Please specify a region you want to edit!");
            return false;
        }
        String s;
        s = checkExistence(args[0]);
        if (s == null){
            s = checkExistence(args[0].replace('_',' '));
            if (s == null) {
                sender.sendMessage(ChatColor.RED + "Please input a valid region!");
                return false;
            }
        }
        if (plugin.DynamicRegionFile.getConfig().contains("regions." + s)) {
            dynamic = true;
        }
        Player player = (Player) sender;
        if (args.length > 1){
            StringBuilder builder = new StringBuilder();
            for (int i = 2; i < args.length; i++){
                builder.append(args[i]).append(" ");
            }
            if (args[1].equalsIgnoreCase("setPlaceMsg")){
                if (dynamic){
                    plugin.DynamicRegionFile.getConfig().set("regions." + s + ".build-msg", builder.toString());
                    plugin.DynamicRegionFile.saveConfig();
                } else{
                    plugin.RegionStorageFile.getConfig().set("regions." + s + ".build-msg", builder.toString());
                    plugin.RegionStorageFile.saveConfig();
                }
                player.sendMessage(ChatColor.GREEN + "Set the deny-place message for the region " + s);
                return true;
            }
            if (args[1].equalsIgnoreCase("setBreakMsg")){
                if (dynamic){
                    plugin.DynamicRegionFile.getConfig().set("regions." + s + ".break-msg", builder.toString());
                    plugin.DynamicRegionFile.saveConfig();
                } else{
                    plugin.RegionStorageFile.getConfig().set("regions." + s + ".break-msg", builder.toString());
                    plugin.RegionStorageFile.saveConfig();
                }
                player.sendMessage(ChatColor.GREEN + "Set the deny-break message for the region " + s);
                return true;
            }
            if (args[1].equalsIgnoreCase("setFullMsg")){
                if (dynamic){
                    plugin.DynamicRegionFile.getConfig().set("regions." + s + ".full-msg", builder.toString());
                    plugin.DynamicRegionFile.saveConfig();
                } else{
                    plugin.RegionStorageFile.getConfig().set("regions." + s + ".full-msg", builder.toString());
                    plugin.RegionStorageFile.saveConfig();
                }
                player.sendMessage(ChatColor.GREEN + "Set the inventory full message for the region " + s);
                return true;
            }
            if (args[1].equalsIgnoreCase("setBreakSound")){
                try{
                    Sound sound = Sound.valueOf(args[2].toUpperCase(Locale.ROOT));

                    if (args.length >= 5){
                        try{
                            float volume = Float.parseFloat(args[3]);
                            float pitch = Float.parseFloat(args[4]);
                            if (dynamic){
                                plugin.DynamicRegionFile.getConfig().set("regions." + s + ".break-sound.name", sound.name());
                                plugin.DynamicRegionFile.getConfig().set("regions." + s + ".break-sound.volume", volume);
                                plugin.DynamicRegionFile.getConfig().set("regions." + s + ".break-sound.pitch", pitch);
                                plugin.DynamicRegionFile.saveConfig();
                            }else{
                                plugin.RegionStorageFile.getConfig().set("regions." + s + ".break-sound.name", sound.name());
                                plugin.RegionStorageFile.getConfig().set("regions." + s + ".break-sound.volume", volume);
                                plugin.RegionStorageFile.getConfig().set("regions." + s + ".break-sound.pitch", pitch);
                                plugin.RegionStorageFile.saveConfig();
                            }
                            player.sendMessage(ChatColor.GREEN + "Set the block-break sound for blocks in region " + s + " to " + sound.name());
                            return true;
                        }catch (Exception e){
                            plugin.getLogger().log(Level.WARNING, "Attempted to parse floats " + args[3] + " and "  + args[4] + ", but there was no float!");
                        }
                        player.sendMessage(ChatColor.RED + "Something went wrong when reading your volume/pitch values, make sure you used real decimal values!");
                        return false;
                    }else{
                        player.sendMessage(ChatColor.RED + "Not enough arguments!");
                        player.sendMessage(ChatColor.RED + "Proper Usage: /region [region] setBreakSound [sound] [volume (1 is the norm)] [pitch (1 is the norm)]");
                        return false;
                    }
                }catch (Exception ignored){}
                player.sendMessage(ChatColor.RED + "The sound " + args[2] + " doesn't exist!");
                return false;
            }
            if (args[1].equalsIgnoreCase("removeBreakSound")){
                if (dynamic){
                    plugin.DynamicRegionFile.getConfig().set("regions." + s + ".break-sound", null);
                    plugin.DynamicRegionFile.saveConfig();
                }else {
                    plugin.RegionStorageFile.getConfig().set("regions." + s + ".break-sound", null);
                    plugin.RegionStorageFile.saveConfig();
                }
                player.sendMessage(ChatColor.GREEN + "Removed added sound effects for breaking blocks in region " + s);
                return true;
            }

            if (args[1].equalsIgnoreCase("setPickupMsg")){
                try {
                    Material mat = Material.matchMaterial(args[2].toUpperCase(Locale.ROOT));
                    if (dynamic) {
                        plugin.DynamicRegionFile.getConfig().set("regions." + s + ".pickup-msg." + mat.name(), builder.toString().replace(args[2] + " ", ""));

                        plugin.DynamicRegionFile.saveConfig();
                    }else{
                        plugin.RegionStorageFile.getConfig().set("regions." + s + ".pickup-msg." + mat.name(), builder.toString().replace(args[2] + " ", ""));
                        plugin.RegionStorageFile.saveConfig();
                    }
                    player.sendMessage(ChatColor.GREEN + "Added pickup-msg for material " + args[2] + " in region " + s);
                    player.sendMessage(ChatColor.YELLOW + "Note: This message will only pop up if you have the dropInInventory option on for this region!");
                    return true;
                }catch (Exception ignored){}
                player.sendMessage(ChatColor.RED + "Inputted material " + args[2] + " doesn't exist!");
                return false;
            }
        }


        AddOptions.string.remove(player.getUniqueId());
        AddOptions.string.put(player.getUniqueId(), s);
        if (dynamic){
            EditDynamic ed = new EditDynamic(plugin,s);
            player.openInventory(ed.editMenu());
        }else {
            EditRegion er = new EditRegion(plugin, s);
            player.openInventory(er.editMenu());
        }
        return true;
    }

    private String checkExistence(String target){
        if (plugin.RegionStorageFile.getConfig().contains("regions")) {
            for (String key : plugin.RegionStorageFile.getConfig().getConfigurationSection("regions").getKeys(false)) {
                if (key.equalsIgnoreCase(target))
                    return key;
            }
        }
        if (plugin.DynamicRegionFile.getConfig().contains("regions")){
            for (String key : plugin.DynamicRegionFile.getConfig().getConfigurationSection("regions").getKeys(false)){
                if (key.equalsIgnoreCase(target)) {
                    return key;
                }
            }
        }
        return null;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        int count = args.length;
        if (count == 1) return regionList(args[0]);
        if (count == 2) return subCommands();
        if (count == 3){
            if (args[1].equalsIgnoreCase("setBreakSound")) return sounds(args[2]);
            if (args[1].equalsIgnoreCase("setBreakMsg")) return yourMessageHere(false);
            if (args[1].equalsIgnoreCase("setPlaceMsg")) return yourMessageHere(false);
            if (args[1].equalsIgnoreCase("setFullMsg")) return yourMessageHere(false);
            if (args[1].equalsIgnoreCase("setPickupMsg")) return materials(args[2]);
        }
        if (count == 4){
            if (args[1].equalsIgnoreCase("setBreakSound")) return volume();
            if (args[1].equalsIgnoreCase("setPickupMsg")) return yourMessageHere(true);
        }
        if (count == 5){
            if (args[1].equalsIgnoreCase("setBreakSound")) return pitch();
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

    private List<String> subCommands() {
        List<String> subs = new ArrayList<>();
        subs.add("setBreakMsg");
        subs.add("setPlaceMsg");
        subs.add("setFullMsg");
        subs.add("setBreakSound");
        subs.add("removeBreakSound");
        subs.add("setPickupMsg");
        return subs;
    }

    private List<String> volume(){
        List<String> info = new ArrayList<>();
        info.add("[volume decimal number]");
        return info;
    }
    private List<String> pitch(){
        List<String> info = new ArrayList<>();
        info.add("[pitch decimal number]");
        return info;
    }

    private List<String> yourMessageHere(boolean x){
        List<String> info = new ArrayList<>();
        info.add("[Your message here]");
        if (x) info.add("[use '[x]' as a stand-in for amount of items dropped]");
        return info;
    }

    private List<String> materials(String match){
        List<String> mats = new ArrayList<>();
        for (Material mat : Material.values()){
            if (!mat.isAir()) mats.add(mat.name());
        }
        return filter(mats, match);
    }

    private List<String> regionList(String match){
        List<String> regions = new ArrayList<>();
        for (Region region : plugin.regions){
            regions.add(region.getId());
        }
        return filter(regions, match);
    }

    private List<String> sounds(String match){
        List<String> sounds = new ArrayList<>();
        for (Sound sound : Sound.values()){
            sounds.add(sound.name());
        }
        return filter(sounds, match);
    }
}
