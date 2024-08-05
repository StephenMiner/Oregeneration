package me.stephenminer.oreregeneration.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Items {
    public ItemStack wand(){
        ItemStack item = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Region wand");
        List<String> lore = new ArrayList<>();
        lore.add("region-wand");
        meta.setLore(lore);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack dynamicwand(){
        ItemStack item = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("dynamic-region wand");
        List<String> lore = new ArrayList<>();
        lore.add("region-dynamicwand");
        meta.setLore(lore);
        item.setItemMeta(meta);
        meta.setUnbreakable(true);
        return item;
    }
    public ItemStack GeneratorRemover(){
        ItemStack item = new ItemStack(Material.GOLDEN_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Node Removal");
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack dropOnPlayer(boolean drop){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Drop Item on Player");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "If enabled, dropped items");
        lore.add(ChatColor.ITALIC + "will drop on the player");
        lore.add(ChatColor.GOLD + "Currently set to " + drop);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack dropInInventory(boolean drop){
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Drop Item in Inventory");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "If enabled, dropped items");
        lore.add(ChatColor.ITALIC + "will be put in the player's");
        lore.add(ChatColor.ITALIC + "inventory");
        lore.add(ChatColor.GOLD + "Currently set to " + drop);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack givePlayerXp(boolean give){
        ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Give Player Xp Directly");
        List<String> lore = new ArrayList<>();;
        lore.add(ChatColor.ITALIC + "If enabled, xp from broken blocks");
        lore.add(ChatColor.ITALIC + "will be give directly to the player");
        lore.add(ChatColor.GOLD + "Currently set to " + give);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack canBreak(String s){
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Can Break");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Defines which blocks");
        lore.add(ChatColor.ITALIC + "can be broken in");
        lore.add(ChatColor.ITALIC + s);
        lore.add(ChatColor.GRAY + "Click Me!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack canPvp(String s, boolean b){
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Can Pvp");
        List<String> lore = new ArrayList<>();
        String string = "on";
        if (!b)
            string = "off";
        lore.add(ChatColor.BOLD + "" + ChatColor.GOLD + "PVP is currently " + string);
        lore.add(ChatColor.ITALIC + "Defines whether you");
        lore.add(ChatColor.ITALIC + "can be pvp in");
        lore.add(ChatColor.ITALIC + s + " or not");
        lore.add(ChatColor.GRAY + "Click Me!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack replenishAs(String s){
        ItemStack item = new ItemStack(Material.DIAMOND_SHOVEL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Replenish As");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "(if left blank it will");
        lore.add(ChatColor.ITALIC + "use the material of the");
        lore.add(ChatColor.ITALIC + "broken block)");
        lore.add(ChatColor.ITALIC + "Defines what material");
        lore.add(ChatColor.ITALIC + "the broken block will");
        lore.add(ChatColor.ITALIC + "regenerate as.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack canSpawn(String s, boolean b){
        ItemStack item = new ItemStack(Material.CHICKEN_SPAWN_EGG);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Can mobs spawn?");
        List<String> lore = new ArrayList<>();
        String string = "can";
        if (!b)
            string = "can't";
        lore.add(ChatColor.BOLD + "" + ChatColor.GOLD + "Mobs " +
                "" + string + " currently spawn");
        lore.add(ChatColor.ITALIC + "Defines whether mobs");
        lore.add(ChatColor.ITALIC + "can spawn in ");
        lore.add(ChatColor.ITALIC + s + " or not");
        lore.add(ChatColor.GRAY + "Click Me!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack replaceWith(String s, Material mat){
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Replace With");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Defines what block");
        lore.add(ChatColor.ITALIC + "broken blocks in");
        lore.add(ChatColor.ITALIC + s + " will be replaced");
        lore.add(ChatColor.ITALIC + "with");
        lore.add(ChatColor.GRAY + "Click Me!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack replenishTime(){
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Replenish Time");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Defines how long");
        lore.add(ChatColor.ITALIC + "it will take for");
        lore.add(ChatColor.ITALIC + "broken blocks to");
        lore.add(ChatColor.ITALIC + "regenerate (in ticks)");
        lore.add(ChatColor.GRAY + "Click Me!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack replaceOn(){
        ItemStack item = new ItemStack(Material.DIAMOND_SHOVEL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Replace On");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.ITALIC + " Defines what blocks");
        lore.add(ChatColor.ITALIC + "the block in question");
        lore.add(ChatColor.ITALIC + "can regenerate on");
        lore.add(ChatColor.GRAY + "Click Me!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack setReplenishRadius(Material mat){
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Set Replenish Radius");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.ITALIC + "Defines radius");
        lore.add(ChatColor.ITALIC + "of blocks in which");
        lore.add(ChatColor.ITALIC + "the broken block in question");
        lore.add(ChatColor.ITALIC + "will try to regenerate in");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack currentMaxBlocks(int i){
        ItemStack item = new ItemStack(Material.SCAFFOLDING);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + i + " radius ");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.ITALIC + "Radius is currently");
        lore.add(ChatColor.ITALIC + "" + i);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack currentTime(int i){
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "" + i/20 + " seconds");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.ITALIC + "Replenish time currently");
        lore.add(ChatColor.ITALIC + "at " + i/20 + " seconds");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack addTime(int i){
        ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Add " + i + " seconds");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to add " + i + "seconds");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack addBlocks(int i){
        ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Add " + i + " block(s)");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to add " + i + "block(s)");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack back(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Go back");
        item.setItemMeta(meta);
        return item;
    }


    public boolean isItem(String key, ItemStack item){
        if (item.hasItemMeta())
            if (item.getItemMeta().hasLore()){
                List<String> lore = item.getItemMeta().getLore();
                for (String str : lore) {
                    if (key.equals("region-wand")) {
                        if (str.equalsIgnoreCase(key)) return true;
                    }
                    if (key.equals("region-dynamicwand")) {
                        if (str.equalsIgnoreCase(key)) return true;
                    }
                }
            }
        return false;
    }

}
