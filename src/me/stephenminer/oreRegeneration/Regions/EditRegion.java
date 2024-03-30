package me.stephenminer.oreRegeneration.Regions;


import me.stephenminer.oreRegeneration.Items.Items;
import me.stephenminer.oreRegeneration.OreRegeneration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EditRegion {
    private String region;

    private OreRegeneration plugin;

    public EditRegion(OreRegeneration plugin, String regionName) {
        this.plugin = plugin;
        this.region = regionName;
        checkExistence();
    }

    public void checkExistence() {
        if (plugin.RegionStorageFile.getConfig().contains("regions." + region))
            return;
        else {
            for (String key : plugin.RegionStorageFile.getConfig().getConfigurationSection("regions").getKeys(false)) {
                region = key;
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Inputted region is invalid, defaulting to " + key);
            }
        }
    }

    public void addCanBreak(String material) {
        plugin.RegionStorageFile.getConfig().createSection("regions." + region + ".canBreak." + material.toUpperCase() + ".replaceWith");
        plugin.RegionStorageFile.getConfig().set("regions." + region + ".canBreak." + material.toUpperCase() + ".replenishTime", 40);
        plugin.RegionStorageFile.saveConfig();
    }

    public void addCanBreak(String material, int replenishTime) {
        plugin.RegionStorageFile.getConfig().set("regions." + region + ".canBreak." + material.toUpperCase() + "replenishTime", replenishTime);
        plugin.RegionStorageFile.saveConfig();
    }
    public void addCanBreak(String material, String[] replaceWith) {
        plugin.RegionStorageFile.getConfig().set("regions." + region + ".canBreak." + material.toUpperCase() + "replaceWith", replaceWith);
        plugin.RegionStorageFile.saveConfig();
    }

    public void addCanBreak(String material, int replenishTime, String[] replaceWith) {
        plugin.RegionStorageFile.getConfig().set("regions." + region + ".canBreak." + material.toUpperCase() + "replenishTime", replenishTime);
        plugin.RegionStorageFile.getConfig().set("regions." + region + ".canBreak." + material.toUpperCase() + "replaceWith", replaceWith);
        plugin.RegionStorageFile.saveConfig();
    }

    public void clearCanBreak(String material){
        if (plugin.RegionStorageFile.getConfig().contains("regions." + region + ".canBreak." + material)){
            plugin.RegionStorageFile.getConfig().set("regions." + region + ".canBreak.", null);
            plugin.RegionStorageFile.saveConfig();
        }

    }
    public void addPvp(boolean b){
        plugin.RegionStorageFile.getConfig().set("regions." + region + ".pvp", b);
        plugin.RegionStorageFile.saveConfig();
    }

    public void setDropOnPlayer(boolean b){
        plugin.RegionStorageFile.getConfig().set("regions." + region + ".dropOnPlayer", b);
        plugin.RegionStorageFile.saveConfig();
    }

    public void setDropInInventory(boolean b){
        plugin.RegionStorageFile.getConfig().set("regions." + region + ".dropInInventory", b);
        plugin.RegionStorageFile.saveConfig();
    }
    public void setGivePlayerXp(boolean b){
        plugin.RegionStorageFile.getConfig().set("regions." + region + ".givePlayerXp", b);
        plugin.RegionStorageFile.saveConfig();
    }


    public void addPvp(Player sender, boolean b){
        plugin.RegionStorageFile.getConfig().set("regions." + region + ".pvp", b);
        String s;
        if (b)
            s = "on";
        else s = "off";
        sender.sendMessage(ChatColor.GREEN + "Turned PVP " + s + " in " + region);
        plugin.RegionStorageFile.saveConfig();
    }


    public Inventory editMenu(){
        Items items = new Items();
        Inventory inv = Bukkit.createInventory(null, 9,ChatColor.GOLD + "Region " + region);
        inv.setItem(8, items.back());
        inv.setItem(5, items.canBreak(region));

        if (plugin.RegionStorageFile.getConfig().getBoolean("regions." + region + ".pvp"))
            inv.setItem(3, items.canPvp(region, plugin.RegionStorageFile.getConfig().getBoolean("regions." + region + ".pvp")));
        else inv.setItem(3, items.canPvp(region, false));
        if (plugin.RegionStorageFile.getConfig().getBoolean("regions." + region + ".mobs-spawn"))
            inv.setItem(2, items.canSpawn(region, true));
        else inv.setItem(2, items.canSpawn(region, false));
        if (plugin.RegionStorageFile.getConfig().getBoolean("regions." + region + ".dropOnPlayer"))
            inv.setItem(4, items.dropOnPlayer(true));
        else inv.setItem(4, items.dropOnPlayer(false));
        if (plugin.RegionStorageFile.getConfig().getBoolean("regions." + region + ".dropInInventory"))
            inv.setItem(1, items.dropInInventory(true));
        else inv.setItem(1, items.dropInInventory(false));
        if (plugin.RegionStorageFile.getConfig().getBoolean("regions." + region + ".givePlayerXp"))
            inv.setItem(6, items.givePlayerXp(true));
        else inv.setItem(6, items.givePlayerXp(false));
        return inv;
    }
    public ItemStack filler(){
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
    public Inventory canBreakMenu(){
        Items items = new Items();
        List<ItemStack> itemStacks = new ArrayList<>();
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Can Break in " + region);
        if (plugin.RegionStorageFile.getConfig().contains("regions." + region + ".canBreak")){
            for (String key : plugin.RegionStorageFile.getConfig().getConfigurationSection("regions." + region + ".canBreak").getKeys(false)){
                try{
                    Material mat = Material.matchMaterial(key);
                    itemStacks.add(new ItemStack(mat));
                } catch (Exception e){Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Something went wrong!");}

            }
        }
        inv.setContents(itemStacks.toArray(new ItemStack[0]));
        for (int i = 45; i <= 53; i++){
            inv.setItem(i, filler());
        }
        inv.setItem(49, items.back());
        return inv;
    }
    public Inventory replaceWithMenu(Material mat){
        Items items = new Items();

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GOLD + mat.toString().toLowerCase().replace("_"," ") +
                "Will be replaced by:");
        List<String> replaceWith = plugin.RegionStorageFile.getConfig().getStringList("regions." + region + ".canBreak." + mat.toString() + ".replaceWith");
        ItemStack[] itemArray = generateItems(replaceWith);
        inv.setContents(itemArray);
        for (int i = 45; i <= 53; i++){
            inv.setItem(i, filler());
        }
        inv.setItem(49, items.back());

        return inv;
    }
    public Inventory replenishTimeMenu(Player player, Material mat){
        Items items = new Items();
        int i = plugin.RegionStorageFile.getConfig().getInt("regions." + region + ".canBreak." + mat.toString() + ".replenishTime");
        Inventory inv = Bukkit.createInventory(player, 9, "Replenish Time");
        inv.setItem(4, items.currentTime(i));
        inv.setItem(0, filler());
        inv.setItem(1, items.addTime(1));
        inv.setItem(2,items.addTime(5));
        inv.setItem(3, items.addTime(10));
        inv.setItem(5, items.addTime(-1));
        inv.setItem(6, items.addTime(-5));
        inv.setItem(7, items.addTime(-10));
        inv.setItem(8, items.back());
        return inv;
    }
    public Inventory deleteOrReplaceWithmenu(Material mat){
        Items items = new Items();
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Delete or add options");
        inv.setItem(8, items.back());
        inv.setItem(6, items.replaceWith(region, mat));
        inv.setItem(4, items.replenishTime());
        inv.setItem(5, items.replenishAs(region));

        return inv;
    }
    public Inventory replenishAs(Material mat){
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Replenish As");
        Items items = new Items();
        List<String> stringEntries = plugin.RegionStorageFile.getConfig().getStringList("regions." + region + ".canBreak." + mat.toString() + ".replenishAs");
        ItemStack[] itemArray = generateItems(stringEntries);
        inv.setContents(itemArray);
        for (int i = 45; i <= 53; i++){
            inv.setItem(i, filler());
        }
        inv.setItem(49, items.back());
        return inv;
    }

    /**
     *
     * @param strings List of strings presumably as BlockEntries
     * @return list of items using materials found in BlockEntries and weight written in lore
     */
    private ItemStack[] generateItems(List<String> strings){
        ItemStack[] items = new ItemStack[strings.size()];
        int passed = 0;
        for (int i = 0; i < strings.size(); i++){
            String entry = strings.get(i);
            try {
                BlockEntry blockEntry = new BlockEntry(entry);
                ItemStack item = new ItemStack(blockEntry.mat());
                ItemMeta meta = item.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("weight: " + blockEntry.weight());
                meta.setLore(lore);
                item.setItemMeta(meta);
                items[i] = item;
                passed++;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (passed == 0) return failsafe(strings);
        else return items;


    }

    /**
     * Should only be used IN asBlockEntries()
     * @param strings List of strings of presumably materials
     * @return List of items using materials found in strings
     */
    private ItemStack[] failsafe(List<String> strings){
        ItemStack[] items = new ItemStack[strings.size()];
        int index = 0;
        for (String s : strings){
            if (Material.matchMaterial(s) == null)
                continue;
            ItemStack item = new ItemStack(Material.matchMaterial(s));
            items[index] = item;
            index++;
        }
        return items;
    }



    public void saveReplenishAs(ItemStack[] contents, Material mat){
        List<BlockEntry> entries = generateFromItems(contents);
        if (entries.size() > 0)
            plugin.RegionStorageFile.getConfig().set("regions." + region + ".canBreak." + mat.toString() + ".replenishAs", entries.stream().map(BlockEntry::toString).collect(Collectors.toList()));
        else plugin.RegionStorageFile.getConfig().set("regions." + region + ".canBreak." + mat.toString() + ".replenishAs", null);
        plugin.RegionStorageFile.saveConfig();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Updated replenish As in " + region+"!");
    }

    public void saveCanBreak(ItemStack[] contents){
        List<String> mats = new ArrayList<>();
        for (ItemStack stacks : contents){
            if (stacks == null){
                continue;
            }
            if (!plugin.RegionStorageFile.getConfig().contains("regions." + region + ".canBreak." + stacks.getType().toString())){
                addCanBreak(stacks.getType().toString());
            }
            mats.add(stacks.getType().toString());
        }
        plugin.RegionStorageFile.saveConfig();
        if (plugin.RegionStorageFile.getConfig().contains("regions." + region + ".canBreak")) {
            for (String s : plugin.RegionStorageFile.getConfig().getConfigurationSection("regions." + region + ".canBreak").getKeys(false)) {
                if (!mats.contains(s)) {
                    plugin.RegionStorageFile.getConfig().set("regions." + region + ".canBreak." + s, null);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed an item from the list! :(");
                }
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Updated canBreak in " + region+"!");
        plugin.RegionStorageFile.saveConfig();
    }
    public void saveReplaceWith(ItemStack[] contents, Material mat){
        List<BlockEntry> entries = generateFromItems(contents);
        plugin.RegionStorageFile.getConfig().set("regions." + region + ".canBreak." + mat.toString() + ".replaceWith", entries.stream().map(BlockEntry::toString).collect(Collectors.toList()));
        plugin.RegionStorageFile.saveConfig();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Updated replaceWith in " + region+"!");
    }


    private List<BlockEntry> generateFromItems(ItemStack[] contents){
        List<BlockEntry> blockEntries = new ArrayList<>();
        for (ItemStack item : contents){
            if (item == null) continue;
            int weight = readWeight(item);
            BlockEntry entry = new BlockEntry(item.getType(),weight);
            blockEntries.add(entry);
        }
        return blockEntries;
    }

    private int readWeight(ItemStack item){
        int weight = 50;
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return weight;
        List<String> lore = item.getItemMeta().getLore();
        String sNum = null;
        for (String entry : lore){
            if (entry.contains("weight: ")) {
                sNum = ChatColor.stripColor(entry.replace("weight: ",""));
                break;
            }
        }
        if (sNum == null) return weight;
        else weight = Integer.parseInt(sNum);
        return weight;
    }



    public enum ChanceType{
        REPLACE_WITH,
        REPLACE_AS
    }



}


