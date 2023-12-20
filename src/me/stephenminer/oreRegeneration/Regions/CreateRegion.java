package me.stephenminer.oreRegeneration.Regions;

import me.stephenminer.oreRegeneration.OreRegeneration;
import org.bukkit.Location;

public class CreateRegion {
    private OreRegeneration plugin;
    public CreateRegion(OreRegeneration plugin){
        this.plugin = plugin;
    }
    private Location loc1;
    private Location loc2;
    private String name;






    public void createRegion(){
        plugin.RegionStorageFile.getConfig().set("regions." + name + ".pos1", plugin.fromBlockLoc(loc1));
        plugin.RegionStorageFile.getConfig().set("regions." + name + ".pos2", plugin.fromBlockLoc(loc2));
        plugin.RegionStorageFile.saveConfig();
        plugin.RegionStorageFile.reloadConfig();
        plugin.DropsFile.getConfig().createSection("regions." + name);
        plugin.regions.add(new Region(plugin, name));
    }



    public Location getLoc1(){
        return loc1;
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
    }

    public void setLoc2(Location loc2){
        this.loc2 = loc2;
    }
    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
