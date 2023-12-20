package me.stephenminer.oreRegeneration.Nodes;

import me.stephenminer.oreRegeneration.OreRegeneration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Node {
    public static List<Node> nodes = new ArrayList<>();
    private final OreRegeneration plugin;
    private final Location loc;
    private final Material mat;
    private final UUID owner;
    public Node(OreRegeneration plugin, UUID owner, Location loc, Material mat){
        this.plugin = plugin;
        this.loc = loc;
        this.mat = mat;
        this.owner = owner;
        nodes.add(this);
    }

    public boolean onLoc(Location loc){
        return this.loc.equals(loc);
    }

    public boolean tryBreak(Player player, Location loc){
        if (this.loc.equals(loc)){
            int replenishTime = 40;
            if (plugin.DropsFile.getConfig().contains("blocks." + mat.name() + ".replenishTime"))
                replenishTime = plugin.DropsFile.getConfig().getInt("blocks." + mat.name() + ".replenishTime");
            new BukkitRunnable(){
                @Override
                public void run(){
                    loc.getBlock().setType(Material.BEDROCK);
                }
            }.runTaskLater(plugin, 2);
            Node node = this;
            Location dropLoc = loc;
            if (getDropOnPlayer()){
                dropLoc = player.getLocation();
            }
            List<ItemStack> drops = potentialDrops();
            if (drops.size() < 1) drops = new ArrayList<>(loc.getBlock().getDrops(player.getInventory().getItemInMainHand()));
            else{
                ItemStack item = drops.get(ThreadLocalRandom.current().nextInt(drops.size()));
                drops.clear();
                drops.add(item);
            }
            if (getDropInInventory()){
                for (int i = drops.size() - 1; i >= 0; i--){
                    HashMap<Integer, ItemStack> remove = player.getInventory().addItem(drops.get(i));
                    if (!remove.containsValue(drops.get(i))) drops.remove(i);
                }
            }
            for (ItemStack item : drops){
                player.getWorld().dropItemNaturally(dropLoc, item);
            }
            int finalReplenishTime = replenishTime;
            new BukkitRunnable(){
                int count = 0;
                @Override
                public void run(){
                    if (!nodes.contains(node)){
                        plugin.getLogger().log(Level.INFO, "Node was removed before regeneration! (Things should be fine)");
                        this.cancel();
                        return;
                    }
                    if (count >= finalReplenishTime){
                        loc.getBlock().setType(mat);
                        this.cancel();
                        return;
                    }
                    count++;
                }
            }.runTaskTimer(plugin, 1, 1);
            return true;
        }
        return false;
    }


    public List<ItemStack> potentialDrops(){
        List<ItemStack> items = new ArrayList<>();
        if(plugin.DropsFile.getConfig().contains("blocks." + mat.name() + ".drops")) {
            Set<String> mats = plugin.DropsFile.getConfig().getConfigurationSection("blocks." + mat.name() + ".drops").getKeys(false);
            for (String mat : mats) {
                items.add(fromEntry(mat));
            }
        }
        return items;
    }

    public ItemStack fromEntry(String base){
        Material type = Material.matchMaterial(base);
        String name = null;
        List<String> lore = new ArrayList<>();
        int min = 1;
        int max = 1;

        String path = "blocks." + mat.name() + ".drops." + base;
        if (plugin.DropsFile.getConfig().contains(path + ".name"))
            name = ChatColor.translateAlternateColorCodes('&', plugin.DropsFile.getConfig().getString(path + ".name"));
        if (plugin.DropsFile.getConfig().contains(path + ".lore")){
            List<String> temp = plugin.DropsFile.getConfig().getStringList(path + ".lore");
            for (String entry : temp){
                lore.add(ChatColor.translateAlternateColorCodes('&', entry));
            }
        }
        if (plugin.DropsFile.getConfig().contains(path + ".min-drop"))
            min = plugin.DropsFile.getConfig().getInt(path + ".min-drop");
        if (plugin.DropsFile.getConfig().contains(path + ".max-drop"))
            max = plugin.DropsFile.getConfig().getInt(path + ".max-drop");
        ItemStack item = new ItemStack(type, ThreadLocalRandom.current().nextInt(min, max+1));
        ItemMeta meta = item.getItemMeta();
        if(name != null) meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void save(){
        plugin.NodeFile.getConfig().set("nodes." + plugin.fromBlockLoc(loc) + ".placer", owner.toString());
        plugin.NodeFile.getConfig().set("nodes." + plugin.fromBlockLoc(loc) + ".mat", mat.name());
        plugin.NodeFile.saveConfig();
    }
    public void remove(){
        plugin.NodeFile.getConfig().set("nodes." + plugin.fromBlockLoc(loc), null);
        plugin.NodeFile.saveConfig();
        Node.nodes.remove(this);
    }

    public boolean getDropOnPlayer(){
        return plugin.DropsFile.getConfig().getBoolean("blocks." + mat.name() + ".dropOnPlayer");
    }

    public boolean getDropInInventory(){
        return plugin.DropsFile.getConfig().getBoolean("blocks." + mat.name() + ".dropInInventory");
    }

    public Location getLoc(){ return loc; }
    public UUID getOwner(){ return owner; }
    public Material getType(){ return mat; }
}
