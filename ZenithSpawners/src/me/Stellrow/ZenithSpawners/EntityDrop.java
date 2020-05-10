package me.Stellrow.ZenithSpawners;

import org.bukkit.Material;

import java.util.HashMap;

public class EntityDrop {
    private HashMap<Integer, Material> drops = new HashMap<Integer, Material>();
    public void addDrop(Integer tier,Material drop){
        drops.put(tier,drop);
    }
    public Material returnDrop(Integer tier){
        if(drops.containsKey(tier)){
            return drops.get(tier);
        }
        return null;
    }
}
