package me.stephenminer.oreregeneration.Nodes;
import me.stephenminer.oreregeneration.OreRegeneration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class OreNodes implements Listener {
    private OreRegeneration plugin;

    public OreNodes(OreRegeneration plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void migrationEvent(BlockBreakEvent event){
        Location loc = event.getBlock().getLocation();
        long key = OreRegeneration.getBlockKey(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        if (plugin.NodeFile.getConfig().contains("Nodes." + key)){
            String uuid = plugin.NodeFile.getConfig().getString("Nodes." + key + ".Placer");
            String mat = plugin.NodeFile.getConfig().getString("Nodes." + key + ".Block");
            plugin.NodeFile.getConfig().set("Nodes." + key, null);
            plugin.NodeFile.getConfig().set("nodes." + plugin.fromBlockLoc(loc) + ".placer", uuid);
            plugin.NodeFile.getConfig().set("nodes." + plugin.fromBlockLoc(loc) + ".mat", mat);
            plugin.NodeFile.saveConfig();
            event.getPlayer().sendMessage(ChatColor.GREEN + "Your node has been migrated and you can now break as usual!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        ItemStack hand = event.getItemInHand();
        Location loc = block.getLocation();
        if (hand.getItemMeta() == null || !hand.hasItemMeta())
            return;
        if (hand.getItemMeta().getDisplayName().contains(ChatColor.BLUE + " Generator")){
            Node node = new Node(plugin, player.getUniqueId(), loc, block.getType());
            node.save();
            player.sendMessage("Node Placed!");
            plugin.NodeFile.saveConfig();
        }
    }


    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        Location loc = block.getLocation();
        for (Node node : Node.nodes){
            boolean b = node.tryBreak(event.getPlayer(), loc);
            if (b){
                event.setDropItems(false);
            }
        }
    }
    public ItemStack yes(){
        ItemStack item = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Yes");
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack no(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "No");
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createGen(String s){
        ItemStack item = new ItemStack(Material.matchMaterial(s));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(s.toLowerCase() + ChatColor.BLUE + " Generator");
        item.setItemMeta(meta);
        return item;
    }
    HashMap<UUID, Location> removeQue = new HashMap<UUID, Location>();

    @EventHandler
    public void removeNode(PlayerInteractEvent event){
        if (!event.hasBlock())
            return;
        Location loc = event.getClickedBlock().getLocation();
        Player player = event.getPlayer();

        if (event.isCancelled())
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        for (Node node : Node.nodes){
            if (node.onLoc(loc)){
                if (node.getOwner().equals(player.getUniqueId()) || player.hasPermission("oreGen.editor.noderemover")){
                    Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Remove Node?");
                    inv.setItem(3, yes());
                    inv.setItem(6, no());
                    player.openInventory(inv);
                    removeQue.put(player.getUniqueId(), loc);
                }
            }
        }

    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if (!event.getView().getTitle().contains("Remove Node?"))
            return;
        Player player = (Player) event.getWhoClicked();
        World world = player.getWorld();
        if (removeQue.containsKey(player.getUniqueId())){
            Location loc = removeQue.get(player.getUniqueId());
            if (event.getCurrentItem() == (null))
                return;
            event.setCancelled(true);
            if (event.getCurrentItem().isSimilar(yes())){
                for (Node node : Node.nodes){
                    if (node.onLoc(loc)){
                        if (loc.getBlock().getType().equals(Material.BEDROCK)){
                            player.sendMessage(ChatColor.RED + "You cannot remove the node while it is regenerating");
                            return;
                        }
                        world.dropItemNaturally(loc, createGen(node.getType().name()));
                        loc.getBlock().setType(Material.AIR);
                        node.remove();
                        player.sendMessage(ChatColor.GREEN + "Node has been removed");
                        removeQue.remove(player.getUniqueId());
                        player.closeInventory();
                        plugin.NodeFile.saveConfig();
                        plugin.NodeFile.reloadConfig();
                        return;
                    }
                }
            }
            if (event.getCurrentItem().isSimilar(no())){
                player.closeInventory();
                return;
            }
        }
    }
}
