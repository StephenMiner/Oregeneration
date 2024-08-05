package me.stephenminer.oreregeneration.Regions;

import me.stephenminer.oreregeneration.OreRegeneration;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Cocoa;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Region {
    public static HashMap<String, Region> regions = new HashMap<>();

    protected BoundingBox bounds;

    protected final OreRegeneration plugin;
    protected final String id;

    protected final String regionPath;
    protected final Random random;

    protected HashMap<Location, Material> replenishing;
    protected boolean editmode;
    protected boolean outline;
    protected boolean actionBar;
    protected boolean dynamic;
    protected World world;

    protected Location loc1;
    protected Location loc2;

    public Region(OreRegeneration plugin, String id) {
        this.plugin = plugin;
        this.id = id;
        regionPath = "regions." + this.id;
        random = new Random();
        replenishing = new HashMap<>();
        editmode = false;
        initRegion();
        regions.put(id, this);
    }

    protected void initRegion() {
        Location loc1 = plugin.fromString(plugin.RegionStorageFile.getConfig().getString("regions." + id + ".pos1"));
        Location loc2 = plugin.fromString(plugin.RegionStorageFile.getConfig().getString("regions." + id + ".pos2"));
        if (loc1 != null && loc2 != null) {
            Location l1 = loc1.clone().add(0.5, 0.5, 0.5);
            Location l2 = loc2.clone().add(0.5, 0.5, 0.5);
            this.loc1 = loc1;
            this.loc2 = loc2;
            bounds = BoundingBox.of(l1, l2);
            world = loc1.getWorld();
        }
    }

    public String getId() {
        return id;
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    public boolean givePlayerXp(){
        return plugin.RegionStorageFile.getConfig().getBoolean("regions." + id + ".givePlayerXp");
    }
    // called in a BlockBreakEvent
    // returns true if break event shouldn't cancel
    public boolean tryBreakEvent(Player player, Block block) {
        Material mat = block.getType();
        if (mat.isAir()) return true;
        List<Material> canBreak = getCanBreak();
        Location loc = block.getLocation();
        if (canBreak.size() > 0 && canBreak.contains(mat)) {
            boolean drop = getDropOnPlayer();
            boolean invDrop = getDropInInventory();
            if (replenishing.containsKey(loc)) {
                if (plugin.RegionStorageFile.getConfig().contains("regions." + id  + ".canBreak." + mat.name()) || plugin.DynamicRegionFile.getConfig().contains("regions." + id + ".canBreak." + mat.name())) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            loc.getBlock().setType(Material.BEDROCK, false);
                        }
                    }.runTaskLater(plugin, 1);
                    return true;
                }
            }
            if (isLocNode(block.getLocation())){
                return true;
            }
            Location dropLoc = loc;
            List<ItemStack> potDrops = potentialDrops(block.getType());
            List<ItemStack> items = new ArrayList<>(block.getDrops(player.getInventory().getItemInMainHand()));
            if (!potDrops.isEmpty()){
                ItemStack item = potDrops.get(ThreadLocalRandom.current().nextInt(potDrops.size()));
                potDrops.clear();
                potDrops.add(item);
                items = potDrops;
            }
            if (blockCrop(block.getType())){
                for (int i = block.getY()+1; i <= block.getY() + 18; i++){
                    Block b = block.getWorld().getBlockAt(block.getX(), i, block.getZ());
                    if (blockCrop(b.getType())){
                        List<ItemStack> pDrops = potentialDrops(b.getType());
                        if (pDrops.isEmpty()) items.addAll(b.getDrops(player.getInventory().getItemInMainHand()));
                        else items.add(pDrops.get(ThreadLocalRandom.current().nextInt(pDrops.size())));
                        b.setType(Material.AIR);
                    }else break;
                   // highestAt
                }
            }
            if (player.getGameMode() != GameMode.CREATIVE) {
                for (int i = items.size() - 1; i >= 0; i--) {
                    if (i - 1 <= -1) continue;
                    ItemStack current = items.get(i);
                    ItemStack next = items.get(i - 1);
                    if (current.getType() == next.getType()) {
                        current.setAmount(current.getAmount() + next.getAmount());
                        items.remove(next);
                    }
                }
                boolean worked = false;
                if (invDrop && !dontDropItems(mat)) {
                    try {
                        for (int i = items.size() - 1; i >= 0; i--) {
                            ItemStack item = items.get(i);
                            if (item.getType().isAir()) continue;
                            HashMap<Integer, ItemStack> remove = player.getInventory().addItem(item);
                            if (!remove.containsValue(item)) {
                                String msg = pickupMsg(item.getType().name());
                                if (msg != null && !msg.isEmpty())
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg.replace("[x]", String.valueOf(item.getAmount()))));
                                items.remove(item);
                            } else {
                                String msg = "";
                                if (fullMessage() != null) {
                                    msg = ChatColor.translateAlternateColorCodes('&', fullMessage());
                                } else msg = ChatColor.RED + "Your inventory is full!";
                                if (!msg.isEmpty()) player.sendTitle(msg, "");
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
                if (!worked) {
                    if (drop) {
                        dropLoc = player.getLocation();
                    }
                    if (!dontDropItems(mat)) {
                        for (ItemStack item : items) {
                            if (item.getType().isAir()) continue;
                            player.getWorld().dropItemNaturally(dropLoc, item);
                        }
                    }
                }
            }
            if (blockCrop(mat) && (blockCrop(loc.clone().add(0,-1,0).getBlock().getType()) || loc.clone().add(0,-1,0).getBlock().getType().isAir())) return true;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!mat.equals(Material.AIR)) {
                        loc.getBlock().setType(getReplaceWith(mat));
                        loc.getBlock().setMetadata("isGen", new FixedMetadataValue(plugin,
                                loc.getWorld().getName() + "/" + loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ()));
                    }
                }
            }.runTaskLater(plugin, 1);
            replenishing.put(loc, mat);
            startReplenishment(loc, mat, block.getState());
            return true;

        }

        return false;
    }

    public boolean isLocNode(Location loc){
        String sLoc = plugin.fromBlockLoc(loc);
        if (plugin.NodeFile.getConfig().contains("nodes")){
            Set<String> nodePositions = plugin.NodeFile.getConfig().getConfigurationSection("nodes").getKeys(false);
            for (String pos : nodePositions){
                if (sLoc.equals(pos)) return true;
            }
        }
        return false;
    }
    public boolean getDropOnPlayer(){
        return plugin.RegionStorageFile.getConfig().getBoolean("regions." + id + ".dropOnPlayer");
    }

    public boolean getDropInInventory(){
        return plugin.RegionStorageFile.getConfig().getBoolean("regions." + id + ".dropInInventory");
    }

    private boolean blockCrop(Material mat){
        boolean blockCrop = false;
        switch(mat){
            case SUGAR_CANE:
            case CACTUS:
            case BAMBOO:
                blockCrop = true;
        }
        return blockCrop;
    }

    public void startReplenishment(Location loc, Material mat, BlockState state) {
        Block block = loc.getBlock();
        new BukkitRunnable() {
            @Override
            public void run() {
                loc.getBlock().setType(getReplenishAs(mat));
                if (block.getBlockData() instanceof Ageable) {
                    Ageable age = (Ageable) block.getBlockData();
                    age.setAge(age.getMaximumAge());
                    block.setBlockData(age);
                }
                /*
                if (state instanceof Skull && loc.getBlock().getState() instanceof Skull){
                    Skull old = (Skull) state;
                    Skull skull = (Skull) loc.getBlock().getState();
                    OfflinePlayer offlinePlayer = old.getOwningPlayer();
                    if (offlinePlayer==null) System.out.println(23213123);
                    else {
                        skull.setOwningPlayer(offlinePlayer);
                        System.out.println(20000);
                        skull.update(true);
                    }

                }

                 */

                setProperRotation(block);
                block.getWorld().playSound(loc, Sound.BLOCK_STONE_BREAK, 4, 1);
                replenishing.remove(loc);
                loc.getBlock().removeMetadata("isGen", plugin);
            }
        }.runTaskLater(plugin, getReplenishTime(mat));

    }


    public boolean isInRegion(Block block) {
        return world.equals(block.getWorld()) && bounds.overlaps(block.getBoundingBox());
    }


    public boolean editMode(Player player) {
        if (player.hasPermission("oreGen.regions.editor"))
            return editmode;
        return false;
    }

    public Material translate(String s) {
        switch (s) {
            case "CARROT":
                return Material.CARROTS;
            case "POTATO":
                return Material.POTATOES;
            case "BEETROOT_SEEDS":
                return Material.BEETROOTS;
            case "COCOA_BEANS":
                return Material.COCOA;
            default:
                return Material.matchMaterial(s);
        }
    }

    /**
     * Message that shows when the region denies the breaking of blocks
     */

    public String denyBreak(){
        return plugin.RegionStorageFile.getConfig().getString(regionPath + ".break-msg");
    }

    /**
     *Message that shows when the region denies the breaking of blocks
     */
    public String denyPlace(){
        return plugin.RegionStorageFile.getConfig().getString(regionPath + ".build-msg");
    }

    /**
     * Returns the message that displays when a player has a full inventory and drop-in-inventory is on
     */
    public String fullMessage(){
        return plugin.RegionStorageFile.getConfig().getString(regionPath + ".full-msg");
    }

    /**
     * Returns the block-break sound for blocks in the region
     */
    public Sound breakSound(){
        String attempt = plugin.RegionStorageFile.getConfig().getString(regionPath + ".break-sound.name");
        try{
            return Sound.valueOf(attempt);
        }catch (Exception e){
            //plugin.getLogger().log(Level.WARNING, "Attempted to parse sound " + attempt + " for region " + id + ", but sound doesn't exist!");
        }
        return null;
    }

    /**
     * Returns the volume of the sound for block breaking
     */
    public float soundVol(){
        return (float) plugin.RegionStorageFile.getConfig().getDouble(regionPath + ".break-sound.volume");
    }

    /**
     * Returns the pitch of the sound for block breaking
     */
    public float soundPitch(){
        return (float) plugin.RegionStorageFile.getConfig().getDouble(regionPath + ".break-sound.pitch");
    }

    /**
     * Returns the pick-up msg for specific material in region
     */
    public String pickupMsg(String mat){
        if (plugin.RegionStorageFile.getConfig().contains(regionPath + ".pickup-msg." + mat))
            return plugin.RegionStorageFile.getConfig().getString(regionPath + ".pickup-msg." + mat);
        return null;
    }



    public List<Material> getCanBreak() {
        List<String> strings = new ArrayList<>();
        if (plugin.RegionStorageFile.getConfig().contains(regionPath + ".canBreak")) {
            for (String key : plugin.RegionStorageFile.getConfig().getConfigurationSection(regionPath + ".canBreak").getKeys(false)) {
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

    public void delete(){
        if (plugin.RegionStorageFile.getConfig().contains("regions." + id)){
            plugin.RegionStorageFile.getConfig().set("regions." + id, null);
            plugin.RegionStorageFile.saveConfig();
        }
        outline = false;
        plugin.regions.remove(this);
    }

    public Material getReplaceWith(Material key) {
        if (plugin.RegionStorageFile.getConfig().contains(regionPath + ".canBreak." + key.name() + ".replaceWith")) {
            List<String> entries = plugin.RegionStorageFile.getConfig().getStringList(regionPath + ".canBreak." + key.name() + ".replaceWith");
            return blockEntryMethod(entries);
        }
        return Material.AIR;
    }

    public Material getReplenishAs(Material key) {
        if (plugin.RegionStorageFile.getConfig().contains(regionPath + ".canBreak." + key.name() + ".replenishAs")) {
            List<String> entries = plugin.RegionStorageFile.getConfig().getStringList(regionPath + ".canBreak." + key.name() + ".replenishAs");
            return blockEntryMethod(entries);
        }
        return key;
    }

    /**
     *
     * @param blockTranslate list of Strings either as Materials or BlockEntries (Preferably BlockEntries
     * @return randomly selected material from list
     */
    protected Material blockEntryMethod(List<String> blockTranslate){
        int passed = 0;
        List<BlockEntry> blocks = new ArrayList<>();
        for (String str : blockTranslate){
            try{
                BlockEntry entry = new BlockEntry(str);
                blocks.add(entry);
                passed++;
            }catch (Exception e){
                plugin.getLogger().warning("Error loading BlockEntry for " + str);
            }
        }
        if (passed < 1) {
           // plugin.getLogger().warning("< 1 Block Entries loaded, attempting to load basic Materials...");
            return failsafe(blockTranslate);
        }
        BlockRoller roller = new BlockRoller(blocks);
        return roller.makeRoll().mat();
    }

    /**
     *
     * @param matTranslate String List containing Material Strings ONLY, only called if no BlockEntries can be loaded
     * @return
     */
    protected Material failsafe(List<String> matTranslate){
        List<Material> materials = new ArrayList<>();
        for (String str : matTranslate) {
            materials.add(translate(str));
        }
        if (materials.size() > 0) {
            if (materials.size() > 1) return materials.get(random.nextInt(materials.size()));
            else return materials.get(0);

        }
        else return Material.AIR;
    }


    public int getReplenishTime(Material key) {
        if (plugin.RegionStorageFile.getConfig().contains(regionPath + ".canBreak." + key.name() + ".replenishTime"))
            return plugin.RegionStorageFile.getConfig().getInt(regionPath + ".canBreak." + key.name() + ".replenishTime");
        return 100;
    }

    public Location getLocsFromConfig(boolean loc1) {
        String locpath = loc1 ? "loc1" : "loc2";
        String name = plugin.RegionStorageFile.getConfig().getString(regionPath + ".world");
        World world;
        try {
            world = Bukkit.getWorld(name);
        } catch (Exception e) {
            world = new WorldCreator(name).createWorld();
        }
        int x = plugin.RegionStorageFile.getConfig().getInt(regionPath + "." + locpath + ".x");
        int y = plugin.RegionStorageFile.getConfig().getInt(regionPath + "." + locpath + ".y");
        int z = plugin.RegionStorageFile.getConfig().getInt(regionPath + "." + locpath + ".z");
        return new Location(world, x, y, z);
    }

    public void setProperRotation(Block block){
        if (block.getBlockData() instanceof Cocoa){
            Cocoa cocoa = (Cocoa) block.getBlockData();;
            if (isWood(block, BlockFace.NORTH)){
                cocoa.setFacing(BlockFace.NORTH);
            }
            if (isWood(block, BlockFace.EAST)){
                cocoa.setFacing(BlockFace.EAST);
            }
            if (isWood(block, BlockFace.SOUTH)){
                cocoa.setFacing(BlockFace.SOUTH);
            }
            if (isWood(block, BlockFace.WEST)){
                cocoa.setFacing(BlockFace.WEST);
            }
            block.setBlockData(cocoa);

        }
    }

    private boolean isWood(Block block, BlockFace face){
        Block relative = block.getRelative(face);
        switch (relative.getType()){
            case JUNGLE_LOG:
            case STRIPPED_JUNGLE_LOG:
            case JUNGLE_WOOD:
            case STRIPPED_JUNGLE_WOOD:
                return true;
        }
        return false;
    }

    public static boolean migrate(OreRegeneration plugin, String id) {
        if (plugin.RegionStorageFile.getConfig().contains("regions." + id)) {
            Location loc1 = plugin.RegionStorageFile.getConfig().getLocation("regions." + id + ".loc1");
            Location loc2 = plugin.RegionStorageFile.getConfig().getLocation("regions." + id + ".loc2");
            if (loc1 != null && loc2 != null) {
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc1", null);
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc2", null);
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".world", loc1.getWorld().getName());
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc1.x", loc1.getBlockX());
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc1.y", loc1.getBlockY());
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc1.z", loc1.getBlockZ());
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc2.x", loc2.getBlockX());
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc2.y", loc2.getBlockY());
                plugin.RegionStorageFile.getConfig().set("regions." + id + ".loc2.z", loc2.getBlockZ());
                plugin.RegionStorageFile.saveConfig();
                plugin.regions.add(new Region(plugin, id));
                return true;
            }
            return false;
        } else if (plugin.DynamicRegionFile.getConfig().contains("regions." + id)) {
            Location loc1 = plugin.DynamicRegionFile.getConfig().getLocation("regions." + id + ".loc1");
            Location loc2 = plugin.DynamicRegionFile.getConfig().getLocation("regions." + id + ".loc2");
            if (loc1 != null && loc2 != null) {
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc1", null);
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc2", null);
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".world", loc1.getWorld().getName());
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc1.x", loc1.getBlockX());
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc1.y", loc1.getBlockY());
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc1.z", loc1.getBlockZ());
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc2.x", loc2.getBlockX());
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc2.y", loc2.getBlockY());
                plugin.DynamicRegionFile.getConfig().set("regions." + id + ".loc2.z", loc2.getBlockZ());
                plugin.DynamicRegionFile.saveConfig();
                plugin.regions.add(new DynamicRegion(plugin, id));
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean dontDropItems(Material mat){
        return plugin.DropsFile.getConfig().getBoolean("regions." + id + "." + mat.name() + ".stop-drops");
    }

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
    public void showBorder(){
        World world = loc1.getWorld();
        Vector fp = loc1.toVector();
        Vector sp = loc2.toVector();
        Vector max = org.bukkit.util.Vector.getMaximum(fp, sp);
        Vector min = Vector.getMinimum(fp, sp);
        Set<Location> locSet = new HashSet<>();
        for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
            for (int x = min.getBlockX() - 1; x <= max.getBlockX() + 1; x++) {
                locSet.add(new Location(world, x, y, min.getBlockZ() - 1));
                locSet.add(new Location(world, x, y, max.getBlockZ() + 1));
            }
            for (int z = min.getBlockZ() - 1; z <= max.getBlockZ() + 1; z++) {
                locSet.add(new Location(world,min.getBlockX() - 1, y, z));
                locSet.add(new Location(world,max.getBlockX() + 1, y, z));
            }
        }
        new BukkitRunnable(){
            @Override
            public void run(){
                if (!outline) this.cancel();
                for (Location loc : locSet){
                    Location l = loc.clone().add(0.5,0.5,0.5);
                    world.spawnParticle(Particle.VILLAGER_HAPPY, l, 0);
                }
            }
        }.runTaskTimer(plugin, 1, 5);

    }

    public List<ItemStack> potentialDrops(Material broken){
        List<ItemStack> items = new ArrayList<>();
        if (plugin.DropsFile.getConfig().contains("regions." + id + "." + broken.name() + ".drops")){
            Set<String> dropIds = plugin.DropsFile.getConfig().getConfigurationSection("regions." + id + "." + broken.name() + ".drops").getKeys(false);
            for (String entry : dropIds){
                ItemStack item = fromEntry(broken, entry);
                if (item != null) items.add(item);
            }
        }
        return items;
    }

    public ItemStack fromEntry(Material mat, String entry){
        if (plugin.DropsFile.getConfig().contains("regions." + id + "." + mat.name() + ".drops." + entry)){
            String path = "regions." + id + "." + mat.name() + ".drops." + entry;
            Material itemMat = Material.matchMaterial(entry);
            ItemStack item = new ItemStack(itemMat);
            ItemMeta meta = item.getItemMeta();
            if (plugin.DropsFile.getConfig().contains(path + ".name")){
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.DropsFile.getConfig().getString(path + ".name")));
            }
            List<String> lore = new ArrayList<>();
            for (String content : plugin.DropsFile.getConfig().getStringList(path + ".lore")){
                lore.add(ChatColor.translateAlternateColorCodes('&', content));
            }
            meta.setLore(lore);
            int min = 1;
            int max = 1;
            if (plugin.DropsFile.getConfig().contains(path + ".min-drop")) min = plugin.DropsFile.getConfig().getInt(path + ".min-drop");
            if (plugin.DropsFile.getConfig().contains(path + ".max-drop")) max = plugin.DropsFile.getConfig().getInt(path + ".max-drop");
            item.setAmount(ThreadLocalRandom.current().nextInt(min, max+1));
            item.setItemMeta(meta);
            return item;
        }
        return null;
    }


    public HashMap<Location, Material> getReplenishing() {
        return replenishing;
    }

    public boolean editmodeOn() {
        return editmode;
    }

    public void setEditmode(boolean editmode) {
        this.editmode = editmode;
    }

    public boolean outlining(){ return outline; }
    public void setOutlining(boolean outline){ this.outline = outline;}

    public World getWorld(){
        return world;
    }

    public boolean dynamic(){ return dynamic; }


}
