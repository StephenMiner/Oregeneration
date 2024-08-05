package me.stephenminer.oreregeneration.Regions;

import me.stephenminer.oreregeneration.OreRegeneration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.logging.Level;

public class Migrator {

    private final OreRegeneration plugin;
    private final boolean dynamic;
    private final String id;

    public Migrator(OreRegeneration plugin, String region, boolean dynamic){
        this.plugin = plugin;
        this.dynamic = dynamic;
        this.id = region;
        regionPath = "regions." + id;
    }


    public boolean firstMigration(){
        Location loc1;
        Location loc2;
        if (dynamic){
            try{
                loc1 = (Location) plugin.DynamicRegionFile.getConfig().get("regions." + id + ".loc1");
                loc2 = (Location) plugin.DynamicRegionFile.getConfig().get("regions." + id + ".loc2");
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc1", null);
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc2", null);
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".pos1", plugin.fromBlockLoc(loc1));
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".pos2", plugin.fromBlockLoc(loc2));
                plugin.DynamicRegionFile.saveConfig();
                plugin.regions.add(new DynamicRegion(plugin, id));
            }catch (Exception e){
                plugin.getLogger().log(Level.WARNING, "Attemped to load locs for unmigrated region " + id + ", please make sure all worlds with regions are loaded!");
                return false;
            }
        }else{
            try {
                loc1 = (Location) plugin.RegionStorageFile.getConfig().get("regions." + id + ".loc1");
                loc2 = (Location) plugin.RegionStorageFile.getConfig().get("regions." + id + ".loc2");
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc1", null);
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc2", null);
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".pos1", plugin.fromBlockLoc(loc1));
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".pos2", plugin.fromBlockLoc(loc2));
                plugin.RegionStorageFile.saveConfig();
                plugin.regions.add(new Region(plugin, id));
            }catch (Exception e){
                plugin.getLogger().log(Level.WARNING, "Attemped to load locs for unmigrated region " + id + ", please make sure all worlds with regions are loaded!");
                return false;
            }
        }
        return true;
    }

    public boolean secondMigration(){
        Location loc1;
        Location loc2;
        if (dynamic){
            try{
                loc1 = getLegacyLocDyn(true);
                loc2 = getLegacyLocDyn(false);
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".world", null);
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc1", null);
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc2", null);
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".pos1", plugin.fromBlockLoc(loc1));
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".pos2", plugin.fromBlockLoc(loc2));
                plugin.DynamicRegionFile.saveConfig();
                plugin.regions.add(new DynamicRegion(plugin, id));
            }catch (Exception e){
                plugin.getLogger().log(Level.WARNING, "Attemped to load locs for unmigrated region " + id + ", please make sure all worlds with regions are loaded!");
                return false;
            }

        }else{
            try{
                loc1 = getLegacyLoc(true);
                loc2 = getLegacyLoc(false);
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".world", null);
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc1", null);
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc2", null);
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".pos1", plugin.fromBlockLoc(loc1));
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".pos2", plugin.fromBlockLoc(loc2));
                plugin.RegionStorageFile.saveConfig();
                plugin.regions.add(new Region(plugin, id));
            }catch (Exception e){
                plugin.getLogger().log(Level.WARNING, "Attemped to load locs for unmigrated region " + id + ", please make sure all worlds with regions are loaded!");
                return false;
            }

        }
        return true;
    }
    private String regionPath;
    public Location getLegacyLocDyn(boolean loc1) {
        String locpath = loc1 ? "loc1" : "loc2";
        String name = plugin.DynamicRegionFile.getConfig().getString(regionPath + ".world");
        World world;
        try {
            world = Bukkit.getWorld(name);
        } catch (Exception e) {
            world = new WorldCreator(name).createWorld();
        }
        int x = plugin.DynamicRegionFile.getConfig().getInt(regionPath + "." + locpath + ".x");
        int y = plugin.DynamicRegionFile.getConfig().getInt(regionPath + "." + locpath + ".y");
        int z = plugin.DynamicRegionFile.getConfig().getInt(regionPath + "." + locpath + ".z");
        return new Location(world, x, y, z);
    }
    public Location getLegacyLoc(boolean loc1) {
        String locpath = loc1 ? "loc1" : "loc2";
        String name = plugin.RegionStorageFile.getConfig().getString(regionPath + ".world");
        World world;
        try {
            world = Bukkit.getWorld(name);
        } catch (Exception e) {
            world = new WorldCreator(name).createWorld();
        }
        int x = plugin.RegionStorageFile.getConfig().getInt(regionPath + "." + locpath + ".x");
        int y = plugin.RegionStorageFile.getConfig().getInt(regionPath + "." + locpath + ".y");
        int z = plugin.RegionStorageFile.getConfig().getInt(regionPath + "." + locpath + ".z");
        return new Location(world, x, y, z);
    }
}
