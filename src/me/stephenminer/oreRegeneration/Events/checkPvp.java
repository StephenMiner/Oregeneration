package me.stephenminer.oreRegeneration.Events;

import me.stephenminer.oreRegeneration.OreRegeneration;
import me.stephenminer.oreRegeneration.Regions.DynamicRegion;
import me.stephenminer.oreRegeneration.Regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.BoundingBox;

public class checkPvp implements Listener {
    private OreRegeneration plugin;
    public checkPvp(OreRegeneration plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void canPvp(EntityDamageByEntityEvent event){
        Entity hit = event.getEntity();
        Entity attacker = event.getDamager();
        if (!(hit instanceof Player))
            return;
        if (!(attacker instanceof Player))
            return;
        Player pHit = (Player) hit;
        if (plugin.regions.size() > 0)
            for (Region region : plugin.regions){
                if (hit.getBoundingBox().overlaps(region.getBounds())){
                    if (region instanceof DynamicRegion){
                        if (plugin.DynamicRegionFile.getConfig().contains("regions." + region.getId() + ".pvp")) {
                            if (plugin.DynamicRegionFile.getConfig().getBoolean("regions." + region.getId() + ".pvp"))
                                return;
                        }
                    }else {
                        if (plugin.RegionStorageFile.getConfig().contains("regions." + region.getId() + ".pvp")) {
                            if (plugin.RegionStorageFile.getConfig().getBoolean("regions." + region.getId() + ".pvp"))
                                return;
                        }
                    }
                    event.setCancelled(true);
                    attacker.sendMessage(ChatColor.RED + "You cannot attack players in " + region.getId() + "!");
                    return;
                }
            }
    }
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER)) {
            return;
        }
        if (plugin.regions.size() > 0)
            for (Region region : plugin.regions) {
                if (event.getEntity().getWorld().equals(region.getWorld()) && region.getBounds().overlaps(event.getEntity().getBoundingBox())) {
                    if (region instanceof DynamicRegion) {
                        event.setCancelled(!plugin.DynamicRegionFile.getConfig().getBoolean("regions." + region.getId() + ".mobs-spawn"));
                    } else {
                        event.setCancelled(!plugin.RegionStorageFile.getConfig().getBoolean("regions." + region.getId() + ".mobs-spawn"));
                        if (plugin.DynamicRegionFile.getConfig().contains("regions." + region.getId() + ".mobs-spawn")) {
                            if (!plugin.DynamicRegionFile.getConfig().getBoolean("regions." + region.getId() + ".mobs-spawn")) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
    }
}
