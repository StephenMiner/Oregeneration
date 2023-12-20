package me.stephenminer.oreRegeneration.Events;

import me.stephenminer.oreRegeneration.DynamicRegion.Create;
import me.stephenminer.oreRegeneration.Items.Items;
import me.stephenminer.oreRegeneration.OreRegeneration;
import me.stephenminer.oreRegeneration.Regions.CreateRegion;
import me.stephenminer.oreRegeneration.Regions.DynamicRegion;
import me.stephenminer.oreRegeneration.Regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Interact implements Listener {
    private OreRegeneration plugin;
    public Interact(OreRegeneration plugin){
        this.plugin = plugin;
    }

    public static HashMap<Location, Material> tempMap = new HashMap<Location, Material>();

    HashMap<UUID, Location> loc1 = new HashMap<UUID, Location>();
    HashMap<UUID,Location> loc2 = new HashMap<UUID, Location>();
    HashMap<UUID, Boolean> canName = new HashMap<UUID, Boolean>();
    HashMap<UUID, Location> dloc1 = new HashMap<UUID, Location>();
    HashMap<UUID,Location> dloc2 = new HashMap<UUID, Location>();
    HashMap<UUID, Boolean> dcanName = new HashMap<UUID, Boolean>();

    @EventHandler
    public void onClick(PlayerInteractEvent event){
        HashMap<UUID, Long> lcd = new HashMap<UUID, Long>();
        HashMap<UUID, Long> rcd = new HashMap<UUID, Long>();

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        Items i = new Items();
        if (i.isItem("region-wand", hand)){
            if (event.hasBlock()) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (lcd.containsKey(player.getUniqueId()))
                        if (lcd.get(player.getUniqueId()) > System.currentTimeMillis())
                            return;
                    loc1.put(player.getUniqueId(), event.getClickedBlock().getLocation());
                    player.sendMessage("Location #1 set");
                    lcd.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
                    event.setCancelled(true);
                }
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (rcd.containsKey(player.getUniqueId()))
                        if (rcd.get(player.getUniqueId()) > System.currentTimeMillis())
                            return;
                    loc2.put(player.getUniqueId(), event.getClickedBlock().getLocation());
                    player.sendMessage("Location #2 set");
                    rcd.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
                    event.setCancelled(true);
                }
            }
            if (event.getAction() == Action.LEFT_CLICK_AIR){
                loc1.put(player.getUniqueId(), player.getLocation());
                player.sendMessage("Location #1 set");
            }
            if (event.getAction() == Action.RIGHT_CLICK_AIR){
                loc2.put(player.getUniqueId(), player.getLocation());
                player.sendMessage("Location #2 set");
            }
            if (loc1.containsKey(player.getUniqueId())){
                if (loc2.containsKey(player.getUniqueId())){
                    canName.put(player.getUniqueId(), true);
                    player.sendMessage(ChatColor.GOLD + "Please type out the name of your region in chat.");
                }
            }
        }
        if (i.isItem("region-dynamicwand", hand)){
            if (event.hasBlock()) {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (lcd.containsKey(player.getUniqueId()))
                        if (lcd.get(player.getUniqueId()) > System.currentTimeMillis())
                            return;
                    dloc1.put(player.getUniqueId(), event.getClickedBlock().getLocation());
                    player.sendMessage("Location #1 set");
                    lcd.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
                    event.setCancelled(true);
                }
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (rcd.containsKey(player.getUniqueId()))
                        if (rcd.get(player.getUniqueId()) > System.currentTimeMillis())
                            return;
                    dloc2.put(player.getUniqueId(), event.getClickedBlock().getLocation());
                    player.sendMessage("Location #2 set");
                    rcd.put(player.getUniqueId(), System.currentTimeMillis() + 1000);
                    event.setCancelled(true);
                }
            }
            if (event.getAction() == Action.LEFT_CLICK_AIR){
                dloc1.put(player.getUniqueId(), player.getLocation());
                player.sendMessage("Location #1 set");
            }
            if (event.getAction() == Action.RIGHT_CLICK_AIR){
                dloc2.put(player.getUniqueId(), player.getLocation());
                player.sendMessage("Location #2 set");
            }
            if (dloc1.containsKey(player.getUniqueId())){
                if (dloc2.containsKey(player.getUniqueId())){
                    dcanName.put(player.getUniqueId(), true);
                    player.sendMessage(ChatColor.GOLD + "Please type out the name of your region in chat.");
                }
            }
        }
    }
    Random r = new Random();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        String name = ChatColor.stripColor(event.getMessage());
        Player player = event.getPlayer();
        if (canName.containsKey(player.getUniqueId())){
            if (canName.get(player.getUniqueId())) {
                Location location1 = loc1.get(player.getUniqueId());
                Location location2 = loc2.get(player.getUniqueId());
                CreateRegion cr = new CreateRegion(plugin);
                cr.setLoc1(location1);
                cr.setName(name);
                cr.setLoc2(location2);
                cr.createRegion();
                canName.put(player.getUniqueId(),false);
                loc1.remove(player.getUniqueId());
                loc2.remove(player.getUniqueId());
                final String fName = name.replace(' ','_');
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.GOLD +"Created Region "+fName +"!");
                    }
                }.runTaskLater(plugin, 1);
            }
        }
        if (dcanName.containsKey(player.getUniqueId())){
            if (dcanName.get(player.getUniqueId())) {
                Location location1 = dloc1.get(player.getUniqueId());
                Location location2 = dloc2.get(player.getUniqueId());
                Create cr = new Create(plugin);
                cr.setLoc1(location1);
                cr.setName(name);
                cr.setLoc2(location2);
                cr.createRegion();
                dcanName.put(player.getUniqueId(),false);
                dloc1.remove(player.getUniqueId());
                dloc2.remove(player.getUniqueId());
                final String fName = name.replace(' ','_');
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.GOLD +"Created Region "+fName +"!");
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }

    public static HashMap<String, Boolean> editMode = new HashMap<String, Boolean>();

    private Material translator(Material input){
        if (input.equals(Material.CARROT))
            return Material.CARROTS;
        if (input.equals(Material.POTATO))
            return Material.POTATOES;
        if (input.equals(Material.BEETROOT_SEEDS))
            return Material.BEETROOTS;
        return Material.CARROTS;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event){
        /*
        Material replaceWith = Material.AIR;
        int replenishTime = 100;
        CreateRegion cr = new CreateRegion(plugin);

         */
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
        if (plugin.NodeFile.getConfig().contains("Nodes." + OreRegeneration.getBlockKey(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())  + ".Block"))
            return;
        if (!plugin.RegionStorageFile.getConfig().contains("regions"))
            return;
        for (Region region : plugin.regions){
            if (region.isInRegion(block) && !region.editMode(player)){
                event.setCancelled(true);
                if (region.tryBreakEvent(player, block)) {
                    event.setDropItems(false);
                    event.setCancelled(false);
                    if (region.givePlayerXp()){
                        player.giveExp(event.getExpToDrop());
                        event.setExpToDrop(0);
                    }
                    Sound sound = region.breakSound();
                    if (sound != null) player.playSound(player.getLocation(), sound, region.soundVol(), region.soundPitch());
                }else{
                    event.setCancelled(true);
                    String msg = "";
                    if (region.denyBreak() != null)
                        msg = ChatColor.translateAlternateColorCodes('&', region.denyBreak());
                    else msg = ChatColor.RED + "You cannot break this block in " + region.getId();
                    if (!msg.isEmpty()) 
                        player.sendMessage(msg);
                }
            }
        }
        /*
        for (String key : plugin.RegionStorageFile.getConfig().getConfigurationSection("regions.").getKeys(false)){
            Location l1 = (Location) plugin.RegionStorageFile.getConfig().get("regions." +key + ".loc1");
            Location l2 = (Location) plugin.RegionStorageFile.getConfig().get("regions." +key + ".loc2");
            if (l2 == null)
                continue;
            if (l1 == null)
                continue;
            Location loc1 = l1.clone().add(0.5, 0.5, 0.5);
            Location loc2 = l2.clone().add(0.5, 0.5, 0.5);
            BoundingBox bb = null;
            if (loc2 != null) {
                if (loc1 != null) {
                    bb = BoundingBox.of(loc1, loc2);
                }
            }
            if (bb != null && bb.overlaps(block.getBoundingBox())) {
                if (!loc.getWorld().getName().equalsIgnoreCase(loc1.getWorld().getName()))
                    continue;
                if (player.hasPermission("oreGen.regions.editor")) {
                    if (editMode.containsKey(key))
                        if (editMode.get(key)) {
                            tempMap.remove(loc);
                            return;
                        }
                }
                if (plugin.RegionStorageFile.getConfig().contains("regions." + key + ".canBreak")) {
                    if (block.hasMetadata("isGen")) {
                        if (plugin.RegionStorageFile.getConfig().contains("regions." + key + ".canBreak." + loc.getBlock().getType().toString())){
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    loc.getBlock().setType(Material.BEDROCK, false);
                                }
                            }.runTaskLater(plugin, 3);
                            return;
                        }
                    }
                    event.setCancelled(false);
                    for (String s : plugin.RegionStorageFile.getConfig().getConfigurationSection("regions." + key + ".canBreak").getKeys(false)) {
                        Material mat = Material.matchMaterial(s);
                        if (s.equalsIgnoreCase(Material.CARROT.name()) || s.equalsIgnoreCase(Material.POTATO.name()) || s.equalsIgnoreCase(Material.BEETROOT_SEEDS.name()))
                            mat = translator(mat);
                        if (block.getType().equals(mat) ) {
                            event.setCancelled(false);
                            if (plugin.RegionStorageFile.getConfig().contains("regions." + key + ".canBreak." + s + ".replenishTime"))
                                replenishTime = plugin.RegionStorageFile.getConfig().getInt("regions." + key + ".canBreak." + s + ".replenishTime");
                            if (plugin.RegionStorageFile.getConfig().contains("regions." + key + ".canBreak." + s + ".replaceWith")) {
                                // replaceWith = Material.matchMaterial(plugin.RegionStorageFile.getConfig().getString("regions." + key + ".canBreak." + s + ".replaceWith"));
                                List<Material> items = new ArrayList<Material>();
                                Material item = null;
                                int position = 0;
                                for (String i : plugin.RegionStorageFile.getConfig().getStringList("regions." + key + ".canBreak." + s + ".replaceWith")) {
                                    try {
                                        item = Material.matchMaterial(i);
                                    } catch (Exception e) {
                                        item = null;
                                    }
                                    items.add(position, item);
                                    position++;
                                }
                                if (items.size() != 0) {
                                    int num = r.nextInt(items.size());
                                    replaceWith = items.get(num);
                                }
                            }
                            if (replaceWith.equals(Material.CARROT))
                                replaceWith = Material.CARROTS;
                            if (replaceWith.equals(Material.POTATO))
                                replaceWith = Material.POTATOES;
                            if (replaceWith.equals(Material.BEETROOT_SEEDS))
                                replaceWith = Material.BEETROOTS;

                            Material finalReplaceWith = replaceWith;
                            new BukkitRunnable() {
                                final Material m = finalReplaceWith;

                                @Override
                                public void run() {
                                    if (!m.equals(Material.AIR)) {
                                        loc.getBlock().setType(finalReplaceWith);
                                        loc.getBlock().setMetadata("isGen", new FixedMetadataValue(plugin,
                                                loc.getWorld().getName() + "/" + loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ()));
                                    }

                                }
                            }.runTaskLater(plugin, 3);
                            tempMap.put(loc, mat);
                            List<Material> replaceAs = new ArrayList<>();
                            if (plugin.RegionStorageFile.getConfig().contains("regions." + key + ".canBreak." + s + ".replenishAs")) {
                                for (String i : plugin.RegionStorageFile.getConfig().getStringList("regions." + key + ".canBreak." + s + ".replenishAs")) {
                                    Material m = Material.matchMaterial(i);
                                    if (m != null)
                                        replaceAs.add(m);
                                }
                            }
                            Material replaceMaterial = mat;
                            if (plugin.RegionStorageFile.getConfig().contains("regions." + key + ".canBreak." + s + ".replenishAs"))
                                if(replaceAs.size() > 0)
                                    replaceMaterial = replaceAs.get(r.nextInt(replaceAs.size()));
                            if (replaceMaterial == null)
                                replaceMaterial = mat;
                            Material finalReplaceMaterial = replaceMaterial;
                            new BukkitRunnable() {
                                Block eventBlock = block;
                                Location eventLoc = loc;
                                @Override
                                public void run() {
                                    eventLoc.getBlock().setType(finalReplaceMaterial);
                                    if (eventBlock.getBlockData() instanceof Ageable){
                                        Ageable age = (Ageable) eventBlock.getBlockData();
                                        age.setAge(age.getMaximumAge());
                                        eventBlock.setBlockData(age);
                                    }
                                    eventBlock.getWorld().playSound(eventLoc, Sound.BLOCK_STONE_BREAK, 4, 1);
                                     tempMap.remove(loc);
                                    eventLoc.getBlock().removeMetadata("isGen", plugin);
                                }
                            }.runTaskLater(plugin, replenishTime);
                            return;
                        }
                    }
                }
                player.sendMessage(ChatColor.RED + "You cannot break this block in " + key);
                event.setCancelled(true);
            }
        }

         */
    }

    @EventHandler (priority = EventPriority.LOW)
    public void cancelInteraction(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (!plugin.RegionStorageFile.getConfig().contains("regions"))
            return;
        if (event.hasItem() && (event.getItem().getType().isBlock() || event.getItem().getType().isEdible())) return;
        if (event.hasBlock()) {
            Block block = event.getClickedBlock();
            if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL))
                return;
            if (block == null)
                return;
            boolean exit = false;
            switch(block.getType()){
                case CHEST:
                case TRAPPED_CHEST:
                case CRAFTING_TABLE:
                case ANVIL:
                case DAMAGED_ANVIL:
                case CHIPPED_ANVIL:
                case ENDER_CHEST:
                case FURNACE:
                case ENCHANTING_TABLE:
                    exit = true;
            }
            if (exit) return;

            for (Region region : plugin.regions) {
                if (region.isInRegion(block)) {
                    event.setCancelled(!region.editMode(player));
                    return;
                }
            }
        }
    }
    @EventHandler (priority = EventPriority.HIGHEST)
    public void cancelPlace(BlockPlaceEvent event){

        if (!plugin.RegionStorageFile.getConfig().contains("regions"))
            return;
        Player player = event.getPlayer();
        Block block = event.getBlock();
        for (Region region : plugin.regions){
            if (region.isInRegion(block)){
                event.setCancelled(!region.editMode(player));
                if (event.isCancelled()){
                    String msg = "";
                    if (region.denyPlace() != null){
                        msg = ChatColor.translateAlternateColorCodes('&', region.denyPlace());
                    }else {
                        msg = ChatColor.RED + "You cannot place blocks in region " + region.getId();
                    }
                    if (!msg.isEmpty()) player.sendMessage(msg);
                }
                return;
            }
        }
    }
