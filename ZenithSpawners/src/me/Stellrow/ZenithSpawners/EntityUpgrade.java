package me.Stellrow.ZenithSpawners;

import java.util.HashMap;

public class EntityUpgrade {
    private HashMap<Integer,Integer> prices = new HashMap<Integer, Integer>();
    public void addPrice(Integer tier,Integer price){
        prices.put(tier,price);
    }
    public int returnNextTierPrice(Integer currentTier){
        if(prices.containsKey(currentTier+1)){
            return prices.get(currentTier+1);
        }
        return 0;
    }
}
