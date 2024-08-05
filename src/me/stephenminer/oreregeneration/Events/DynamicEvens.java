package me.stephenminer.oreregeneration.Events;

import me.stephenminer.oreregeneration.OreRegeneration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class DynamicEvens implements Listener {
    private OreRegeneration plugin;
    public DynamicEvens(OreRegeneration plugin){
        this.plugin = plugin;
    }
    public List<Block> getNearbyBlocks(Location loc, int radius){
        List<Block> blocks = new ArrayList<Block>();
        for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
            for (int y = loc.getBlockY() - radius; y <= loc.getBlockY() + radius; y++) {
                for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
                    Location location = new Location(loc.getWorld(), x, y, z);
                    blocks.add(location.getBlock());
                }
            }
        }
        return blocks;
    }
/*
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event){
        Material replaceWith = Material.AIR;
        Create c = new Create(plugin);
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material ogmat = block.getType();
        Location loc = new Location(block.getWorld(), block.getX(), block.getY(), block.getZ());
        if (plugin.NodeFile.getConfig().contains("Nodes." + OreRegeneration.getBlockKey(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())  + ".Block"))
            return;
        if (!plugin.DynamicRegionFile.getConfig().contains("regions"))
            return;
        for (String key : plugin.DynamicRegionFile.getConfig().getConfigurationSection("regions.").getKeys(false)) {
            Location l1 = (Location) plugin.DynamicRegionFile.getConfig().get("regions." + key + ".loc1");
            Location l2 = (Location) plugin.DynamicRegionFile.getConfig().get("regions." + key + ".loc2");

            BoundingBox bb = null;
            if (l2 == null)
                continue;
            if (l1 == null)
                continue;
            Location loc1 = l1.clone().add(0.5, 0.5, 0.5);
            Location loc2 = l2.clone().add(0.5, 0.5, 0.5);
            bb = BoundingBox.of(loc1, loc2);
            if (bb != null && bb.overlaps(block.getBoundingBox())) {
                if (!loc.getWorld().getName().equalsIgnoreCase(loc1.getWorld().getName()))
                    continue;
                if (player.hasPermission("oreGen.regions.editor")) {
                    if (Interact.editMode.containsKey(key))
                        if (Interact.editMode.get(key)){
                            return;
                        }
                }
                if (block.hasMetadata("isGen")) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            loc.getBlock().setType(Material.BEDROCK, false);
                        }
                    }.runTaskLater(plugin, 3);
                    return;
                }
                if (plugin.DynamicRegionFile.getConfig().contains("regions." + key + ".canBreak." + block.getType().toString())){
                    event.setCancelled(false);
                    List<Material> rW = new ArrayList<>();
                    for (String s : plugin.DynamicRegionFile.getConfig().getStringList("regions." + key + ".canBreak." + block.getType().toString() + ".replaceWith")){
                        Material m = Material.matchMaterial(s);
                        rW.add(m);
                    }
                    final int replenishradius = plugin.DynamicRegionFile.getConfig().getInt("regions." + key + ".canBreak." + block.getType().toString() + ".replenishRadius");
                    final int replenishtime = plugin.DynamicRegionFile.getConfig().getInt("regions." + key + ".canBreak." + block.getType().toString() + ".replenishTime");
                    Interact.tempMap.put(loc, block.getType());
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            block.setType(rW.get(new Random().nextInt(rW.size())));
                        }
                    }.runTaskLater(plugin, 1);
                    List<Material> filter = new ArrayList<>();
                    for (String s : plugin.DynamicRegionFile.getConfig().getStringList("regions." + key + ".canBreak." + block.getType().toString() + ".replaceOn")){
                        Material mat = Material.matchMaterial(s);
                        filter.add(mat);
                    }
                    List<Material> airs = new ArrayList<>();
                    airs.add(Material.CAVE_AIR);
                    airs.add(Material.AIR);
                    airs.add(Material.VOID_AIR);
                    new BukkitRunnable(){
                        final BoundingBox b = BoundingBox.of(loc1, loc2);
                        @Override
                        public void run(){
                            List<Block> blocks = getNearbyBlocks(loc, replenishradius);
                            for (Block bloc : blocks.toArray(new Block[0])){
                                Material mat = bloc.getType();
                                if (mat.equals(Material.AIR) || mat.equals(Material.CAVE_AIR) || mat.equals(Material.VOID_AIR))
                                    blocks.remove(bloc);
                                if (!(airs.contains(bloc.getRelative(BlockFace.DOWN).getType()) || airs.contains(bloc.getRelative(BlockFace.UP).getType()) || airs.contains(bloc.getRelative(BlockFace.NORTH).getType())
                                || airs.contains(bloc.getRelative(BlockFace.SOUTH).getType()) || airs.contains(bloc.getRelative(BlockFace.EAST).getType())
                                || airs.contains(bloc.getRelative(BlockFace.WEST).getType())))
                                    blocks.remove(bloc);

                                if (!filter.contains(bloc.getType()))
                                    blocks.remove(bloc);
                                if (!b.overlaps(bloc.getBoundingBox()))
                                    blocks.remove(bloc);
                            }
                            Create c = new Create(plugin);
                            if (blocks.size() > 0) {
                                c.getRandomBlock(blocks, filter).setType(ogmat);
                                this.cancel();
                                return;
                            }
                           //Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "no blocks in replenish radius suitible for replenishment, replenising on original block!");
                            block.setType(ogmat);
                            Interact.tempMap.remove(loc);

                        }
                    }.runTaskLater(plugin, replenishtime);
                    return;
                }
                event.setCancelled(true);
            }
        }
    }

 */
    /*
    @EventHandler
    public void cancelPlace(BlockPlaceEvent event){
        if (!plugin.DynamicRegionFile.getConfig().contains("regions"))
            return;
        Player player = event.getPlayer();
        Block block = event.getBlock();
        for (String key : plugin.DynamicRegionFile.getConfig().getConfigurationSection("regions.").getKeys(false)) {
            Location l1 = (Location) plugin.DynamicRegionFile.getConfig().get("regions." + key + ".loc1");
            Location l2 = (Location) plugin.DynamicRegionFile.getConfig().get("regions." + key + ".loc2");
            Location loc1 = l1.clone().add(0.5, 0.5,0.5);
            Location loc2 = l2.clone().add(0.5, 0.5, 0.5);

            BoundingBox bb = null;
            if (loc1 != null) {
                if (loc2 != null) {
                    bb = BoundingBox.of(loc1 , loc2);
                }
            }
            if (bb != null && bb.overlaps(BoundingBox.of(block))) {
                if (!player.isOp()) {
                    if (Interact.editMode.containsKey(key))
                        if (Interact.editMode.get(key)) {
                            if (player.hasPermission("oreGen.regions.editor"))
                                return;
                        }
                }
                if (!block.getLocation().getWorld().getName().equalsIgnoreCase(loc1.getWorld().getName())) {
                    continue;
                }
                player.sendMessage(ChatColor.RED + "You cannot place blocks here!");
                event.setCancelled(true);
                return;
            }
        }
    }

     */

}
