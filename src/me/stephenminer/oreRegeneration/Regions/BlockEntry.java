package me.stephenminer.oreRegeneration.Regions;

import org.bukkit.Material;
import org.bukkit.Raid;

import java.util.Random;

public class BlockEntry {
    private final double weight;
    private final Material mat;


    public BlockEntry(Material mat, double weight){
        this.mat = mat;
        this.weight = weight;
    }



    @Override
    public String toString(){
        return mat.name() + "," + weight;
    }

    public static BlockEntry fromString(String str){
        String[] split = str.split(",");
        Material mat = Material.matchMaterial(split[0]);
        double weight = 1;
        if (split.length > 1)
         weight = Integer.parseInt(split[1]);
        return new BlockEntry(mat, weight);
    }
}
