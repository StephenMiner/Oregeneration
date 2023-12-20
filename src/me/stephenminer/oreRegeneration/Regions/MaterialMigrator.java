package me.stephenminer.oreRegeneration.Regions;

import jdk.nashorn.internal.ir.Block;
import me.stephenminer.oreRegeneration.Files.ConfigFile;
import me.stephenminer.oreRegeneration.OreRegeneration;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MaterialMigrator {
    private final String regionId;
    private final OreRegeneration plugin;
    public MaterialMigrator(String regionId){
        this.plugin = JavaPlugin.getPlugin(OreRegeneration.class);
        this.regionId = regionId;
    }


    public void migrate(boolean dynamic){
        String path = "regions." + regionId + ".canBreak";
        if (!dynamic){
            region_statement: {
                if (!plugin.RegionStorageFile.getConfig().contains(path)) break region_statement;
                Set<String> canBreak = plugin.RegionStorageFile.getConfig().getConfigurationSection(path).getKeys(false);
                for (String entry : canBreak){
                    migrateSection("replaceWith","replace-with",entry, false);
                    migrateSection("replenishAs","replace-as",entry,false);
                }
            }
        }else{
            region_statement:{
                if (!plugin.DynamicRegionFile.getConfig().contains(path)) break region_statement;
                Set<String> canBreak = plugin.DynamicRegionFile.getConfig().getConfigurationSection(path).getKeys(false);
                for (String entry : canBreak){
                    migrateSection("replaceWith","replace-with",entry, true);
                    migrateSection("replaceOn","replace-on",entry,true);
                }
            }
        }
    }

    private void migrateSection(String oldSection, String newSection, String entry, boolean dynamic){
        ConfigFile file = dynamic ? plugin.DynamicRegionFile : plugin.RegionStorageFile;
        String path = "regions." + regionId + ".canBreak." + entry + "." + oldSection;
        List<String> oldEntries = file.getConfig().getStringList(path);
        Set<BlockEntry> newEntries = new HashSet<>();
        for (int i = 0; i < oldEntries.size(); i++){
            Material mat = Material.matchMaterial(oldEntries.get(i));
            newEntries.add(new BlockEntry(mat, 50));
        }
        path = path.replace(oldSection,newSection);
        List<String> toSave = newEntries.stream().map(BlockEntry::toString).collect(Collectors.toList());;
        file.getConfig().set(path,toSave);
        file.saveConfig();
    }


}

