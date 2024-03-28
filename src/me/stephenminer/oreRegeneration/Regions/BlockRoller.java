package me.stephenminer.oreRegeneration.Regions;

import jdk.nashorn.internal.ir.Block;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for selecting a random block provided a list of BlockEntry
 */
public class BlockRoller {
    private List<BlockEntry> entries;
    private HashMap<Integer, List<BlockEntry>> byChance;
    private Random random;

    private int max;
    public BlockRoller(List<BlockEntry> entries){
        this.entries = entries;
        random = new Random();
        init();
    }


    public BlockEntry makeRoll(){
        int roll = random.nextInt(max);
        List<BlockEntry> valid = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++){
            BlockEntry entry = entries.get(i);
            if (roll <= entry.weight()){
                if (byChance.containsKey(entry.weight())){
                    valid = byChance.get(entry.weight());
                    break;
                }
            }
        }
        if (valid.size() < 1) return new BlockEntry(Material.AIR,0);
        return valid.get(random.nextInt(valid.size()));
    }
    private void init(){
        sortList();
        byChance = byChance();
        max = findBounds();
    }

    private void sortList(){
        entries.sort(Comparator.comparingInt(BlockEntry::weight));
    }


    private HashMap<Integer, List<BlockEntry>> byChance(){
        HashMap<Integer, List<BlockEntry>> byChance = new HashMap<>();
        for (BlockEntry entry : entries){
            if (byChance.containsKey(entry.weight())){
                List<BlockEntry> input = byChance.get(entry.weight());
                input.add(entry);
                byChance.put(entry.weight(),input);
            }else{
                List<BlockEntry> input = new ArrayList<>();
                input.add(entry);
                byChance.put(entry.weight(), input);
            }
        }
        return byChance;
    }


    private int findBounds(){
        return entries.size() > 0 ? entries.stream().mapToInt(BlockEntry::weight).max().getAsInt() : 1;
    }
}
