package me.stephenminer.oreregeneration.Regions;

import me.stephenminer.oreregeneration.Files.ConfigFile;
import me.stephenminer.oreregeneration.OreRegeneration;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class ChanceEditor {
    private final OreRegeneration plugin;
    private String  regionId;
    private Type type;
    private Material key, material;
    private boolean dynamic;

    /**
     *
     * @param regionId
     * @param type replace with or replace as?
     * @param key the block under canBreak section
     * @param material material you are setting chance for
     */
    public ChanceEditor(String regionId, boolean dynamic, Type type, Material key, Material material){
        this.plugin = JavaPlugin.getPlugin(OreRegeneration.class);
        this.regionId = regionId;
        this.dynamic = dynamic;
        this.key = key;
        this.type = type;
        this.material = material;
    }

    public void writeWeight(int weight){
        ConfigFile file = dynamic ? plugin.DynamicRegionFile : plugin.RegionStorageFile;
        String path = "regions." + regionId + ".canBreak." + key.name() + "." + type.section();
        List<String> entries = file.getConfig().getStringList(path);
        System.out.println(entries);
        List<BlockEntry> blockEntries = entries.stream().map(BlockEntry::new).collect(Collectors.toList());
        for (int i = 0; i < blockEntries.size(); i++){
            BlockEntry entry = blockEntries.get(i);
            if (entry.mat() == material){
                entry.setWeight(weight);
                blockEntries.set(i,entry);
                break;
            }
        }
        file.getConfig().set(path, blockEntries.stream().map(BlockEntry::toString).collect(Collectors.toList()));
        file.saveConfig();
    }


    public String regionid(){ return regionId; }
    public Type type(){ return type; }
    public Material key(){ return key; }
    public Material mat(){ return material; }




    public enum Type{
        REPLACE_WITH("replaceWith"),
        REPLACE_AS("replenishAs");

        private final String section;
        private Type(String section){
            this.section = section;
        }

        public String section(){ return section; }
    }
}
