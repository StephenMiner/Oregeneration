package me.stephenminer.oreRegeneration;

import me.stephenminer.oreRegeneration.DynamicRegion.DynamicOptions;
import me.stephenminer.oreRegeneration.Events.DynamicEvens;
import me.stephenminer.oreRegeneration.Events.Interact;
import me.stephenminer.oreRegeneration.Events.checkPvp;
import me.stephenminer.oreRegeneration.Files.*;
import me.stephenminer.oreRegeneration.Nodes.Node;
import me.stephenminer.oreRegeneration.Nodes.OreNodes;
import me.stephenminer.oreRegeneration.Regions.AddOptions;
import me.stephenminer.oreRegeneration.Regions.DynamicRegion;
import me.stephenminer.oreRegeneration.Regions.Migrator;
import me.stephenminer.oreRegeneration.Regions.Region;
import me.stephenminer.oreRegeneration.commands.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

public class OreRegeneration extends JavaPlugin {
    public ConfigFile NodeFile;
    public ConfigFile DropsFile;
    public ConfigFile RegionStorageFile;
    public ConfigFile DynamicRegionFile;

    public List<Region> regions;


    @Override
    public void onEnable() {
        regions = new ArrayList<>();
        this.DropsFile = new ConfigFile(this, "drops");
        this.NodeFile = new ConfigFile(this, "orenode");
        this.RegionStorageFile = new ConfigFile(this, "locations");
        this.DynamicRegionFile = new ConfigFile(this, "dynamiclocations");
        registerEvents();
        registerCommands();
        loadNodes();
        loadRegions();
        createRegionSections();
        if (RegionStorageFile.getConfig().contains("regions"))
            for (String key : RegionStorageFile.getConfig().getConfigurationSection("regions").getKeys(false)){
                if (RegionStorageFile.getConfig().contains("regions." + key + ".world")){
                    Migrator migrator = new Migrator(this, key, false);
                    migrator.secondMigration();
                    continue;
                }
                if (RegionStorageFile.getConfig().contains("regions." + key + ".loc1")){
                    Migrator migrator = new Migrator(this, key, false);
                    migrator.firstMigration();
                }

            }
        if (DynamicRegionFile.getConfig().contains("regions"))
            for (String key : DynamicRegionFile.getConfig().getConfigurationSection("regions").getKeys(false)){
                if (DynamicRegionFile.getConfig().contains("regions." + key + ".world")){
                    Migrator migrator = new Migrator(this, key, true);
                    migrator.secondMigration();
                    continue;
                }
                if (DynamicRegionFile.getConfig().contains("regions." + key + ".loc1")){
                    Migrator migrator = new Migrator(this, key, true);
                    migrator.firstMigration();
                }
            }

    }

