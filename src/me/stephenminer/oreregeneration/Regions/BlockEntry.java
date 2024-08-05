package me.stephenminer.oreregeneration.Regions;

import org.bukkit.Material;
import org.bukkit.Raid;

public class BlockEntry {
    /**
     * Some integer between 0-1000
     */
    private int weight;
    private Material mat;


    public BlockEntry(Material mat, int weight){
        this.mat = mat;
        this.weight = weight;
    }

    public BlockEntry(String str){
        String[] split = str.split(",");
        Material mat = Material.matchMaterial(split[0]);
        int weight = 1;
        if (split.length > 1)
            weight = Integer.parseInt(split[1]);
        this.mat = mat;
        this.weight = weight;
    }



    @Override
    public String toString(){
        return mat.name() + "," + weight;
    }

    public Material mat(){ return mat; }
    public int weight(){ return weight; }

    public void setMat(Material mat){ this.mat = mat; }
    public void setWeight(int weight){ this.weight = weight; }
}
