package me.stephenminer.oreRegeneration.Regions;

import me.stephenminer.oreRegeneration.OreRegeneration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class DynamicRegion extends Region {
    public HashMap<Location, Material> replaceQueue = new HashMap<>();
    public DynamicRegion(OreRegeneration plugin, String id) {
        super(plugin, id);
    }

    public int getReplenishRadius(Material key) {
        return plugin.DynamicRegionFile.getConfig().getInt(regionPath + ".canBreak." + key.name() + ".replenishRadius");
    }

    public List<Material> getReplaceOn(Material key) {
        List<Material> out = new ArrayList<>();
        for (String s : plugin.DynamicRegionFile.getConfig().getStringList(regionPath + ".canBreak." + key.name() + ".replaceOn")) {
            Material mat = Material.matchMaterial(s);
            out.add(mat);
        }
        return out;
    }

    @Override
    protected void initRegion(){
        Location loc1 = plugin.fromString(plugin.DynamicRegionFile.getConfig().getString("regions." + id + ".pos1"));
        Location loc2 = plugin.fromString(plugin.DynamicRegionFile.getConfig().getString("regions." + id + ".pos2"));
        if (loc1 != null && loc2 != null) {
            Location l1 = loc1.clone().add(0.5, 0.5, 0.5);
            Location l2 = loc2.clone().add(0.5, 0.5, 0.5);
            this.loc1 = loc1;
            this.loc2 = loc2;
            bounds = BoundingBox.of(l1, l2);
            world = loc1.getWorld();
        }
    }
/*
    @Override
    public Location getLocsFromConfig(boolean loc1) {
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

 */

    public void getWhereToReplace(Block origin, Material key) {
        new BukkitRunnable(){
            @Override
            public void run() {
                /*
                List<Material> airs = new ArrayList<>();
                airs.add(Material.CAVE_AIR);
                airs.add(Material.AIR);
                airs.add(Material.VOID_AIR);
                List<Block> blocks = getNearbyBlocks(origin.getLocation(), getReplenishRadius(key));
                List<Material> filter = getReplaceOn(key);
                for (int i = blocks.size() - 1; i >= 0; i--) {
                    Block block = blocks.get(i);
                    if (airs.contains(block.getType()))
                        blocks.remove(i);
                    if (!(airs.contains(block.getRelative(BlockFace.DOWN).getType()) || airs.contains(block.getRelative(BlockFace.UP).getType()) || airs.contains(block.getRelative(BlockFace.NORTH).getType())
                            || airs.contains(block.getRelative(BlockFace.SOUTH).getType()) || airs.contains(block.getRelative(BlockFace.EAST).getType())
                            || airs.contains(block.getRelative(BlockFace.WEST).getType())))
                        blocks.remove(block);
                    if (!filter.contains(block.getType()))
                        blocks.remove(block);
                    else if ()
                    if (!bounds.overlaps(block.getBoundingBox()))
                        blocks.remove(block);
                }

                 */
                List<Block> blocks = getNearbyBlocks(origin.getLocation(), getReplenishRadius(key));
                blocks.remove(origin);
                List<Material> filter = getReplaceOn(key);

                blocks = validBlocks(blocks, filter);
                Block b = origin;
                if (blocks.size() > 0) {
                    b = getRandomBlock(blocks, filter);
                }
                if (b.getType().equals(Material.FARMLAND) && b.getLocation().clone().add(0, 1, 0).getBlock().getType().isAir()) {
                    b = b.getLocation().clone().add(0, 1, 0).getBlock();
                }// else b = origin;
               // replaceQueue.put(b.getLocation(), key);
                replenish(b.getLocation(), key);
                origin.getWorld().playSound(b.getLocation(), Sound.BLOCK_STONE_BREAK, 4, 1);
                origin.removeMetadata("isGen", plugin);
               // replaceQueue.remove(origin.getLocation());
                replenishing.remove(origin.getLocation());
            }
        }.runTaskLater(plugin, getReplenishTime(key));
        /*
        final Block temp = b;
        new BukkitRunnable() {
            @Override
            public void run() {
                replenish(temp.getLocation(), key);
                origin.getWorld().playSound(temp.getLocation(), Sound.BLOCK_STONE_BREAK, 4, 1);
                origin.removeMetadata("isGen", plugin);
                replaceQueue.remove(origin.getLocation());
            }
        }.runTaskLater(plugin, getReplenishTime(key));

         */
    }

    @Override
    public String denyBreak(){
        return plugin.DynamicRegionFile.getConfig().getString(regionPath + ".break-msg");
    }
    @Override
    public String denyPlace(){
        return plugin.DynamicRegionFile.getConfig().getString(regionPath + ".build-msg");
    }
    @Override
    public String fullMessage(){
        return plugin.DynamicRegionFile.getConfig().getString(regionPath + ".full-msg");
    }
    @Override
    public Sound breakSound(){
        String attempt = plugin.DynamicRegionFile.getConfig().getString(regionPath + ".break-sound.name");
        try{
            return Sound.valueOf(attempt);
        }catch (Exception e){
            //plugin.getLogger().log(Level.WARNING, "Attempted to parse sound " + attempt + " for region " + id + ", but sound doesn't exist!");
        }
        return null;
    }
    @Override
    public float soundVol(){
        return (float) plugin.DynamicRegionFile.getConfig().getDouble(regionPath + ".break-sound.volume");
    }
    @Override
    public float soundPitch(){
        return (float) plugin.DynamicRegionFile.getConfig().getDouble(regionPath + ".break-sound.pitch");
    }
    @Override
    public String pickupMsg(String mat){
        if (plugin.DynamicRegionFile.getConfig().contains(regionPath + ".pickup-msg." + mat))
            return plugin.DynamicRegionFile.getConfig().getString(regionPath + ".pickup-msg." + mat);
        return null;
    }

    private List<Block> validBlocks(List<Block> blocks, List<Material> filter){
        List<Material> airs = new ArrayList<>();
        airs.add(Material.AIR);
        airs.add(Material.VOID_AIR);
        airs.add(Material.CAVE_AIR);
        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block block = blocks.get(i);
            if (airs.contains(block.getType()))
                blocks.remove(i);
            if (!(airs.contains(block.getRelative(BlockFace.DOWN).getType()) || airs.contains(block.getRelative(BlockFace.UP).getType()) || airs.contains(block.getRelative(BlockFace.NORTH).getType())
                    || airs.contains(block.getRelative(BlockFace.SOUTH).getType()) || airs.contains(block.getRelative(BlockFace.EAST).getType())
                    || airs.contains(block.getRelative(BlockFace.WEST).getType())))
                blocks.remove(block);
            if (!filter.contains(block.getType()))
                blocks.remove(block);
            else if (block.getType().equals(Material.FARMLAND)){
                if (!block.getLocation().clone().add(0,1,0).getBlock().getType().isAir()) {
                    blocks.remove(block);
                }
            }
            if (!bounds.overlaps(block.getBoundingBox()))
                blocks.remove(block);
        }
        return blocks;
    }

    public void delayReplenishment(Block origin, Block replaceOn, Material mat){
        new BukkitRunnable() {
            @Override
            public void run() {
                replenish(replaceOn.getLocation(), mat);
                origin.getWorld().playSound(replaceOn.getLocation(), Sound.BLOCK_STONE_BREAK, 4, 1);
                origin.removeMetadata("isGen", plugin);
                replaceQueue.remove(origin.getLocation());
            }
        }.runTaskLater(plugin, getReplenishTime(mat));
    }
    
    public void replenish(Location loc, Material mat){
        Block block = loc.getBlock();
        block.setType(toCrop(mat));
        if (block.getBlockData() instanceof Ageable){
            Ageable age = (Ageable) block.getBlockData();
            age.setAge(age.getMaximumAge());
            block.setBlockData(age);
        }
        setProperRotation(block);
    }

    private Material toCrop(Material mat){
        Material out = mat;
        switch (mat){
            case CARROT:
                out = Material.CARROTS;
                break;
            case POTATO:
                out = Material.POTATOES;
                break;
            case BEETROOT_SEEDS:
                out = Material.BEETROOTS;
                break;
            case COCOA_BEANS:
                out = Material.COCOA;
        }
        return out;
    }




    @Override
    public void startReplenishment(Location loc, Material mat) {
        Block block = loc.getBlock();
        getWhereToReplace(block, fixMat(mat));
    }
    private Material fixMat(Material mat){
        Material correct = mat;
        switch (mat){
            case CARROTS:
                correct = Material.CARROT;
                break;
            case POTATOES:
                correct = Material.POTATO;
                break;
            case BEETROOTS:
                correct = Material.BEETROOT_SEEDS;
                break;
            case COCOA:
                correct = Material.COCOA_BEANS;
        }
        return correct;
    }
    @Override
    public boolean getDropOnPlayer(){
        return plugin.DynamicRegionFile.getConfig().getBoolean("regions." + id + ".dropOnPlayer");
    }
    @Override
    public boolean getDropInInventory(){
        return plugin.DynamicRegionFile.getConfig().getBoolean("regions." + id + ".dropInInventory");
    }
    @Override
    public boolean givePlayerXp(){
        return plugin.DynamicRegionFile.getConfig().getBoolean("regions." + id + ".givePlayerXp");
    }

    @Override
    public void delete(){
        if (plugin.DynamicRegionFile.getConfig().contains("regions." + id)){
            plugin.DynamicRegionFile.getConfig().set("regions." + id, null);
            plugin.DynamicRegionFile.saveConfig();
        }
        plugin.regions.remove(this);
    }



    @Override
    public Material getReplaceWith(Material key) {
        if (plugin.DynamicRegionFile.getConfig().contains(regionPath + ".canBreak." + key.name() + ".replaceWith")) {
            List<String> entries = plugin.DynamicRegionFile.getConfig().getStringList(regionPath + ".canBreak." + key.name() + ".replaceWith");
            List<Material> materials = new ArrayList<>();
            for (String str : entries) {
                materials.add(translate(str));
            }
            if (materials.size() > 0)
                return materials.get(random.nextInt(materials.size()));
        }
        return Material.AIR;
    }

    public List<Block> getNearbyBlocks(Location loc, int radius) {
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

    public Block getRandomBlock(List<Block> blocks, List<Material> filter) {
        Random r = new Random();
        Block b = blocks.get(r.nextInt(blocks.size()));
        if (!filter.contains(b.getType()))
            return getRandomBlock(blocks, filter);
        else if (b.getType().equals(Material.FARMLAND)){
            if (b.getLocation().clone().add(0,1,0).getBlock().getType().isAir()){
                b = b.getLocation().clone().add(0,1,0).getBlock();
            }else return getRandomBlock(blocks, filter);
        }
        return b;
    }

    @Override
    public int getReplenishTime(Material key) {
        if (plugin.DynamicRegionFile.getConfig().contains(regionPath + ".canBreak." + key.name() + ".replenishTime"))
            return plugin.DynamicRegionFile.getConfig().getInt(regionPath + ".canBreak." + key.name() + ".replenishTime");
        return 100;
    }

    @Override
    public List<Material> getCanBreak() {
        List<String> strings = new ArrayList<>();
        if (plugin.DynamicRegionFile.getConfig().contains(regionPath + ".canBreak")) {
            for (String key : plugin.DynamicRegionFile.getConfig().getConfigurationSection(regionPath + ".canBreak").getKeys(false)) {
                if (key != null && !key.isEmpty())
                    strings.add(key);
            }
        }
        List<Material> mats = new ArrayList<>();
        if (strings.size() < 1)
            return mats;
        for (String string : strings) {
            try {
                mats.add(translate(string));
            } catch (Exception ignored) {
            }
        }
        return mats;
    }

    @Override
    public Set<Block> getOutline() {
        Location loc1 = getLocsFromConfig(true);
        Location loc2 = getLocsFromConfig(false);
        Set<Block> blockSet = new HashSet<>();
        World world = loc1.getWorld();
        org.bukkit.util.Vector fp = loc1.toVector();
        org.bukkit.util.Vector sp = loc2.toVector();
        org.bukkit.util.Vector max = org.bukkit.util.Vector.getMaximum(fp, sp);
        org.bukkit.util.Vector min = Vector.getMinimum(fp, sp);

        for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
            for (int x = min.getBlockX() - 1; x <= max.getBlockX() + 1; x++) {
                blockSet.add(world.getBlockAt(x, y, min.getBlockZ() - 1));
                blockSet.add(world.getBlockAt(x, y, max.getBlockZ() + 1));
            }
            for (int z = min.getBlockZ() - 1; z <= max.getBlockZ() + 1; z++) {
                blockSet.add(world.getBlockAt(min.getBlockX() - 1, y, z));
                blockSet.add(world.getBlockAt(max.getBlockX() + 1, y, z));
            }
        }
        return blockSet;
    }


}