/*
    public static void updateChecker(Main plugin){
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Location loc : tempMap.keySet()){
                    loc.getBlock().setType(tempMap.get(loc));
                    Block eventBlock = loc.getBlock();
                    if (eventBlock.getBlockData() instanceof Ageable){
                        Ageable age = (Ageable) eventBlock.getBlockData();
                        age.setAge(age.getMaximumAge());
                        eventBlock.setBlockData(age);
                    }
                }
                 Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Restored all blocks in regions");
            }
        }.runTaskTimer(plugin, 0, 24000);

    }

 */
    public static void resetRegions(){
        for (Location loc : tempMap.keySet()){
            loc.getBlock().setType(tempMap.get(loc));
            Block eventBlock = loc.getBlock();
            if (eventBlock.getBlockData() instanceof Ageable){
                Ageable age = (Ageable) eventBlock.getBlockData();
                age.setAge(age.getMaximumAge());
                eventBlock.setBlockData(age);
            }
        }
    }

    @EventHandler
    public void preventBooms(BlockExplodeEvent event){
        if (!plugin.RegionStorageFile.getConfig().contains("regions"))
            return;
        for (Region region : plugin.regions) {
            for (Block block : event.blockList()) {
                if (region.isInRegion(block)){
                    event.blockList().clear();
                    return;
                }
            }
        }
    }
    @EventHandler
    public void preventEBooms(EntityExplodeEvent event){
        if (!plugin.RegionStorageFile.getConfig().contains("regions"))
            return;
        for (Region region : plugin.regions) {
            for (Block block : event.blockList()) {
                if (region.isInRegion(block)){
                    event.blockList().clear();
                    return;
                }
            }
        }
    }
    @EventHandler
    public void preventBigFlow(BlockFromToEvent event){
        if (!plugin.RegionStorageFile.getConfig().contains("regions"))
            return;
        for (Region region : plugin.regions) {
            if (!region.getWorld().equals(event.getBlock().getWorld())) continue;
            if (!region.getBounds().overlaps(BoundingBox.of(event.getBlock())) && region.getBounds().overlaps(BoundingBox.of(event.getToBlock()))) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