    @Override
    public void onDisable() {
        this.NodeFile.saveConfig();
        this.DropsFile.saveConfig();
        this.RegionStorageFile.saveConfig();
        for (Region region : regions){
            for (Location loc : region.getReplenishing().keySet()) {
                Material mat = region.getReplenishing().get(loc);
                Block b = loc.getBlock();
                b.setType(mat);
                if (b.getBlockData() instanceof Ageable) {
                    Ageable age = (Ageable) b.getBlockData();
                    age.setAge(age.getMaximumAge());
                    b.setBlockData(age);
                }
            }
            if (region instanceof DynamicRegion){
                DynamicRegion r = (DynamicRegion) region;
                for (Location loc : r.replaceQueue.keySet()){
                    loc.getBlock().setType(r.replaceQueue.get(loc));
                    //r.replenish(loc, r.replaceQueue.get(loc));
                }
            }
        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("node")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!(player.hasPermission("oreGen.commands.node")))
                    return false;

                ItemStack item = player.getInventory().getItemInMainHand();
                if (item == null){
                    player.sendMessage(ChatColor.RED + "You need to be holding an item to use this command!");
                    return false;
                }
                ItemMeta meta = item.getItemMeta();
                if (meta.getDisplayName().isEmpty()) {
                    meta.setDisplayName(item.getType().name().toLowerCase(Locale.ROOT) + ChatColor.BLUE + " Generator");
                } else meta.setDisplayName(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() + ChatColor.BLUE + " Generator");
                item.setItemMeta(meta);
                player.sendMessage(ChatColor.GREEN + "Your block has been generator-ified");

                return true;
            } else return false;
        }
        if (label.equalsIgnoreCase("reloadOreGen")){
            if (sender instanceof Player){
                Player p = (Player) sender;
                if (!p.hasPermission("oreGen.reload")) {
                    p.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    return false;
                }
            }
            this.RegionStorageFile.reloadConfig();
            this.DynamicRegionFile.reloadConfig();
            this.DynamicRegionFile.saveConfig();
            this.DropsFile.reloadConfig();
            this.NodeFile.reloadConfig();
            this.RegionStorageFile.saveConfig();
            this.DropsFile.saveConfig();
            this.NodeFile.saveConfig();
            sender.sendMessage(ChatColor.GREEN + "Reloaded config for Oregeneration");
            return true;
        }
        return false;
    }


    private void registerCommands(){
        RegionCmd regionCmd = new RegionCmd(this);
        DeleteRegion deleteRegion = new DeleteRegion(this);
        ShowBorder showBorder = new ShowBorder(this);
        EditMode editMode = new EditMode(this);
        Migrate migrate = new Migrate(this);


        getCommand("region").setExecutor(regionCmd);
        getCommand("deleteregion").setExecutor(deleteRegion);
        getCommand("showborder").setExecutor(showBorder);
        getCommand("regionlist").setExecutor(new RegionList(this));
        getCommand("editmode").setExecutor(editMode);
        getCommand("regionwand").setExecutor(new Regionwand());
        getCommand("region").setTabCompleter(regionCmd);
        getCommand("deleteregion").setTabCompleter(deleteRegion);
        getCommand("showborder").setTabCompleter(showBorder);
        getCommand("editmode").setTabCompleter(editMode);
        getCommand("dynamicregionwand").setExecutor(new DynamicRegionwand());
        getCommand("migrateregion").setExecutor(migrate);
        getCommand("migrateregion").setTabCompleter(migrate);
    }
    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new OreNodes(this), this);
        this.getServer().getPluginManager().registerEvents(new Interact(this), this);
        this.getServer().getPluginManager().registerEvents(new checkPvp(this), this);
        this.getServer().getPluginManager().registerEvents(new AddOptions(this), this);
        this.getServer().getPluginManager().registerEvents(new DynamicEvens(this), this);
        this.getServer().getPluginManager().registerEvents(new DynamicOptions(this), this);
    }

    public static long getBlockKey(int x, int y, int z) {
        return (long)x & 134217727L | ((long)z & 134217727L) << 27 | (long)y << 54;
    }

    public Location fromString(String str){
        String[] split = str.split(",");
        World world = null;
        double x = 0;
        double y = 0;
        double z = 0;
        try{
            world = Bukkit.getWorld(split[0]);
            x = Double.parseDouble(split[1]);
            y = Double.parseDouble(split[2]);
            z = Double.parseDouble(split[3]);
        }catch (Exception e){
            getLogger().log(Level.WARNING, "Error getting world " + split[0] + ", attempting to load the world now...");
        }
        if (world == null) world = new WorldCreator(split[0]).createWorld();
        return new Location(world, x, y, z);
    }
    public String fromBlockLoc(Location loc){
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public void createRegionSections(){
        for (Region region : regions){
            if (DropsFile.getConfig().contains("regions." + region.getId())) continue;
            DropsFile.getConfig().createSection("regions." + region.getId());
        }
        DropsFile.saveConfig();
    }

    public void loadNodes(){
        if (NodeFile.getConfig().contains("nodes")){
            for (String key : NodeFile.getConfig().getConfigurationSection("nodes").getKeys(false)){
                Location loc = fromString(key);
                try {
                    Material mat = Material.matchMaterial(NodeFile.getConfig().getString("nodes." + key + ".mat"));
                    UUID owner = UUID.fromString(NodeFile.getConfig().getString("nodes." + key + ".placer"));
                    Node node = new Node(this, owner, loc, mat);
                    loc.getBlock().setType(node.getType());
                }catch (Exception e){
                    getLogger().warning("Something went wrong loading node at location " + key + ". Make sure the material entry is correct in the orenode.yml");
                }
            }
        }
    }

    public void loadRegions(){
        if (RegionStorageFile.getConfig().contains("regions"))
            for (String key : RegionStorageFile.getConfig().getConfigurationSection("regions").getKeys(false)){
                if (RegionStorageFile.getConfig().contains("regions." + key + ".pos1"))
                    regions.add(new Region(this, key));
            }
        if (DynamicRegionFile.getConfig().contains("regions"))
            for (String key : DynamicRegionFile.getConfig().getConfigurationSection("regions").getKeys(false)){
                if (DynamicRegionFile.getConfig().contains("regions." + key + ".pos1"))
                    regions.add(new DynamicRegion(this, key));
            }
    }
}

