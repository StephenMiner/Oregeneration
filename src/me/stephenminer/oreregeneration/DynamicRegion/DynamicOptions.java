package me.stephenminer.oreregeneration.DynamicRegion;

import me.stephenminer.oreregeneration.Items.Items;
import me.stephenminer.oreregeneration.OreRegeneration;
import me.stephenminer.oreregeneration.Regions.AddOptions;
import me.stephenminer.oreregeneration.Regions.ChanceEditor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DynamicOptions implements Listener {

    private final OreRegeneration plugin;
    private HashMap<UUID, ChanceEditor> editors;
    public DynamicOptions(OreRegeneration plugin){
        this.plugin = plugin;
        matMap = new HashMap<>();
        editors = new HashMap<>();
    }


    public HashMap<UUID, Material> matMap;



    @EventHandler
    public void editMenu(InventoryClickEvent event){
        Items i = new Items();
        Player p = (Player) event.getWhoClicked();
        if (event.getView().getTitle().contains("Dynamic Region " + AddOptions.string.get(p.getUniqueId()))){
            AddOptions ao = new AddOptions(plugin);
            event.setCancelled(true);
            if (event.getCurrentItem() == null)
                return;
            if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                p.closeInventory();
                AddOptions.string.remove(p.getUniqueId());
            }
            EditDynamic er = new EditDynamic(plugin, AddOptions.string.get(p.getUniqueId()));
            if (event.getCurrentItem().getType().equals(Material.DIAMOND_PICKAXE)){
                p.openInventory(er.canBreakMenu());
                return;
            }
            if (event.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)){
                er.addPvp(p, !plugin.DynamicRegionFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".pvp"));
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        event.getView().getTopInventory().setItem(3, i.canPvp(AddOptions.string.get(p.getUniqueId()), plugin.DynamicRegionFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".pvp")));
                    }
                }.runTaskLater(plugin, 1);

                return;
            }
            if (event.getCurrentItem().getType().equals(Material.CHICKEN_SPAWN_EGG)){
                ao.addMobSpawn(p, AddOptions.string.get(p.getUniqueId()), !plugin.DynamicRegionFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".mobs-spawn"));
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        event.getView().getTopInventory().setItem(2, i.canSpawn(AddOptions.string.get(p.getUniqueId()), plugin.DynamicRegionFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".mobs-spawn")));
                    }
                }.runTaskLater(plugin, 1);
            }
            if (event.getCurrentItem().getType().equals(Material.HOPPER)){
                er.setDropOnPlayer(!plugin.DynamicRegionFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".dropOnPlayer"));
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        event.getView().getTopInventory().setItem(4, i.dropOnPlayer(plugin.DynamicRegionFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".dropOnPlayer")));
                    }
                }.runTaskLater(plugin, 2);
            }
            if (event.getCurrentItem().getType().equals(Material.CHEST)){
                er.setDropInInventory(!plugin.DynamicRegionFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".dropInInventory"));
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        event.getView().getTopInventory().setItem(1, i.dropInInventory(plugin.DynamicRegionFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".dropInInventory")));
                    }
                }.runTaskLater(plugin, 2);
            }
            if (event.getCurrentItem().getType().equals(Material.EXPERIENCE_BOTTLE)){
                er.setGivePlayerXp(!plugin.DynamicRegionFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".givePlayerXp"));
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        event.getView().getTopInventory().setItem(6, i.givePlayerXp(plugin.DynamicRegionFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".givePlayerXp")));
                    }
                }.runTaskLater(plugin, 2);
            }
        }
    }
    @EventHandler
    public void canBreakMenu(InventoryClickEvent event){
        Player p = (Player) event.getWhoClicked();
        if (event.getView().getTitle().contains("Can Break in dynamic " + AddOptions.string.get(p.getUniqueId()))){
            if (event.getCurrentItem() == null)
                return;
            if (event.getClickedInventory().getType().equals(InventoryType.PLAYER))
                return;
            EditDynamic er = new EditDynamic(plugin, AddOptions.string.get(p.getUniqueId()));
            /*
            if (!event.getCurrentItem().getType().isBlock() && !cropItems().contains(event.getCurrentItem().getType()))
                return;
                
             */
            if (event.getSlot() >= 45){
                event.setCancelled(true);
                if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                    p.openInventory(er.editMenu());
                }
                return;
            }
            if (event.isRightClick()){
                p.openInventory(er.deleteOrReplaceWithmenu(event.getCurrentItem().getType()));
                matMap.put(p.getUniqueId(), event.getCurrentItem().getType());

            }
        }
        if (event.getView().getTitle().contains("[dynamic] Delete or add options")){
            event.setCancelled(true);
            if (event.getCurrentItem() == null)
                return;
            if (event.getClickedInventory().getType().equals(InventoryType.PLAYER))
                return;
            EditDynamic er = new EditDynamic(plugin, AddOptions.string.get(p.getUniqueId()));
            if (event.getSlot() == 8){
                if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                    p.openInventory(er.canBreakMenu());
                }
                return;
            }
            if (event.getCurrentItem().getType().equals(Material.CLOCK)){
                p.openInventory(er.replenishTimeMenu(p, matMap.get(p.getUniqueId())));
                return;
            }
            if (event.getCurrentItem().getItemMeta() != null){
                if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Replace With")) {
                    p.openInventory(er.replaceWithMenu(event.getCurrentItem().getType()));
                    return;
                }
            }
            if (event.getCurrentItem().getItemMeta() != null){
                if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Replenish Radius")) {
                    p.openInventory(er.replenishRadius(p, event.getCurrentItem().getType()));
                    return;
                }
            }
            if (event.getCurrentItem().getType().equals(Material.DIAMOND_SHOVEL)){
                p.openInventory(er.replaceOnMenu(matMap.get(p.getUniqueId())));
                return;
            }
        }
        if (event.getView().getTitle().contains("Will be replaced by:") && event.getView().getTitle().contains("[dynamic]")){
            if (event.getCurrentItem() == null)
                return;
            if (event.getClickedInventory().getType().equals(InventoryType.PLAYER))
                return;
            EditDynamic er = new EditDynamic(plugin, AddOptions.string.get(p.getUniqueId()));
            if (event.getSlot() >= 45){
                event.setCancelled(true);
                if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                    p.openInventory(er.canBreakMenu());
                }
                return;
            }
            if (event.isRightClick()){
                ChanceEditor editor = new ChanceEditor(AddOptions.string.get(p.getUniqueId()),true, ChanceEditor.Type.REPLACE_WITH, matMap.get(p.getUniqueId()), event.getCurrentItem().getType());
                editors.put(p.getUniqueId(),editor);
                p.sendMessage(ChatColor.GOLD + "Type out a weight for material " + event.getCurrentItem().getType().name() + " in the replaceWith section");
                p.closeInventory();
            }
        }
        if (event.getView().getTitle().contains("[dynamic] Replenish Time")){
            event.setCancelled(true);
            EditDynamic er = new EditDynamic(plugin, AddOptions.string.get(p.getUniqueId()));
            if (event.getCurrentItem() == null)
                return;
            if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                p.openInventory(er.canBreakMenu());
                return;
            }
            if (event.getCurrentItem().getType().equals(Material.EXPERIENCE_BOTTLE)){
                if (event.getCurrentItem().getItemMeta() == null)
                    return;
                String s = event.getCurrentItem().getItemMeta().getDisplayName();
                if (s.contains("-10")) {
                    saveMuhTime(event.getClickedInventory(), p, -10);
                    return;
                }
                if (s.contains("-1")) {
                    saveMuhTime(event.getClickedInventory(), p, -1);
                    return;
                }
                if (s.contains("-5")) {
                    saveMuhTime(event.getClickedInventory(), p, -5);
                    return;
                }

                if (s.contains("10")) {
                    saveMuhTime(event.getClickedInventory(), p, 10);
                    return;
                }
                if (s.contains("1")) {
                    saveMuhTime(event.getClickedInventory(), p, 1);
                    return;
                }
                if (s.contains("5")) {
                    saveMuhTime(event.getClickedInventory(), p, 5);
                    return;
                }




            }
        }


        if (event.getView().getTitle().contains("Replenish Radius")){
            event.setCancelled(true);
            if (event.getCurrentItem().getItemMeta() == null)
                return;
            EditDynamic er = new EditDynamic(plugin,AddOptions.string.get(p.getUniqueId()));
            String s = event.getCurrentItem().getItemMeta().getDisplayName();
            if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                p.openInventory(er.canBreakMenu());
                return;
            }
            if (s.contains("-10")) {
                saveMuhMaxBlocks(event.getClickedInventory(), p, -10);
                return;
            }
            if (s.contains("-1")) {
                saveMuhMaxBlocks(event.getClickedInventory(), p, -1);
                return;
            }
            if (s.contains("-5")) {
                saveMuhMaxBlocks(event.getClickedInventory(), p, -5);
                return;
            }

            if (s.contains("10")) {
                saveMuhMaxBlocks(event.getClickedInventory(), p, 10);
                return;
            }
            if (s.contains("1")) {
                saveMuhMaxBlocks(event.getClickedInventory(), p, 1);
                return;
            }
            if (s.contains("5")) {
                saveMuhMaxBlocks(event.getClickedInventory(), p, 5);
                return;
            }
        }

        if (event.getView().getTitle().contains("Will be replaced on:")){
            if (event.getCurrentItem() == null)
                return;
            if (event.getClickedInventory().getType().equals(InventoryType.PLAYER))
                return;
            EditDynamic er = new EditDynamic(plugin, AddOptions.string.get(p.getUniqueId()));
            if (event.getSlot() >= 45){
                event.setCancelled(true);
                if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                    p.openInventory(er.canBreakMenu());
                }
                return;
            }
        }
    }




    public void saveMuhTime(Inventory inv, Player p, int increment){
        increment = increment * 20;
        Items items = new Items();
        int i = plugin.DynamicRegionFile.getConfig().getInt("regions." + AddOptions.string.get(p.getUniqueId()) + ".canBreak." + matMap.get(p.getUniqueId()) + ".replenishTime");
        int toAdd = i + increment;
        if (i + increment <= 0)
            toAdd = 0;
        plugin.DynamicRegionFile.getConfig().set("regions." + AddOptions.string.get(p.getUniqueId()) + ".canBreak." + matMap.get(p.getUniqueId()) + ".replenishTime", (toAdd));
        plugin.DynamicRegionFile.saveConfig();
        int newInt = plugin.DynamicRegionFile.getConfig().getInt("regions." + AddOptions.string.get(p.getUniqueId()) + ".canBreak." + matMap.get(p.getUniqueId()) + ".replenishTime");
        inv.setItem(4, items.currentTime(newInt ));
        p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Added " + increment / 20 + " seconds");
    }
    public void saveMuhMaxBlocks(Inventory inv, Player p, int increment){ ;
        Items items = new Items();
        int i = plugin.DynamicRegionFile.getConfig().getInt("regions." + AddOptions.string.get(p.getUniqueId()) + ".canBreak." + matMap.get(p.getUniqueId()) + ".replenishRadius");
        int toAdd = i + increment;
        if (i + increment <= 0)
            toAdd = 0;
        plugin.DynamicRegionFile.getConfig().set("regions." + AddOptions.string.get(p.getUniqueId()) + ".canBreak." + matMap.get(p.getUniqueId()) + ".replenishRadius", (toAdd));
        plugin.DynamicRegionFile.saveConfig();
        int newInt = plugin.DynamicRegionFile.getConfig().getInt("regions." + AddOptions.string.get(p.getUniqueId()) + ".canBreak." + matMap.get(p.getUniqueId()) + ".replenishRadius");
        inv.setItem(4, items.currentMaxBlocks(newInt));
        p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Replenish radius increased by " + increment);
    }

    @EventHandler
    public void checkClose(InventoryCloseEvent event){
        Player p = (Player) event.getPlayer();
        List<ItemStack> list = new ArrayList<>();
        if (event.getView().getTitle().contains("Can Break in dynamic " + AddOptions.string.get(p.getUniqueId()))){
            EditDynamic er = new EditDynamic(plugin, AddOptions.string.get(p.getUniqueId()));
            for (int i = 0; i <= 44; i++){
                if (event.getInventory().getItem(i) != null) {
/*
                    if (!event.getInventory().getItem(i).getType().isBlock()){
                        Material mat = event.getInventory().getItem(i).getType();
                        if (!(mat.equals(Material.CARROT) || mat.equals(Material.POTATO) || mat.equals(Material.BEETROOT_SEEDS)
                        || mat.equals(Material.COCOA_BEANS)))
                            continue;
                    }

 */
                    list.add(event.getInventory().getItem(i));
                }
            }
            er.saveCanBreak(list.toArray(new ItemStack[0]));
        }
        if (event.getView().getTitle().contains("Will be replaced by:") && event.getView().getTitle().contains("[dynamic]")){
            EditDynamic er = new EditDynamic(plugin, AddOptions.string.get(p.getUniqueId()));
            for (int i = 0; i <= 44; i++){
                if (event.getInventory().getItem(i) != null) {
                    if (!event.getInventory().getItem(i).getType().isBlock())
                        continue;
                    list.add(event.getInventory().getItem(i));
                }
            }
            er.saveReplaceWith(list.toArray(new ItemStack[0]), matMap.get(p.getUniqueId()));
            if (!editors.containsKey(p.getUniqueId())) matMap.remove(p.getUniqueId());
        }
        if (event.getView().getTitle().contains("Will be replaced on:")){
            EditDynamic er = new EditDynamic(plugin, AddOptions.string.get(p.getUniqueId()));
            for (int i = 0; i <= 44; i++){
                if (event.getInventory().getItem(i) != null) {
                    if (!event.getInventory().getItem(i).getType().isBlock())
                        continue;
                    list.add(event.getInventory().getItem(i));
                }
            }
            er.saveReplaceOn(list.toArray(new ItemStack[0]), matMap.get(p.getUniqueId()));
            matMap.remove(p.getUniqueId());
        }

    }

    @EventHandler
    public void sayWeight(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        if (!editors.containsKey(player.getUniqueId())) return;
        String msg = ChatColor.stripColor(event.getMessage());
        int weight;
        try {
            weight = Integer.parseInt(msg);
            ChanceEditor editor = editors.get(player.getUniqueId());
            editor.writeWeight(weight);
            EditDynamic editRegion = new EditDynamic(plugin,AddOptions.string.get(player.getUniqueId()));
            Bukkit.getScheduler().runTaskLater(plugin,()->{
                if (editor.type() == ChanceEditor.Type.REPLACE_WITH) player.openInventory(editRegion.replaceWithMenu(editor.key()));
            },1);
            editors.remove(player.getUniqueId());
            plugin.DynamicRegionFile.saveConfig();
        }catch (Exception e){
            player.sendMessage(ChatColor.RED + "You need to type a whole number integer for the weight!");
        }
    }

    private List<Material> cropItems(){
        List<Material> crops = new ArrayList<>();
        crops.add(Material.CARROT);
        crops.add(Material.POTATO);
        crops.add(Material.WHEAT);
        crops.add(Material.BEETROOT_SEEDS);
        crops.add(Material.COCOA_BEANS);
        return crops;
    }




}
