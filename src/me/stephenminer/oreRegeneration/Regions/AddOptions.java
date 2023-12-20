package me.stephenminer.oreRegeneration.Regions;

import me.stephenminer.oreRegeneration.Items.Items;
import me.stephenminer.oreRegeneration.OreRegeneration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AddOptions implements Listener{

    private OreRegeneration plugin;
    public AddOptions(OreRegeneration plugin){
        this.plugin = plugin;
    }

    public static HashMap<UUID, String> string = new HashMap<UUID, String>();

    public HashMap<UUID, Material> matMap = new HashMap<UUID, Material>();


    @EventHandler
    public void editMenu(InventoryClickEvent event){
        Items i = new Items();
        Player p = (Player) event.getWhoClicked();
        String s = event.getView().getTitle();
        if (s.contains("Dynamic") || s.contains("dynamic") || s.contains("[dynamic]"))
            return;
        if (event.getView().getTitle().contains("Region "  + string.get(p.getUniqueId()))){
            event.setCancelled(true);
            if (event.getCurrentItem() == null)
                return;
            if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                p.closeInventory();
                string.remove(p.getUniqueId());
            }
            EditRegion er = new EditRegion(plugin, string.get(p.getUniqueId()));
            if (event.getCurrentItem().getType().equals(Material.DIAMOND_PICKAXE)){
                p.openInventory(er.canBreakMenu());
                return;
            }
            if (event.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)){
                er.addPvp(p, !plugin.RegionStorageFile.getConfig().getBoolean("regions." + string.get(p.getUniqueId()) + ".pvp"));
                event.getView().getTopInventory().setItem(3, i.canPvp(string.get(p.getUniqueId()), plugin.RegionStorageFile.getConfig().getBoolean("regions." + string.get(p.getUniqueId()) + ".pvp")));
                return;
            }
            if (event.getCurrentItem().getType().equals(Material.CHICKEN_SPAWN_EGG)){
                addMobSpawn(p, string.get(p.getUniqueId()), !plugin.RegionStorageFile.getConfig().getBoolean("regions." + string.get(p.getUniqueId()) + ".mobs-spawn"));
                event.getView().getTopInventory().setItem(2, i.canSpawn(string.get(p.getUniqueId()), plugin.RegionStorageFile.getConfig().getBoolean("regions." + string.get(p.getUniqueId()) + ".mobs-spawn")));
            }
            if (event.getCurrentItem().getType().equals(Material.HOPPER)){
                er.setDropOnPlayer(!plugin.RegionStorageFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".dropOnPlayer"));
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        event.getView().getTopInventory().setItem(4, i.dropOnPlayer(plugin.RegionStorageFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".dropOnPlayer")));
                    }
                }.runTaskLater(plugin, 2);
            }
            if (event.getCurrentItem().getType().equals(Material.CHEST)){
                er.setDropInInventory(!plugin.RegionStorageFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".dropInInventory"));
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        event.getView().getTopInventory().setItem(1, i.dropInInventory(plugin.RegionStorageFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".dropInInventory")));
                    }
                }.runTaskLater(plugin, 2);
            }
            if (event.getCurrentItem().getType().equals(Material.EXPERIENCE_BOTTLE)){
                er.setGivePlayerXp(!plugin.RegionStorageFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".givePlayerXp"));
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        event.getView().getTopInventory().setItem(6, i.givePlayerXp(plugin.RegionStorageFile.getConfig().getBoolean("regions." + AddOptions.string.get(p.getUniqueId()) + ".givePlayerXp")));
                    }
                }.runTaskLater(plugin, 2);
            }

        }
    }
    @EventHandler
    public void canBreakMenu(InventoryClickEvent event){
        Player p = (Player) event.getWhoClicked();
        String a = event.getView().getTitle();
        if (a.contains("Dynamic") || a.contains("dynamic") || a.contains("[dynamic]"))
            return;
        if (event.getView().getTitle().contains("Can Break in " + string.get(p.getUniqueId()))){
            if (event.getCurrentItem() == null)
                return;
            if (event.getClickedInventory().getType().equals(InventoryType.PLAYER))
                return;
            EditRegion er = new EditRegion(plugin, string.get(p.getUniqueId()));
            Material mat = event.getCurrentItem().getType();
            /*
            if (!event.getCurrentItem().getType().isBlock())
                if (!(mat.equals(Material.CARROT) || mat.equals(Material.POTATO) || mat.equals(Material.BEETROOT_SEEDS) || mat.equals(Material.COCOA_BEANS)))
                    return;

             */
            if (event.getSlot() >= 45){
                event.setCancelled(true);
                if (mat.equals(Material.BARRIER)){
                    p.openInventory(er.editMenu());
                }
                return;
            }
            if (event.isRightClick()){
                p.openInventory(er.deleteOrReplaceWithmenu(event.getCurrentItem().getType()));
                matMap.put(p.getUniqueId(), event.getCurrentItem().getType());

            }
        }
        if (event.getView().getTitle().contains("Delete or add options")){
            event.setCancelled(true);
            if (event.getCurrentItem() == null)
                return;
            if (event.getClickedInventory().getType().equals(InventoryType.PLAYER))
                return;
            EditRegion er = new EditRegion(plugin, string.get(p.getUniqueId()));
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
            if (event.getCurrentItem().getType().equals(Material.DIAMOND_SHOVEL)){
                p.openInventory(er.replenishAs(matMap.get(p.getUniqueId())));
            }
            if (event.getCurrentItem().getItemMeta() != null){
                if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Replace With")) {
                    p.openInventory(er.replaceWithMenu(event.getCurrentItem().getType()));
                }
            }
        }
        if (event.getView().getTitle().contains("Will be replaced by:")){
            if (event.getCurrentItem() == null)
                return;
            if (event.getClickedInventory().getType().equals(InventoryType.PLAYER))
                return;
            EditRegion er = new EditRegion(plugin, string.get(p.getUniqueId()));
            if (event.getSlot() >= 45){
                event.setCancelled(true);
                if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                    p.openInventory(er.canBreakMenu());
                }
                return;
            }
        }
        if (event.getView().getTitle().contains("Replenish As")){
            if (event.getCurrentItem() == null)
                return;
            if (event.getClickedInventory().getType().equals(InventoryType.PLAYER))
                return;
            EditRegion er = new EditRegion(plugin, string.get(p.getUniqueId()));
            if (event.getSlot() >= 45){
                event.setCancelled(true);
                if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                    p.openInventory(er.canBreakMenu());
                }
                return;
            }
        }
        if (event.getView().getTitle().contains("Replenish Time")){
            event.setCancelled(true);
            EditRegion er = new EditRegion(plugin, string.get(p.getUniqueId()));
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
    }


    public void saveMuhTime(Inventory inv, Player p, int increment){
        increment = increment * 20;
        Items items = new Items();
        int i = plugin.RegionStorageFile.getConfig().getInt("regions." + string.get(p.getUniqueId()) + ".canBreak." + matMap.get(p.getUniqueId()) + ".replenishTime");
        int toAdd = i + increment;
        if (i + increment <= 0)
            toAdd = 0;
        plugin.RegionStorageFile.getConfig().set("regions." + string.get(p.getUniqueId()) + ".canBreak." + matMap.get(p.getUniqueId()) + ".replenishTime", (toAdd));
        plugin.RegionStorageFile.saveConfig();
        int newInt = plugin.RegionStorageFile.getConfig().getInt("regions." + string.get(p.getUniqueId()) + ".canBreak." + matMap.get(p.getUniqueId()) + ".replenishTime");
        inv.setItem(4, items.currentTime(newInt ));
        p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Added " + increment / 20 + " seconds");
    }


    @EventHandler
    public void checkClose(InventoryCloseEvent event){
        Player p = (Player) event.getPlayer();
        List<ItemStack> list = new ArrayList<>();
        if (event.getView().getTitle().contains("dynamic") || event.getView().getTitle().contains("[dynamic]") || event.getView().getTitle().contains("Dynamic"))
            return;
        if (event.getView().getTitle().contains("Can Break in " + string.get(p.getUniqueId()))){
            EditRegion er = new EditRegion(plugin, string.get(p.getUniqueId()));
            for (int i = 0; i <= 44; i++) {
                if (event.getInventory().getItem(i) != null) {
                    /*
                    if (!event.getInventory().getItem(i).getType().isBlock()) {
                        Bukkit.broadcastMessage(event.getInventory().getItem(i).getType().name());
                        if (!(event.getInventory().getItem(i).getType().equals(Material.CARROT) || event.getInventory().getItem(i).getType().equals(Material.POTATO) || event.getInventory().getItem(i).getType().equals(Material.BEETROOT_SEEDS)
                        || event.getInventory().getItem(i).getType().equals(Material.COCOA_BEANS)))
                            continue;
                    }

                     */
                    list.add(event.getInventory().getItem(i));
                }
            }
            er.saveCanBreak(list.toArray(new ItemStack[0]));
        }
        if (event.getView().getTitle().contains("Replenish As")){
            EditRegion er = new EditRegion(plugin, string.get(p.getUniqueId()));
            for (int i = 0; i <= 44; i++){
                if (event.getInventory().getItem(i) != null){
                    if (!event.getInventory().getItem(i).getType().isBlock())
                        if (!(event.getInventory().getItem(i).getType().equals(Material.CARROT) || event.getInventory().getItem(i).getType().equals(Material.POTATO) || event.getInventory().getItem(i).getType().equals(Material.BEETROOT_SEEDS)))
                            continue;
                    list.add(event.getInventory().getItem(i));
                }
            }
            er.saveReplenishAs(list.toArray(new ItemStack[0]), matMap.get(p.getUniqueId()));
            matMap.remove(p.getUniqueId());
        }
        if (event.getView().getTitle().contains("Will be replaced by:")){
            EditRegion er = new EditRegion(plugin, string.get(p.getUniqueId()));
            for (int i = 0; i <= 44; i++){
                if (event.getInventory().getItem(i) != null) {
                    if (!event.getInventory().getItem(i).getType().isBlock())
                        if (!(event.getInventory().getItem(i).getType().equals(Material.CARROT) || event.getInventory().getItem(i).getType().equals(Material.POTATO) || event.getInventory().getItem(i).getType().equals(Material.BEETROOT_SEEDS)))
                            continue;
                    list.add(event.getInventory().getItem(i));
                }
            }
            if (list.isEmpty())
                list.add(new ItemStack(Material.AIR));
            er.saveReplaceWith(list.toArray(new ItemStack[0]), matMap.get(p.getUniqueId()));
            matMap.remove(p.getUniqueId());
        }
    }

    public void addMobSpawn(Player sender, String region, boolean mobspawn){
        boolean dynamic = false;
        if (plugin.DynamicRegionFile.getConfig().contains("regions." + region))
            dynamic = true;
        else if (plugin.RegionStorageFile.getConfig().contains("regions." + region))
            dynamic = false;
        if (dynamic)
            plugin.DynamicRegionFile.getConfig().set("regions." + region + ".mobs-spawn", mobspawn);
        else plugin.RegionStorageFile.getConfig().set("regions." + region + ".mobs-spawn", mobspawn);
        plugin.DynamicRegionFile.saveConfig();
        plugin.RegionStorageFile.saveConfig();
        boolean b = false;
        String s = "off";
        if (dynamic) {
            b = plugin.DynamicRegionFile.getConfig().getBoolean("regions." + region + ".mobs-spawn");
            if (b)
                s = "on";
            sender.sendMessage(ChatColor.GREEN + "Mob Spawning turned " + s);
        }else{
            b = plugin.RegionStorageFile.getConfig().getBoolean("regions." + region + ".mobs-spawn");
            if (b)
                s = "on";
            sender.sendMessage(ChatColor.GREEN + "Mob Spawning turned " + s);
        }

    }

}
